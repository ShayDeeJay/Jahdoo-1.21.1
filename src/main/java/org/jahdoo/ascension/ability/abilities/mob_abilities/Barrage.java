package org.jahdoo.ascension.ability.abilities.mob_abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.abilities.permafrost.PermafrostAbility;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.particle.ParticleHandlers;

import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.ascension.utils.PositionFinders.getInnerRingOfRadius;
import static org.jahdoo.ascension.utils.PositionFinders.getOuterRingOfRadiusRandom;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;


public class Barrage extends DefaultEntityBehaviour {

    public final static ResourceLocation abilityId = Helpers.res("barrage_property");
    private boolean interacted;
    private int trackCounter;
    private double effectDuration;
    private double effectStrength;
    private double lifetime;
    private double aoe;

    @Override
    public void getAoeCloud(AoeCloud aoeCloud) {
        super.getAoeCloud(aoeCloud);
        this.aoe = this.getTag(AOE);
        aoeCloud.setRadius((float) this.getTag(AOE));
        this.effectDuration = this.getTag(EFFECT_DURATION);
        this.effectStrength = this.getTag(EFFECT_STRENGTH);
        this.lifetime = this.getTag(LIFETIME);
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.cloud.getwandabilityholder();
    }

    private Level level(){
        return this.cloud.level();
    }

    @Override
    public void discardCondition() {
        if (cloud.tickCount > lifetime) cloud.discard();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Barrage();
    }

    @Override
    public String abilityId() {
        return PermafrostAbility.abilityId.getPath().intern();
    }

    private void setArrowsInRadius(AoeCloud entity){
        getInnerRingOfRadius(entity, entity.getRadius() * 3).forEach(this::setNovaDamage);
    }

    private void setParticleNova(Vec3 worldPosition){
        var directions = worldPosition.subtract(this.cloud.position());
        var genericParticle = ParticleHandlers.genericParticle(SOFT_PARTICLE, 10, 1, -1, -1, false);
        ParticleHandlers.sendParticles(level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, 0.5);
    }

    @Override
    public void onTickMethod() {
        trackCounter++;
        if(this.cloud.tickCount == 1){
            getOuterRingOfRadiusRandom(this.cloud.position().add(0,0,0), this.aoe, this.aoe*50, this::setParticleNova);
        }
        if(this.cloud.tickCount > 3) this.setArrowsInRadius(cloud);
    }

    private void setNovaDamage(Vec3 vec3){
        if(this.trackCounter % 10 == 0 && Random.nextInt(30) == 0){
            var arrow = EntityType.ARROW.create(level());
            if (arrow == null) return;
            arrow.moveTo(vec3.x, vec3.y + 5, vec3.z);

            arrow.setBaseDamage(5);
            arrow.shoot(0, -1, 0, 1, 0);
            level().addFreshEntity(arrow);
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

}
