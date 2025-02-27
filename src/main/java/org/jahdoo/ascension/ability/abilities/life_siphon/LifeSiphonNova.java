package org.jahdoo.ascension.ability.abilities.life_siphon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.effects.type_effects.vitality.VitalityEffect;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.ascension.utils.DamageUtils;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.ascension.utils.PositionFinders;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.ascension.ability.AbilityBuilder.DAMAGE;
import static org.jahdoo.ascension.ability.AbilityBuilder.RANGE;
import static org.jahdoo.ascension.ability.abilities.life_siphon.LifeSiphonAbility.HEAL_VALUE;
import static org.jahdoo.common.components.DataComponentHelper.*;
import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.particle.ParticleStore.*;
import static org.jahdoo.ascension.utils.Helpers.*;

public class LifeSiphonNova extends DefaultEntityBehaviour {
    public static final ResourceLocation abilityId = res("soul_siphon_nova_property");
    public static final String COMPONENT_NAME = "soul_siphon_nova";
    public List<LivingEntity> targetedEntities = new ArrayList<>();
    int privateTicks;
    double aoe;

    public static WandAbilityHolder setModifiers(double damage, double range, double healValue) {
        return new AbilityBuilder(null, abilityId.getPath().intern())
            .setModifierWithoutBounds(DAMAGE, damage)
            .setModifierWithoutBounds(RANGE, range)
            .setModifierWithoutBounds(HEAL_VALUE, healValue)
            .buildAndReturn();
    }

    public float getValue(String value){
        var id = abilityId.getPath().intern();
        var holder = this.aoeCloud.getwandabilityholder();
        return (float) getSpecificValue(id, holder, value);
    }

    @Override
    public void onTickMethod() {
        if(aoe <= getValue(RANGE)) {
            aoe += 0.2;
            damageEntitiesLocally();
        } else {
            aoeCloud.discard();
        }
        aoeCloud.setRadius((float) aoe);
        pullParticlesToCenter();
    }

    public void damageEntitiesLocally(){
        var bounding = aoeCloud.getBoundingBox().inflate(aoe, 1, aoe);
        var list = aoeCloud.level().getEntitiesOfClass(LivingEntity.class, bounding);
        for (var livingEntity : list) {
            var canDamage = canDamageEntity(livingEntity, this.aoeCloud.getOwner());
            var beenTargeted = targetedEntities.contains(livingEntity);
            if(canDamage && !beenTargeted){
                targetedEntities.add(livingEntity);
                DamageUtils.damageWithJahdoo(livingEntity, aoeCloud.getOwner(), getValue(DAMAGE));
                if(Maths.percentageChance(50)){
                    VitalityEffect.throwHeartContainer(livingEntity, getValue(HEAL_VALUE));
                }
            }
        }
    }

    public void pullParticlesToCenter(){
        var lifetime = 5;
        var part1 = bakedParticleOptions(getElementType().id(), lifetime, 2f, false);
        var part2 = genericParticleOptions(GENERIC_PARTICLE_SELECTION, getElementType(), lifetime, 2f);
        var particleOptionsList = List.of(part1, part2);
        var pos = this.aoeCloud.position();

        PositionFinders.getOuterRingOfRadiusRandom(
            pos, aoe * 2.8, aoe * 5, positions -> {
                if (this.aoeCloud.level() instanceof ServerLevel serverLevel) {
                    var directions = pos.subtract(positions).normalize();
                    var randomElement = listRandom(particleOptionsList);
                    sendParticles(serverLevel, randomElement, positions, 0, directions.x, directions.y, directions.z, 1);
                }
            }
        );
    }

    @Override
    public void addAdditionalDetails(CompoundTag tag) {
        tag.putInt("private_ticks", privateTicks);
        tag.putDouble("aoe", aoe);
    }

    @Override
    public void readCompoundTag(CompoundTag tag) {
        this.privateTicks = tag.getInt("private_ticks");
        this.aoe = tag.getDouble("aoe");
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.vitality();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new LifeSiphonNova();
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
