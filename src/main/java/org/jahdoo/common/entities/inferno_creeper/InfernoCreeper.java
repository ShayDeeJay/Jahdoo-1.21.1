package org.jahdoo.common.entities.inferno_creeper;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.EntityReg;

import java.util.List;

import static net.minecraft.sounds.SoundEvents.FIRECHARGE_USE;
import static net.minecraft.sounds.SoundEvents.FIRE_AMBIENT;
import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.utils.DamageUtils.damageWithJahdoo;
import static org.jahdoo.ascension.utils.PositionFinders.getOuterRingOfRadiusRandom;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleStore.*;
import static org.jahdoo.common.registers.SoundReg.MAGIC_EXPLOSION;

public class InfernoCreeper extends Creeper {

    private int oldSwell;
    private int swell;
    private final int maxSwell = 30;
    private final double novaMaxSize = 10;

    public InfernoCreeper(EntityType<? extends InfernoCreeper> entityType, Level level) {
        super(entityType, level);
    }

    public InfernoCreeper(Level level) {
        super(EntityReg.INFERNO_CREEPER.get(), level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.ATTACK_DAMAGE, 25F)
            .add(Attributes.MOVEMENT_SPEED, 0.25F);
    }

    public void tick() {
        if (this.isAlive()) {
            this.oldSwell = this.swell;
            if (this.isIgnited()) {
                this.setSwellDir(1);
            }

            int i = this.getSwellDir();
            if (i > 0 && this.swell == 0) {
                this.playSound(SoundEvents.CREEPER_PRIMED, 1.0F, 0.5F);
                this.gameEvent(GameEvent.PRIME_FUSE);
            }

            this.swell += i;
            if (this.swell < 0) {
                this.swell = 0;
            }

            if (this.swell >= this.maxSwell) {
                this.swell = this.maxSwell;
                this.explodeCreeper();
            }
        }

        super.tick();
    }

    public float getSwelling(float partialTicks) {
        return Mth.lerp(partialTicks, (float)this.oldSwell, (float)this.swell) / (float)(this.maxSwell - 2);
    }

    public AbstractElement getElementType() {
        return ElementReg.inferno();
    }

    private void explodeCreeper() {
        if (!this.level().isClientSide) {
            this.dead = true;
            onHitBehaviour();
            novaDamageBehaviour();
            this.triggerOnDeathMobEffects(RemovalReason.KILLED);
            this.discard();
        }
    }

    private void novaDamageBehaviour(){
        this.level().getNearbyEntities(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            this,
            this.getBoundingBox().inflate(novaMaxSize, 4, novaMaxSize)
        ).forEach(
            livingEntity -> {
                if(livingEntity instanceof ITamableEntity t && t.getOwner() != null || livingEntity instanceof Player){
                    var damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
                    damageWithJahdoo(livingEntity, this, damage != null ? damage.getValue() : 25);
                    livingEntity.addEffect(new JahdooMobEffect(EffectReg.INFERNO_EFFECT, 100, 1));
                }
            }
        );
    }

    private void onHitBehaviour() {
        getOuterRingOfRadiusRandom(this.position(), 1.5, this.novaMaxSize * 50, this::setParticleNova);
        var speed = (float) this.novaMaxSize / 10;
        var maxPart = (int) this.novaMaxSize / 2;

        if(this.level() instanceof ServerLevel serverLevel){
            ParticleHandlers.particleBurst(serverLevel, this.position(), maxPart,
                ParticleHandlers.genericParticle(MAGIC_PARTICLE, this.getElementType(), 40, 3f)
                ,0,0,0,speed
            );

            ParticleHandlers.particleBurst(serverLevel, this.position(), maxPart,
                ParticleHandlers.genericParticle(MAGIC_PARTICLE, 40, 3f, rgbToInt(61,61,61), rgbToInt(218,218,218))
                ,0,0,0,speed
            );
        }

        this.playSound(MAGIC_EXPLOSION.get(), 2F, 1.6F);
        this.playSound(FIRECHARGE_USE, 1F, 0.6F);
        this.playSound(FIRE_AMBIENT);
    }

    private void setParticleNova(Vec3 worldPosition){
        var positionScrambler = worldPosition.offsetRandom(RandomSource.create(), 1F);
        var directions = positionScrambler.subtract(this.position()).normalize();
        var lifetime = (int) this.novaMaxSize;
        var size = Helpers.Random.nextDouble(8, 12);
        var bakedParticle = bakedParticle(this.getElementType().id(), lifetime, (float) size, false);
        var col1 = this.getElementType().partColourA();
        var col2 =  color(51, 51, 51);
        var genericParticle = ParticleHandlers.genericParticle(GENERIC_PARTICLE, lifetime, (float) (size - 0.2), col1, col2, false);
        var getRandomParticle = List.of(bakedParticle, genericParticle);
        var randomSpeed = Helpers.Random.nextDouble(this.novaMaxSize/10, this.novaMaxSize/6);
        var randomType = getRandomParticle.get(Helpers.Random.nextInt(2));

        ParticleHandlers.sendParticles(
            level(), randomType, worldPosition, 0, directions.x, directions.y + 0.05, directions.z, randomSpeed
        );
    }

}
