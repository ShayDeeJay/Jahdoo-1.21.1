package org.jahdoo.ability.abilities.MobAbilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.abilities.ability_data.PermafrostAbility;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
import static org.jahdoo.particle.ParticleStore.SOFT_PARTICLE_SELECTION;
import static org.jahdoo.utils.ModHelpers.Random;


public class Barrage extends DefaultEntityBehaviour {
    boolean interacted;
    int trackCounter;
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

        if(this.aoeCloud.tickCount > 3) this.setArrowsInRadius(aoeCloud);
    }


    private void setArrowsInRadius(AoeCloud entity){
        PositionGetters.getInnerRingOfRadius(entity, entity.getRadius() * 3).forEach(this::setNovaDamage);
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

    private void setParticleNova(Vec3 worldPosition){
        var directions = worldPosition.subtract(this.aoeCloud.position());
        var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, 10, 1, -1, -1, false);
        ParticleHandlers.sendParticles(level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, 0.5);
    }

    private Level level(){
        return this.aoeCloud.level();
    }

    @Override
    public void discardCondition() {
        if (aoeCloud.tickCount > lifetime) aoeCloud.discard();
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.LIGHTNING.get();
    }

    public static ResourceLocation abilityId = ModHelpers.res("barrage_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Barrage();
    }
}
