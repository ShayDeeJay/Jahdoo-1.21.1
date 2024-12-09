package org.jahdoo.ability.all_abilities.abilities.raw_abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.all_abilities.abilities.HellfireAbility;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.utils.DamageUtil;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.List;

import static org.jahdoo.ability.SharedFireProperties.fireTrailVegetationRemover;
import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
import static org.jahdoo.registers.DamageTypeRegistry.JAHDOO_SOURCE;
import static org.jahdoo.utils.ModHelpers.Random;

public class HellFire extends DefaultEntityBehaviour {

    float reductionSpeed = 0.25f;
    float yaw;
    double damage;
    double range;
    double effectStrength;
    double effectDuration;
    Vec3 playerOriginalPosition;

    @Override
    public void getAoeCloud(AoeCloud aoeCloud) {
        super.getAoeCloud(aoeCloud);
        if(this.aoeCloud.getOwner() != null){
            var player = this.aoeCloud.getOwner();
            var damage = this.getTag(DAMAGE);
            var attribute = AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;
            this.damage = ModHelpers.attributeModifierCalculator(
                player, (float) damage, this.getElementType(), attribute, true
            );
            this.playerOriginalPosition = player.position();
            this.yaw = player.getYRot();
        }
        this.range = this.getTag(RANGE);
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
        this.effectDuration = this.getTag(EFFECT_DURATION);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.put("position", ModHelpers.nbtDoubleList(this.playerOriginalPosition.x, this.playerOriginalPosition.y, this.playerOriginalPosition.z));
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

    @Override
    public void onTickMethod() {
        this.updateRadius();
        this.reductionSpeed *= 1.01F;
        aoeCloud.setInvisible(true);
        novaBehaviour();
    }

    private void updateRadius(){
        aoeCloud.setRadius(aoeCloud.getRadius() + reductionSpeed);
    }

    private void novaBehaviour(){
        var radius = aoeCloud.getRadius() * 2;
        var positions = PositionGetters.getSemicircle(aoeCloud.position(), radius, 5, yaw, 30);
        this.novaSoundManager(positions);

        positions.forEach(
            positionsA -> {
                var newPos = positionsA.add(0,Random.nextDouble(0.1, 0.8),0);
                var blockPos = BlockPos.containing(positionsA);
                fireTrailVegetationRemover(this.aoeCloud.level().getBlockState(blockPos), blockPos, this.aoeCloud, this.aoeCloud.getOwner());
                this.setParticleNova(newPos);
                this.setNovaDamage(positionsA);
                if (this.playerOriginalPosition.distanceTo(positionsA) >= this.range) aoeCloud.discard();
            }
        );
    }

    private void novaSoundManager(List<Vec3> positions){
        if(aoeCloud.tickCount == 1) ModHelpers.getSoundWithPosition(aoeCloud.level(), BlockPos.containing(positions.get(positions.size()/2)), SoundRegister.DASH_EFFECT.get(), 0.6f,1.4f);
        if (aoeCloud.tickCount % 3 == 0) ModHelpers.getSoundWithPosition(aoeCloud.level(), BlockPos.containing(positions.get(positions.size()/2)), SoundEvents.FIRECHARGE_USE,0.4f,0.8f);
    }

    private void setNovaDamage(Vec3 positionsA){
        var livingEntity = this.getEntityInRange(positionsA);
        if (livingEntity == null) return;
        if(!this.canDamageEntity(livingEntity, this.aoeCloud.getOwner())) return;

        livingEntity.addEffect(new CustomMobEffect(EffectsRegister.FIRE_EFFECT.getDelegate(), (int) effectDuration, (int) effectStrength));
        DamageUtil.damageWithJahdoo(livingEntity, aoeCloud.getOwner(), damage);
    }

    private LivingEntity getEntityInRange(Vec3 positionsA){
        return aoeCloud.level().getNearestEntity(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            aoeCloud.getOwner(),
            positionsA.x, positionsA.y, positionsA.z,
            new AABB(BlockPos.containing(positionsA)).deflate(1, 2, 1)
        );
    }

    private void setParticleNova(Vec3 worldPosition){
        var positionScrambler = worldPosition.offsetRandom(RandomSource.create(), 3f);
        var directions = positionScrambler.subtract(this.aoeCloud.position()).normalize();
        var lifetime = (int)( this.range/2);
        var col1 = this.getElementType().particleColourPrimary();
        var col2 = this.getElementType().particleColourFaded();
        var bakedParticle = bakedParticleOptions(this.getElementType().getTypeId(), lifetime, (float) 7, false);
        var genericParticle = genericParticleOptions(GENERIC_PARTICLE_SELECTION, lifetime, (float) 4, col1, col2, false);
        var getRandomParticle = List.of(bakedParticle, genericParticle);
        var level = this.aoeCloud.level();
        var speed = Math.min(this.aoeCloud.getRadius() * 2, 1.5);
        var randomY = Random.nextDouble(0, 0.4);

        ParticleHandlers.sendParticles(
            level, getRandomParticle.get(Random.nextInt(2)), worldPosition, 0, directions.x, directions.y + randomY, directions.z, speed
        );
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.INFERNO.get();
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.aoeCloud.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return HellfireAbility.abilityId.getPath().intern();
    }

    ResourceLocation abilityId = ModHelpers.res("hellfire_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new HellFire();
    }
}
