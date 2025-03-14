package org.jahdoo.common.entities.goals;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jahdoo.common.entities.eternal_wizard.EternalWizard;
import org.jahdoo.common.entities.ITamableEntity;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class AttackNearbyMonsters<T extends LivingEntity> extends TargetGoal {

    @Nullable
    protected LivingEntity target;
    protected TargetingConditions targetConditions;
    protected float trackDistance;
    protected final Class<T> targetType;
    protected final int randomInterval;

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

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
    }

    protected AABB getTargetSearchArea() {
        return this.mob.getBoundingBox().inflate(60, 4.0F, 60);
    }

    public void start() {
        if(mob instanceof ITamableEntity tamableEntity){
            handleTargeting(tamableEntity);
        }
        super.start();
    }

    public boolean canUse() {
        if (this.mob.getRandom().nextInt(4) != 0) {
            return false;
        } else {
            this.findTarget();
            return this.target != null;
        }
    }

    protected void findTarget() {
        if (this.targetType != Player.class && this.targetType != ServerPlayer.class) {
            this.target = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(), (t) -> true), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        } else {
            this.target = this.mob.level().getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        }
    }

    private void handleTargeting(ITamableEntity tamableEntity) {
        if(mob.getTarget() == null){
            if (target == null) return;

            var isTargetFriend = this.target instanceof ITamableEntity tamableTarget && tamableTarget.getOwner() == tamableEntity.getOwner();
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

}
