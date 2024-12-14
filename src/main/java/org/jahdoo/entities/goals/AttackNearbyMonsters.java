package org.jahdoo.entities.goals;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jahdoo.entities.EternalWizard;
import org.jahdoo.entities.TamableEntity;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class AttackNearbyMonsters<T extends LivingEntity> extends TargetGoal {

    protected final Class<T> targetType;
    protected final int randomInterval;
    @Nullable
    protected LivingEntity target;
    protected TargetingConditions targetConditions;
    protected float trackDistance;

    public AttackNearbyMonsters(Mob mob, Class<T> targetType, boolean mustSee, int randomInterval, int trackDistance) {
        this(mob, targetType, randomInterval, mustSee, false, null);
        this.trackDistance = trackDistance;
    }

    public AttackNearbyMonsters(Mob mob, Class<T> targetType, int randomInterval, boolean mustSee, boolean mustReach, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, mustSee, mustReach);
        this.targetType = targetType;
        this.randomInterval = reducedTickDelay(randomInterval);
        this.setFlags(EnumSet.of(Flag.TARGET));
        this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(targetPredicate);
    }

    public boolean canUse() {
        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        } else {
            this.findTarget();
            return this.target != null;
        }
    }

    protected AABB getTargetSearchArea(double targetDistance) {
        return this.mob.getBoundingBox().inflate(targetDistance, trackDistance, targetDistance);
    }

    protected void findTarget() {
        if (this.targetType != Player.class && this.targetType != ServerPlayer.class) {
            this.target = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), (p_148152_) -> true), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        } else {
            this.target = this.mob.level().getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        }
    }

    public void start() {
        if(mob instanceof TamableEntity tamableEntity){
            if (withoutOwner(tamableEntity)) return;
            wizardBehaviour(tamableEntity);
        }
        super.start();
    }

    private boolean withoutOwner(TamableEntity tamableEntity) {
        if(tamableEntity.getOwner() == null){
            if(target instanceof Player){
                this.mob.setTarget(this.target);
            }
        }
        return false;
    }

    private void wizardBehaviour(TamableEntity tamableEntity) {
        var isTargetFriend = this.target instanceof TamableEntity tamable1 && tamable1.getOwner() == tamableEntity.getOwner();
        if(!isTargetFriend){
            if (mob instanceof EternalWizard wizard) {
                if (wizard.getMode()) {
                    if (wizard.getOwner() != this.target) {
                        this.mob.setTarget(this.target);
                    }
                }
            } else {
                if (tamableEntity.getOwner() != this.target) {
                    this.mob.setTarget(this.target);
                }
            }
        }
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
    }

}
