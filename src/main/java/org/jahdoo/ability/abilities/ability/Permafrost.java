package org.jahdoo.ability.abilities.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.abilities.ability_data.PermafrostAbility;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.List;

import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.particle.ParticleStore.SOFT_PARTICLE_SELECTION;
import static org.jahdoo.utils.ModHelpers.Random;


public class Permafrost extends DefaultEntityBehaviour {

    boolean interacted;
    int trackCounter;
    private double damage;
    private double effectDuration;
    private double effectStrength;
    private double lifetime;
    private double aoe;
    private LivingEntity livingEntity;

    @Override
    public void getAoeCloud(AoeCloud aoeCloud) {
        super.getAoeCloud(aoeCloud);
        if(this.aoeCloud.getOwner() != null){
//            var player = this.aoeCloud.getOwner();
//            var damage = this.getTag(DAMAGE);
//            this.damage = ModHelpers.attributeModifierCalculator(
//                player,
//                (float) damage,
//                this.getElementType(),
//                AttributesRegister.MAGIC_DAMAGE_MULTIPLIER,
//                true
//            );
        }
        this.aoe = this.getTag(AOE);
        aoeCloud.setRadius((float) this.getTag(AOE));
        this.effectDuration = this.getTag(EFFECT_DURATION);
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
        this.lifetime = this.getTag(LIFETIME);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putInt("track_counter", trackCounter);
        compoundTag.putBoolean("interacted", this.interacted);
        compoundTag.putDouble(AOE, this.aoe);
        compoundTag.putDouble(EFFECT_DURATION, this.effectDuration);
        compoundTag.putDouble(EFFECT_STRENGTH, this.effectStrength);
        compoundTag.putDouble(LIFETIME, this.lifetime);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.interacted = compoundTag.getBoolean("interacted");
        this.trackCounter = compoundTag.getInt("track_counter");
        this.aoe = compoundTag.getDouble(AOE);
        this.effectDuration = compoundTag.getDouble(EFFECT_DURATION);
        this.effectStrength = compoundTag.getDouble(EFFECT_STRENGTH);
        this.lifetime = compoundTag.getDouble(LIFETIME);
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.aoeCloud.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return PermafrostAbility.abilityId.getPath().intern();
    }

    @Override
    public void onTickMethod() {
        trackCounter++;

        if(this.aoeCloud.tickCount == 1){
            PositionGetters.getOuterRingOfRadiusRandom(this.aoeCloud.position().add(0,0,0), this.aoe, this.aoe*50, this::setParticleNova);
        }

        if(this.aoeCloud.tickCount > 3){
            this.setSlownessToEntitiesInRadius(aoeCloud);
            this.setOuterRingPulse(level());
            if(this.aoeCloud.tickCount < 20){
                this.setBlizzard(level());
            }
        }
    }

    private void setBlizzard(Level level){
        var randomParticle = List.of(
            genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, this.getElementType(), 5, 2.5f),
            bakedParticleOptions(ElementRegistry.FROST.get().getTypeId(), 10, 2.5f, false)
        );

        PositionGetters.getInnerRingOfRadiusRandom(aoeCloud.position(), aoeCloud.getRadius() * 3, aoeCloud.getRadius() * 3,
            positions -> {
                var randomType = randomParticle.get(Random.nextInt(0, 2));
                var adjustedPos = positions.add(0, 0.5, 0);
                var randomY = Random.nextDouble(1.1, 1.3);
                var randomSpeed = Random.nextDouble(0.2, 0.4);
                ParticleHandlers.sendParticles(
                    level, randomType, adjustedPos, 0, 0, randomY, 0, randomSpeed
                );

                if(Random.nextInt(0,30) == 0){
                    ModHelpers.getSoundWithPosition(aoeCloud.level(), aoeCloud.blockPosition(), SoundRegister.DASH_EFFECT.get(), 0, 0.3f);
                }
            }
        );
    }

    private void setSlownessToEntitiesInRadius(AoeCloud entity){
        PositionGetters.getInnerRingOfRadius(entity, entity.getRadius() * 3).forEach(this::setNovaDamage);
    }

    private void setOuterRingPulse(Level level){
        if (trackCounter == 10) {
            PositionGetters.getOuterRingOfRadiusRandom(aoeCloud.position(), aoeCloud.getRadius() * 3, Math.max(aoeCloud.getRadius() * 1.4, 3),
                positions -> ParticleHandlers.sendParticles(
                    level, genericParticleOptions(this.getElementType(), 20, 2f), positions,
                    0, 0, Random.nextDouble(0.02,0.2),0,1.5
                )
            );
            trackCounter = 0;
        }
    }

    private void setParticleNova(Vec3 worldPosition){
        var directions = worldPosition.subtract(this.aoeCloud.position());
        var getMysticElement = ElementRegistry.FROST.get();

        var genericParticle = genericParticleOptions(
            SOFT_PARTICLE_SELECTION, 6,
            0.1f,
            getMysticElement.particleColourPrimary(),
            getMysticElement.particleColourSecondary(),
            true
        );

        ParticleHandlers.sendParticles(
            level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, 0.5
        );
    }

    private void setNovaDamage(Vec3 positionsA){
        var livingEntity1 = this.getEntityInRange(positionsA);
        if(livingEntity == null || !livingEntity.isAlive()) livingEntity = this.getEntityInRange(positionsA);
        if(livingEntity1 != null && canDamageEntity(livingEntity1, this.aoeCloud.getOwner())){
            var effect = livingEntity1.getEffect(EffectsRegister.FROST_EFFECT);
            if(this.aoeCloud.tickCount % 20 == 0){
                livingEntity1.addEffect(new CustomMobEffect(EffectsRegister.FROST_EFFECT.getDelegate(), (int) effectDuration, Math.min((int) effectStrength, (effect == null ? 0 : effect.getAmplifier()) + 1)));
            }
        }
    }

    private Level level(){
        return this.aoeCloud.level();
    }

    private LivingEntity getEntityInRange(Vec3 positionsA){
        return aoeCloud.level().getNearestEntity(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            aoeCloud.getOwner(),
            positionsA.x, positionsA.y, positionsA.z,
            new AABB(BlockPos.containing(positionsA)).deflate(1, 4, 1)
        );
    }

    @Override
    public void discardCondition() {
        if (aoeCloud.tickCount > lifetime) {
            if(this.livingEntity != null && this.livingEntity.isAlive()){
                if(this.livingEntity instanceof Mob mob && mob.isNoAi()) mob.setNoAi(false);
            }
            aoeCloud.discard();
        }
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.FROST.get();
    }

    public static ResourceLocation abilityId = ModHelpers.res("arctic_storm_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Permafrost();
    }
}
