package org.jahdoo.ascension.ability.abilities_combat.hellfire;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.DamageUtils;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.mod.ElementReg;

import java.util.List;

import static net.minecraft.core.BlockPos.containing;
import static net.minecraft.sounds.SoundEvents.FIRECHARGE_USE;
import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.ability.SharedFireProperties.fireTrailVegetationRemover;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.common.registers.AttributeReg.INFERNO_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.SoundReg.DASH_EFFECT_INSTANT;

public class HellFire extends DefaultEntityBehaviour {

    private float reductionSpeed = 0.25f;
    private float yaw;
    private double damage;
    private double range;
    private double effectStrength;
    private double effectDuration;
    private Vec3 playerOriginalPosition;

    @Override
    public void getAoeCloud(AoeCloud aoeCloud) {
        super.getAoeCloud(aoeCloud);
        if(this.cloud.getOwner() != null){
            var player = this.cloud.getOwner();
            var damage = this.getTag(DAMAGE);

            this.damage = attributeModifierCalculator(
                player, (float) damage, true,
                MAGIC_DAMAGE_MULTIPLIER, INFERNO_MAGIC_DAMAGE_MULTIPLIER
            );
            this.playerOriginalPosition = player.position();
            this.yaw = player.getYRot();
        }
        this.range = this.getTag(RANGE);
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
        this.effectDuration = this.getTag(EFFECT_DURATION);
    }

    private void updateRadius(){
        cloud.setRadius(cloud.getRadius() + reductionSpeed);
    }

    @Override
    public AbstractElement getElementType() {
        return ElementReg.inferno();
    }

    @Override
    public AbilityHolder getAbilityHolder() {
        return this.cloud.getAbilityHolder();
    }

    @Override
    public String abilityId() {
        return HellfireAbility.abilityId.getPath().intern();
    }

    public static ResourceLocation abilityId = res("hellfire_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new HellFire();
    }

    @Override
    public void onTickMethod() {
        this.updateRadius();
        this.reductionSpeed *= 1.01F;
        cloud.setInvisible(false);
        novaBehaviour();
    }

    private void setNovaDamage(Vec3 positionsA){
        var livingEntity = this.getEntityInRange(positionsA);
        if (livingEntity == null) return;
        if(!canDamageEntity(livingEntity, this.cloud.getOwner())) return;
        livingEntity.addEffect(new JahdooMobEffect(EffectReg.INFERNO_EFFECT.getDelegate(), (int) effectDuration, (int) effectStrength));
        DamageUtils.damageWithJahdoo(livingEntity, cloud.getOwner(), damage);
    }

    private LivingEntity getEntityInRange(Vec3 positionsA){
        return cloud.level().getNearestEntity(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            cloud.getOwner(),
            positionsA.x, positionsA.y, positionsA.z,
            new AABB(containing(positionsA)).deflate(1, 2, 1)
        );
    }

    private void novaSoundManager(List<Vec3> positions){
        var posOf = containing(positions.get(positions.size() / 2));
        var level = cloud.level();
        var tick = cloud.tickCount;

        if(tick == 1) getSoundWithPosition(level, posOf, DASH_EFFECT_INSTANT.get(), 0.6F, 1.4F);
        if (tick % 3 == 0) getSoundWithPosition(level, posOf, FIRECHARGE_USE, 0.4F, 0.8F);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.put("position", nbtDoubleList(this.playerOriginalPosition.x, this.playerOriginalPosition.y, this.playerOriginalPosition.z));
        compoundTag.putFloat("reduction", this.reductionSpeed);
        compoundTag.putFloat("yaw", this.yaw);
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(RANGE, this.range);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        var list = compoundTag.getList("position", Tag.TAG_DOUBLE);
        this.playerOriginalPosition = new Vec3(list.getDouble(0), list.getDouble(1), list.getDouble(2));
        this.reductionSpeed = compoundTag.getFloat("reduction");
        this.yaw = compoundTag.getFloat("yaw");
        this.damage = compoundTag.getDouble(DAMAGE);
        this.range = compoundTag.getDouble(RANGE);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
    }

    private void novaBehaviour(){
        var radius = cloud.getRadius() * 2;
        var positions = PositionFinders.getSemicircle(cloud.position(), radius, 5, yaw, 30);
        this.novaSoundManager(positions);

        positions.forEach(
            positionsA -> {
                var newPos = positionsA.add(0,Random.nextDouble(0.1, 0.8),0);
                var blockPos = containing(positionsA);
                fireTrailVegetationRemover(this.cloud.level().getBlockState(blockPos), blockPos, this.cloud);
                this.setParticleNova(newPos);
                this.setNovaDamage(positionsA);
                if (this.playerOriginalPosition.distanceTo(positionsA) >= this.range) cloud.discard();
            }
        );
    }

    private void setParticleNova(Vec3 worldPosition){
        var positionScrambler = worldPosition.offsetRandom(RandomSource.create(), 3f);
        var directions = positionScrambler.subtract(this.cloud.position()).normalize();
        var lifetime = (int) (this.range/4);
        var col1 = this.getElementType().partColourA();
        var col2 = this.getElementType().partColourFade();
        var lifeExt = Math.max(lifetime, 5);
        var bakedParticle = bakedParticle(this.getElementType().id(), lifeExt, (float) 5, false);
        var genericParticle = ParticleHandlers.genericParticle(GENERIC_PARTICLE, lifeExt, (float) 5, col1, col2, false);
        var getRandomParticle = List.of(bakedParticle, genericParticle);
        var level = this.cloud.level();
        var speed = Math.min(this.cloud.getRadius() * 2, 1.5);
        var randomY = Random.nextDouble(0, 0.4);

        ParticleHandlers.sendParticles(
            level, getRandomParticle.get(Random.nextInt(2)), worldPosition, 0, directions.x, directions.y + randomY, directions.z, speed
        );
    }
}
