package org.jahdoo.entities.goals;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jahdoo.entities.living.EternalWizard;
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
        if (this.mob.getRandom().nextInt(4) != 0) {
            return false;
        } else {
            this.findTarget();
            return this.target != null;
        }
    }

    protected AABB getTargetSearchArea() {
        return this.mob.getBoundingBox().inflate(60, 4.0F, 60);
    }

    protected void findTarget() {
        if (this.targetType != Player.class && this.targetType != ServerPlayer.class) {
            this.target = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(), (t) -> true), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        } else {
            this.target = this.mob.level().getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        }
    }

    public void start() {
        if(mob instanceof TamableEntity tamableEntity){
            handleTargeting(tamableEntity);
        }
        super.start();
    }

    private void handleTargeting(TamableEntity tamableEntity) {
        if(mob.getTarget() == null){
            if (target == null) return;

            var isTargetFriend = this.target instanceof TamableEntity tamableTarget && tamableTarget.getOwner() == tamableEntity.getOwner();
            var isOwner = this.target.equals(tamableEntity.getOwner());

            if (tamableEntity.getOwner() == null) {
                if (this.target instanceof Player) this.mob.setTarget(this.target);
            } else if (!isTargetFriend) {
                if (!isOwner) {
                    if (mob instanceof EternalWizard wizard) {
                        if (wizard.getMode()) this.mob.setTarget(this.target);
                    } else {
                        this.mob.setTarget(this.target);
                    }
                }
            }
        }
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
    }

}
