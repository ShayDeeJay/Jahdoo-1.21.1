package org.jahdoo.trial_nexus.magic.abilities_combat.permafrost;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.magic.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import java.util.List;

import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;
import static org.jahdoo.trial_nexus.magic.AbilityBuilder.*;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;


public class Permafrost extends DefaultEntityBehaviour {

    private int trackCounter;
    private boolean interacted;
    private double damage;
    private double effectDuration;
    private double effectStrength;
    private double lifetime;
    private double aoe;
    private LivingEntity livingEntity;

    @Override
    public void getAoeCloud(AoeCloud aoeCloud) {
        super.getAoeCloud(aoeCloud);
        aoeCloud.setRadius((float) this.getTag(AOE));

        this.aoe = this.getTag(AOE);
        this.effectDuration = this.getTag(EFFECT_DURATION);
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
        this.lifetime = this.getTag(LIFETIME);
    }

    @Override
    public AbilityHolder getAbilityHolder() {
        return this.cloud.getAbilityHolder();
    }

    @Override
    public String abilityId() {
        return PermafrostAbility.abilityId.getPath().intern();
    }

    private void setSlownessToEntitiesInRadius(AoeCloud entity){
        PositionFinders.getInnerRingOfRadius(entity, entity.getRadius() * 3).forEach(this::setNovaDamage);
    }

    private Level level(){
        return this.cloud.level();
    }

    @Override
    public AbstractElement getElementType() {
        return ElementReg.frost();
    }

    public static ResourceLocation abilityId = JahdooHelpers.res("arctic_storm_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Permafrost();
    }

    @Override
    public void discardCondition() {
        if (cloud.tickCount > lifetime) {
            if(this.livingEntity != null && this.livingEntity.isAlive()){
                if(this.livingEntity instanceof Mob mob && mob.isNoAi()) mob.setNoAi(false);
            }
            cloud.discard();
        }
    }

    private LivingEntity getEntityInRange(Vec3 positionsA){
        return cloud.level().getNearestEntity(
            LivingEntity.class,
            TargetingConditions.DEFAULT,
            cloud.getOwner(),
            positionsA.x, positionsA.y, positionsA.z,
            new AABB(BlockPos.containing(positionsA)).deflate(1, 4, 1)
        );
    }

    private void setNovaDamage(Vec3 positionsA){
        var inRange = this.getEntityInRange(positionsA);
        if(livingEntity == null || !livingEntity.isAlive()) livingEntity = this.getEntityInRange(positionsA);
        if(inRange != null && canDamageEntity(inRange, this.cloud.getOwner())){
            if(this.cloud.tickCount % 20 == 0){
                getElementType().aEffect().setEffect(this.cloud.getOwner(), inRange, (int) effectDuration);
            }
        }
    }

    private void setOuterRingPulse(Level level){
        if (trackCounter == 10) {
            PositionFinders.getOuterRingOfRadiusRandom(cloud.position(), cloud.getRadius() * 3, Math.max(cloud.getRadius() * 1.4, 3),
                positions -> ParticleHandlers.sendParticles(
                    level, ParticleHandlers.genericParticle(this.getElementType(), 20, 2f), positions,
                    0, 0, Random.nextDouble(0.02,0.2),0,1.5
                )
            );
            trackCounter = 0;
        }
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

    private void setParticleNova(Vec3 worldPosition){
        var directions = worldPosition.subtract(this.cloud.position());
        var getMysticElement = ElementReg.frost();

        var genericParticle = ParticleHandlers.genericParticle(
            SOFT_PARTICLE, 6,
            0.1f,
            getMysticElement.partColourA(),
            getMysticElement.partColourB(),
            true
        );

        ParticleHandlers.sendParticles(
            level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, 0.5
        );
    }

    @Override
    public void onTickMethod() {
        trackCounter++;

        if(this.cloud.tickCount == 1){
            PositionFinders.getOuterRingOfRadiusRandom(this.cloud.position().add(0,0,0), this.aoe, this.aoe*50, this::setParticleNova);
        }

        if(this.cloud.tickCount > 3){
            this.setSlownessToEntitiesInRadius(cloud);
            this.setOuterRingPulse(level());
            if(this.cloud.tickCount < 20){
                this.setBlizzard(level());
            }
        }
    }

    private void setBlizzard(Level level){
        var randomParticle = List.of(
            ParticleHandlers.genericParticle(ParticleStore.GENERIC_PARTICLE, this.getElementType(), 5, 2.5f),
            bakedParticle(ElementReg.frost().id(), 10, 2.5f, false)
        );

        PositionFinders.innerRadiusRandom(cloud.position(), cloud.getRadius() * 3, cloud.getRadius() * 3,
            positions -> {
                var randomType = randomParticle.get(Random.nextInt(0, 2));
                var adjustedPos = positions.add(0, 0.5, 0);
                var randomY = Random.nextDouble(1.1, 1.3);
                var randomSpeed = Random.nextDouble(0.2, 0.4);
                ParticleHandlers.sendParticles(
                    level, randomType, adjustedPos, 0, 0, randomY, 0, randomSpeed
                );

                if(Random.nextInt(0,30) == 0){
                    SoundHelpers.getSoundWithPosition(cloud.level(), cloud.blockPosition(), SoundReg.DASH_EFFECT.get(), SoundSource.NEUTRAL, 0F, 0.3f);
                }
            }
        );
    }

}
