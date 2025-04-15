package org.jahdoo.ascension.ability.abilities_combat.life_siphon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.effects.type_effects.vitality.VitalityEffect;
import org.jahdoo.ascension.attachments.CasterData;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.DamageUtils;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.mod.ElementReg;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.ascension.ability.AbilityBuilder.DAMAGE;
import static org.jahdoo.ascension.ability.AbilityBuilder.RANGE;
import static org.jahdoo.ascension.ability.abilities_combat.life_siphon.LifeSiphonAbility.HEAL_VALUE;
import static org.jahdoo.ascension.utils.Helpers.listRandom;
import static org.jahdoo.ascension.utils.Helpers.res;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;

public class LifeSiphonNova extends DefaultEntityBehaviour {

    public static final ResourceLocation abilityId = res("soul_siphon_nova_property");
    private static final String COMPONENT_NAME = "soul_siphon_nova";
    private final List<LivingEntity> targetedEntities = new ArrayList<>();
    private int privateTicks;
    private double aoe;

    @Override
    public AbstractElement getElementType() {
        return ElementReg.vitality();
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
    public AbilityHolder getAbilityHolder() {
        return this.cloud.getAbilityHolder();
    }

    @Override
    public String abilityId() {
        return COMPONENT_NAME;
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

    public float getValue(String value){
        var holder = this.cloud.getAbilityHolder();
        return (float) CasterData.getSpecificValue(holder, value);
    }

    public static AbilityHolder setModifiers(double damage, double range, double healValue) {
        return new AbilityBuilder(null, abilityId.getPath().intern())
            .setModifierWithoutBounds(DAMAGE, damage)
            .setModifierWithoutBounds(RANGE, range)
            .setModifierWithoutBounds(HEAL_VALUE, healValue)
            .buildAndReturn();
    }

    @Override
    public void onTickMethod() {
        if(aoe <= getValue(RANGE)) {
            aoe += 0.2;
            damageEntitiesLocally();
        } else {
            cloud.discard();
        }
        cloud.setRadius((float) aoe);
        pullParticlesToCenter();
    }

    public void pullParticlesToCenter(){
        var lifetime = 5;
        var part1 = bakedParticle(getElementType().id(), lifetime, 2f, false);
        var part2 = ParticleHandlers.genericParticle(GENERIC_PARTICLE, getElementType(), lifetime, 2f);
        var particleOptionsList = List.of(part1, part2);
        var pos = this.cloud.position();

        PositionFinders.getOuterRingOfRadiusRandom(
            pos, aoe * 2.8, aoe * 5, positions -> {
                if (this.cloud.level() instanceof ServerLevel serverLevel) {
                    var directions = pos.subtract(positions).normalize();
                    var randomElement = listRandom(particleOptionsList);
                    sendParticles(serverLevel, randomElement, positions, 0, directions.x, directions.y, directions.z, 1);
                }
            }
        );
    }

    public void damageEntitiesLocally(){
        var bounding = cloud.getBoundingBox().inflate(aoe, 1, aoe);
        var list = cloud.level().getEntitiesOfClass(LivingEntity.class, bounding);
        for (var livingEntity : list) {
            var canDamage = canDamageEntity(livingEntity, this.cloud.getOwner());
            var beenTargeted = targetedEntities.contains(livingEntity);
            if(canDamage && !beenTargeted){
                targetedEntities.add(livingEntity);
                DamageUtils.damageWithJahdoo(livingEntity, cloud.getOwner(), getValue(DAMAGE));
                if(Maths.percentageChance(50)){
                    VitalityEffect.throwHeartContainer(livingEntity, getValue(HEAL_VALUE));
                }
            }
        }
    }
}
