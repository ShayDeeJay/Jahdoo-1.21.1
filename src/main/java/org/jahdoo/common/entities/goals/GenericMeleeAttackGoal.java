package org.jahdoo.common.entities.goals;

import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import org.jahdoo.common.entities.ancient_golem.AncientGolem;
import org.jahdoo.common.entities.explosive_barrel.ExplosiveBarrel;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.utils.DamageUtils;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import java.util.EnumSet;

import static net.minecraft.world.entity.EntitySelector.NO_CREATIVE_OR_SPECTATOR;

public class GenericMeleeAttackGoal extends Goal {
    protected final PathfinderMob mob;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;
    private Path path;
    private double pathTargetX;
    private double pathTargetY;
    private double pathTargetZ;
    private int ticksUntilNextPathRecalculation;
    private int ticksUntilNextAttack;
    private long lastCanUseCheck;
    private int failedPathFindingPenalty = 0;
    private final boolean canPenalize = false;

    public GenericMeleeAttackGoal(PathfinderMob mob, double speedModifier, boolean followingTargetEvenIfNotSeen) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(70);
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    protected boolean canPerformAttack(LivingEntity entity) {
        return this.isTimeToAttack() &&
              this.mob.isWithinMeleeAttackRange(entity) &&
              this.mob.getSensing().hasLineOfSight(entity);
    }

    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
    }

    public void stop() {
        LivingEntity livingentity = this.mob.getTarget();
        if (!NO_CREATIVE_OR_SPECTATOR.test(livingentity)) {
            this.mob.setTarget(null);
        }
        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
    }

    public boolean canContinueToUse() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else if (!this.followingTargetEvenIfNotSeen) {
            return !this.mob.getNavigation().isDone();
        } else {
            return this.mob.isWithinRestriction(livingentity.blockPosition()) && (!(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player) livingentity).isCreative());
        }
    }

    protected void checkAndPerformAttack(LivingEntity target) {
        if (this.canPerformAttack(target)) {
            if(this.mob instanceof AncientGolem ancientGolem){
                if(ancientGolem.level() instanceof ServerLevel serverLevel){
                    serverLevel.getChunkSource().broadcast(this.mob, new ClientboundEntityEventPacket(this.mob, isSwarmed(ancientGolem) ? (byte)5 : 4));
                    ancientGolem.triggerDamage = true;
                }
                this.resetAttackCooldown();
            }
        }
    }

    public boolean canUse() {
        var i = this.mob.level().getGameTime();
        if (i - this.lastCanUseCheck < 20L) {
            return false;
        } else {
            this.lastCanUseCheck = i;
            LivingEntity livingentity = this.mob.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else if (this.canPenalize) {
                if (--this.ticksUntilNextPathRecalculation <= 0) {
                    this.path = this.mob.getNavigation().createPath(livingentity, 0);
                    this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                    return this.path != null;
                } else {
                    return true;
                }
            } else {
                this.path = this.mob.getNavigation().createPath(livingentity, 0);
                return this.path != null || this.mob.isWithinMeleeAttackRange(livingentity);
            }
        }
    }

    public void tick() {
        var livingentity = this.mob.getTarget();
        if (livingentity != null) {
            this.mob.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);

            var needsVisual = this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(livingentity);
            var hasDistance = livingentity.distanceToSqr(this.pathTargetX, this.pathTargetY, this.pathTargetZ) >= (double) 1.0F || this.mob.getRandom().nextFloat() < 0.05F;
            var hasPath = this.pathTargetX == (double) 0.0F && this.pathTargetY == (double) 0.0F && this.pathTargetZ == (double) 0.0F;
            var canRePath = this.ticksUntilNextPathRecalculation <= 0;

            if (needsVisual && canRePath && (hasPath || hasDistance)) {
                this.pathTargetX = livingentity.getX();
                this.pathTargetY = livingentity.getY();
                this.pathTargetZ = livingentity.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                double d0 = this.mob.distanceToSqr(livingentity);
                if (this.canPenalize) {
                    this.ticksUntilNextPathRecalculation += this.failedPathFindingPenalty;
                    if (this.mob.getNavigation().getPath() != null) {
                        Node finalPathPoint = this.mob.getNavigation().getPath().getEndNode();
                        if (finalPathPoint != null && livingentity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < (double)1.0F) {
                            this.failedPathFindingPenalty = 0;
                        } else {
                            this.failedPathFindingPenalty += 10;
                        }
                    } else {
                        this.failedPathFindingPenalty += 10;
                    }
                }

                if (d0 > (double)1024.0F) {
                    this.ticksUntilNextPathRecalculation += 10;
                } else if (d0 > (double)256.0F) {
                    this.ticksUntilNextPathRecalculation += 5;
                }

                if (!this.mob.getNavigation().moveTo(livingentity, this.speedModifier)) {
                    this.ticksUntilNextPathRecalculation += 15;
                }

                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
            }

            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            if(this.mob instanceof AncientGolem ancientGolem){
                ancientGolem.particle();
                if(this.isTimeToAttack()){
                    ancientGolem.runningParticle();
                }
                if(ancientGolem.triggerDamage) {
                    ancientGolem.damageDelay++;
                    var target = ancientGolem.getTarget();
                    var damage = ancientGolem.damage;

                    if(isSwarmed(ancientGolem)){
                        if(ancientGolem.damageDelay == 6){
                            DamageUtils.damageWithJahdoo(target, this.mob, damage, ElementReg.vitality().damageTypeResourceKey());

                            var chance = ancientGolem.effectChance;
                            var strength = ancientGolem.effectStrength;
                            var duration = ancientGolem.effectDuration;

                            ExplosiveBarrel.novaExplosion(ancientGolem, ancientGolem.element(), damage, duration, strength, 4, chance, ancientGolem.position());

                            attackSound(ancientGolem);
                            ancientGolem.triggerDamage = false;
                            ancientGolem.damageDelay = 0;
                        }
                    } else {
                        //Add Heal
                        DamageUtils.damageWithJahdoo(target, this.mob, damage, ElementReg.vitality().damageTypeResourceKey());
                        attackSound(ancientGolem);
                        ancientGolem.triggerDamage = false;
                        ancientGolem.damageDelay = 0;
                    }
                }
            }

            this.checkAndPerformAttack(livingentity);

        }

    }

    private static void attackSound(AncientGolem ancientGolem) {
        SoundHelpers.getSoundWithPosition(ancientGolem.level(), ancientGolem.position(), SoundEvents.VAULT_PLACE, SoundSource.NEUTRAL, 2, 0.4f);
        SoundHelpers.getSoundWithPosition(ancientGolem.level(), ancientGolem.position(), SoundEvents.IRON_GOLEM_STEP, SoundSource.NEUTRAL, 2, 0.6f);
        SoundHelpers.getSoundWithPosition(ancientGolem.level(), ancientGolem.position(), SoundReg.IMPACT.get(), SoundSource.NEUTRAL, 0.8F, 0.1f);
    }

    private static boolean isSwarmed(AncientGolem ancientGolem) {
        return ancientGolem
            .level()
            .getEntities(null, ancientGolem.getBoundingBox().inflate(2))
            .stream().filter(Mob.class::isInstance).toList().size() > 5;
    }
}
