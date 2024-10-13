package org.jahdoo.entities.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;
import org.jahdoo.entities.EternalWizard;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class AttackNearbyMonsters<T extends LivingEntity> extends TargetGoal {

    private static final int ATTACK_RANGE = 20;
    protected final Class<T> targetType;
    protected final int randomInterval;
    @Nullable
    protected LivingEntity target;
    /** This filter is applied to the Entity search. Only matching entities will be targeted. */
    protected TargetingConditions targetConditions;

    public AttackNearbyMonsters(Mob pMob, Class<T> pTargetType, boolean pMustSee) {
        this(pMob, pTargetType, 2, pMustSee, false, null);
    }

    public AttackNearbyMonsters(Mob pMob, Class<T> pTargetType, int pRandomInterval, boolean pMustSee, boolean pMustReach, @Nullable Predicate<LivingEntity> pTargetPredicate) {
        super(pMob, pMustSee, pMustReach);
        this.targetType = pTargetType;
        this.randomInterval = reducedTickDelay(pRandomInterval);
        this.setFlags(EnumSet.of(Flag.TARGET));
        this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(pTargetPredicate);
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    public boolean canUse() {
        if(!(this.mob instanceof EternalWizard eternalWizard)) return false;

        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        } else {
            this.findTarget();
            return this.target != null && eternalWizard.getMode() && !(this.target instanceof EternalWizard);
        }
    }


    protected AABB getTargetSearchArea(double pTargetDistance) {
        return this.mob.getBoundingBox().inflate(pTargetDistance, 5, pTargetDistance);
    }


    boolean filterValidTarget(LivingEntity entity){
        boolean isNeutralHostile = entity instanceof NeutralMob neutralMob && neutralMob.getTarget() == this.mob;
        boolean isAgroEntity = entity instanceof Monster;
        return isNeutralHostile || isAgroEntity;
    }

    protected void findTarget() {
        this.target = this.mob.level().getNearestEntity(
                this.mob.level().getEntitiesOfClass(
                    this.targetType,
                    this.getTargetSearchArea(ATTACK_RANGE), this::filterValidTarget),
                    this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ()
            );
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void start() {
        this.mob.setTarget(this.target);
        super.start();
    }

    public void setTarget(@Nullable LivingEntity pTarget) {
        this.target = pTarget;
    }

}
