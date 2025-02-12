package org.jahdoo.ability.ability_components;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.ability.AbilityBuilder;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.effects.type_effects.vitality.VitalityEffect;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.List;

import static org.jahdoo.ability.AbilityBuilder.DAMAGE;
import static org.jahdoo.ability.AbilityBuilder.RANGE;
import static org.jahdoo.ability.abilities.ability_data.OverchargedAbility.HEAL_VALUE;
import static org.jahdoo.ability.abilities.ability_data.OverchargedAbility.PULSES;
import static org.jahdoo.components.DataComponentHelper.*;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.sendParticles;

public class SoulSiphonNova extends DefaultEntityBehaviour {
    public static final ResourceLocation abilityId = ModHelpers.res("soul_siphon_nova_property");
    public static final String COMPONENT_NAME = "soul_siphon_nova";
    int privateTicks;
    double aoe = 0.05;

    public static WandAbilityHolder setModifiers(double damage, double range, double healValue) {
        return new AbilityBuilder(null, abilityId.getPath().intern())
            .setModifierWithoutBounds(DAMAGE, damage)
            .setModifierWithoutBounds(RANGE, range)
            .setModifierWithoutBounds(HEAL_VALUE, healValue)
            .buildAndReturn();
    }

    public float getValue(String value){
        return (float) getSpecificValue(abilityId.getPath().intern(), this.aoeCloud.getwandabilityholder(), value);
    }

    @Override
    public void onTickMethod() {
        if(aoe <= getValue(RANGE)) {
            aoe += 0.25;
            damageEntitiesLocally();
        } else {
            aoeCloud.discard();
        }
        aoeCloud.setRadius((float) aoe);
        if(aoe >= aoeCloud.getRandomRadius()) privateTicks++;
        pullParticlesToCenter();
    }

    public void damageEntitiesLocally(){
        var bounding = aoeCloud.getBoundingBox().inflate(aoe, 1, aoe);
        var list = aoeCloud.level().getEntitiesOfClass(LivingEntity.class, bounding);
        for (var livingEntity : list) {
            if(canDamageEntity(livingEntity, this.aoeCloud.getOwner())){
                livingEntity.hurt(aoeCloud.damageSources().generic(), getValue(DAMAGE));
                if(livingEntity.hurtMarked){
                    VitalityEffect.throwHeartContainer(livingEntity, getValue(HEAL_VALUE));
                }
            }
        }
    }

    public void pullParticlesToCenter(){
        var lifetime = 5;
        var bakedParticleOptions = ParticleHandlers.bakedParticleOptions(getElementType().getTypeId(), lifetime, 2f, false);
        var genericParticleOptions = genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, getElementType(), lifetime, 2f);
        var particleOptionsList = List.of(bakedParticleOptions, genericParticleOptions);

        PositionGetters.getOuterRingOfRadiusRandom(
            this.aoeCloud.position(), aoe * 2.8, aoe * 5,
            positions -> {
                if (this.aoeCloud.level() instanceof ServerLevel serverLevel) {
                    var directions = this.aoeCloud.position().subtract(positions).normalize();
                    sendParticles(
                        serverLevel, ModHelpers.getRandomListElement(particleOptionsList),
                        positions, 0, directions.x, directions.y, directions.z, 1
                    );
                }
            }
        );
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putInt("private_ticks", privateTicks);
        compoundTag.putDouble("aoe", aoe);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.privateTicks = compoundTag.getInt("private_ticks");
        this.aoe = compoundTag.getDouble("aoe");
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.VITALITY.get();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new SoulSiphonNova();
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.aoeCloud.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return COMPONENT_NAME;
    }
}
