package org.jahdoo.ascension.ability.abilities.armageddon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.EntityDataReg;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.utils.PositionFinders.getInnerRingOfRadiusRandom;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
import static org.jahdoo.common.registers.AttributeReg.INFERNO_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;


public class Armageddon extends DefaultEntityBehaviour {

    double aoe;
    double spawnSpeed;
    double damage;
    double lifetime;

    @Override
    public void getAoeCloud(AoeCloud aoeCloud) {
        super.getAoeCloud(aoeCloud);
        this.aoe = this.getTag(AOE);
        this.spawnSpeed = this.getTag(ArmageddonAbility.SPAWNING_SPEED);
        if(this.cloud.getOwner() != null){
            var player = this.cloud.getOwner();
            var damage = this.getTag(DAMAGE);
            this.damage = Helpers.attributeModifierCalculator(
                player,
                (float) damage,
                true,
                MAGIC_DAMAGE_MULTIPLIER,
                INFERNO_MAGIC_DAMAGE_MULTIPLIER
            );
        }
        this.lifetime = this.getTag(LIFETIME);
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.cloud.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return ArmageddonAbility.abilityId.getPath().intern();
    }

    @Override
    public void discardCondition() {
        if(cloud.tickCount > lifetime) cloud.discard();
    }


    @Override
    public AbstractElement getElementType() {
        return ElementReg.inferno();
    }

    public static ResourceLocation abilityId = Helpers.res("armageddon_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Armageddon();
    }

    private void createModules(){
        var getPositionInRadius = getInnerRingOfRadiusRandom(cloud.position(), this.cloud.getRadius() * 2, 100);
        this.createModule(getPositionInRadius.get(Helpers.Random.nextInt(0, getPositionInRadius.size())));
    }

    public AbilityHolder setAbilityModifiers(String name, double value){
        var abilityModifiers = new AbilityHolder.AbilityModifiers(value, 0,0,0,value,true);
        return new AbilityHolder(Map.of(name, abilityModifiers));
    }

    public WandAbilityHolder armageddonModule() {
        var wandAbilityHolder = new LinkedHashMap<String, AbilityHolder>();
        wandAbilityHolder.put(ArmageddonModule.name, this.setAbilityModifiers(DAMAGE, this.damage));
        return new WandAbilityHolder(wandAbilityHolder);
    }

    @Override
    public void addAdditionalDetails(CompoundTag compoundTag) {
        compoundTag.putDouble(DAMAGE, this.damage);
        compoundTag.putDouble(AOE, this.aoe);
        compoundTag.putDouble(ArmageddonAbility.SPAWNING_SPEED, this.spawnSpeed);
        compoundTag.putDouble(LIFETIME, this.lifetime);
    }

    @Override
    public void readCompoundTag(CompoundTag compoundTag) {
        this.damage = compoundTag.getDouble(DAMAGE);
        this.aoe = compoundTag.getDouble(AOE);
        this.spawnSpeed = compoundTag.getDouble(ArmageddonAbility.SPAWNING_SPEED);
        this.lifetime = compoundTag.getDouble(LIFETIME);
    }

    private void createModule(Vec3 location){
        var aoeCloud = new AoeCloud(
            this.cloud.level(),
            this.cloud.getOwner(), 0.2f,
            EntityDataReg.ARMAGEDDON_MODULE.get().setAbilityId(),
            armageddonModule(),
            ArmageddonAbility.abilityId.getPath().intern()
        );
        aoeCloud.setPos(location.x, location.y + Helpers.Random.nextInt(6, 12), location.z);
        aoeCloud.level().addFreshEntity(aoeCloud);
    }

    @Override
    public void onTickMethod() {

        if(cloud.tickCount == 1) this.createModules();

        cloud.setRadius((float) aoe / 2);

        if(cloud.tickCount % spawnSpeed == 0 || cloud.tickCount == 0){
            this.createModules();
        }

    }

    private void setParticleNova(Vec3 worldPosition){
        var directions = worldPosition.subtract(this.cloud.position());
        var getMysticElement = ElementReg.mystic();

        var genericParticle = genericParticleOptions(
            GENERIC_PARTICLE_SELECTION, 20,
            6f,
            getMysticElement.partColourA(),
            getMysticElement.partColourB(),
            false
        );

        ParticleHandlers.sendParticles(
            cloud.level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, 0.2
        );
    }

}
