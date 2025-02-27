package org.jahdoo.common.entities.goals;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.AABB;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.common.entities.eternal_wizard.EternalWizard;
import org.jahdoo.common.entities.TamableEntity;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class GenericHurtByTargetGoal extends TargetGoal {
    private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
    private int timestamp;

    public GenericHurtByTargetGoal(PathfinderMob pMob) {
        super(pMob, true);
        this.setFlags(EnumSet.of(Flag.TARGET));
    }

    public boolean canUse() {
        int i = this.mob.getLastHurtByMobTimestamp();
        LivingEntity livingentity = this.mob.getLastHurtByMob();
        if(livingentity == null || livingentity.isAlliedTo(mob))
            return false;
        if (i != this.timestamp) {
            if (livingentity.getType() == EntityType.PLAYER && this.mob.level().getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                return false;
            } else {
                if(mob instanceof TamableEntity tamableEntity) if(!DefaultEntityBehaviour.canDamageEntity(livingentity, tamableEntity.getOwner())) return false;

                return this.canAttack(livingentity, HURT_BY_TARGETING) &&  !(livingentity instanceof EternalWizard);
            }
        } else {
            return false;
        }
    }

    public void start() {
        this.mob.setTarget(this.mob.getLastHurtByMob());
        this.mob.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_TARGET, this.mob.getLastHurtByMob(), 200L);

        this.targetMob = this.mob.getTarget();
        this.timestamp = this.mob.getLastHurtByMobTimestamp();
        this.unseenMemoryTicks = 300;
        super.start();
    }

    protected void alertOthers() {
        double d0 = this.getFollowDistance();
        AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 20.0D, d0);
        List<? extends Mob> list = this.mob.level().getEntitiesOfClass(this.mob.getClass(), aabb, EntitySelector.NO_SPECTATORS);
        Iterator<? extends Mob> iterator = list.iterator();

        while(true) {
            Mob mob;
            while(true) {
                if (!iterator.hasNext()) return;
                mob = iterator.next();

                var isntMob = this.mob != mob;
                var hasTarget = mob.getTarget() == null;
                var isTamedEntity =
                      !(this.mob instanceof TamableAnimal tamedAnimal) ||
                      (tamedAnimal.getOwner() == ((TamableAnimal) mob).getOwner());
                var hasAllie = !mob.isAlliedTo(this.mob.getLastHurtByMob());

                if (isntMob && hasTarget && isTamedEntity && hasAllie) {
                    boolean flag = false;
                    if (!flag) break;
                }
            }

            this.alertOther(mob, this.mob.getLastHurtByMob());
        }
    }

    protected void alertOther(Mob pMob, LivingEntity pTarget) {
        pMob.setTarget(pTarget);
    }
}