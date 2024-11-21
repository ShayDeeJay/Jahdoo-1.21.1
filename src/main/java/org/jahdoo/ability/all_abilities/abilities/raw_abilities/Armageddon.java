package org.jahdoo.ability.all_abilities.abilities.raw_abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.all_abilities.ability_components.ArmageddonModule;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.all_abilities.abilities.ArmageddonAbility;
import org.jahdoo.entities.AoeCloud;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.utils.PositionGetters;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
import static org.jahdoo.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.ability.AbilityBuilder.*;


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
        if(this.aoeCloud.getOwner() != null){
            var player = this.aoeCloud.getOwner();
            var damage = this.getTag(DAMAGE);
            this.damage = ModHelpers.attributeModifierCalculator(
                player,
                (float) damage,
                this.getElementType(),
                MAGIC_DAMAGE_MULTIPLIER,
                true
            );
        }
        this.lifetime = this.getTag(LIFETIME);
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.aoeCloud.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return ArmageddonAbility.abilityId.getPath().intern();
    }

    @Override
    public void onTickMethod() {
        if(aoeCloud.tickCount == 1) {
            PositionGetters.getOuterRingOfRadiusRandom(this.aoeCloud.position(), this.aoe, 200, this::setParticleNova);
            this.createModules();
        }

        aoeCloud.setRadius((float) aoe / 2);

        if(aoeCloud.tickCount % spawnSpeed == 0 || aoeCloud.tickCount == 0){
            this.createModules();
        }
    }

    private void createModules(){
        var getPositionInRadius = PositionGetters.getInnerRingOfRadiusRandom(aoeCloud.position(), aoe + 2, 100);
        this.createModule(getPositionInRadius.get(ModHelpers.Random.nextInt(0, getPositionInRadius.size()-1)));
    }

    @Override
    public void discardCondition() {
        if(aoeCloud.tickCount > lifetime) aoeCloud.discard();
    }

    public AbilityHolder setAbilityModifiers(String name, double value){
        AbilityHolder.AbilityModifiers abilityModifiers = new AbilityHolder.AbilityModifiers(value, 0,0,0,value,true);
        return new AbilityHolder(Map.of(name, abilityModifiers));
    }


    public WandAbilityHolder armageddonModule() {
        var wandAbilityHolder = new LinkedHashMap<String, AbilityHolder>();
        wandAbilityHolder.put(ArmageddonModule.name, this.setAbilityModifiers(DAMAGE, this.damage));
        return new WandAbilityHolder(wandAbilityHolder);
    }

    private void createModule(Vec3 location){
        AoeCloud aoeCloud = new AoeCloud(
            this.aoeCloud.level(),
            this.aoeCloud.getOwner(), 0.2f,
            EntityPropertyRegister.ARMAGEDDON_MODULE.get().setAbilityId(),
            armageddonModule(),
            ArmageddonAbility.abilityId.getPath().intern()
        );
        aoeCloud.setPos(location.x, location.y + ModHelpers.Random.nextInt(6, 12), location.z);
        aoeCloud.level().addFreshEntity(aoeCloud);
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

    private void setParticleNova(Vec3 worldPosition){
        var directions = worldPosition.subtract(this.aoeCloud.position());
        var getMysticElement = ElementRegistry.MYSTIC.get();

        var genericParticle = genericParticleOptions(
            GENERIC_PARTICLE_SELECTION, 20,
            6f,
            getMysticElement.particleColourPrimary(),
            getMysticElement.particleColourSecondary(),
            false
        );

        ParticleHandlers.sendParticles(
            aoeCloud.level(), genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, 0.2
        );
    }

    @Override
    public AbstractElement getElementType() {
        return ElementRegistry.INFERNO.get();
    }

    ResourceLocation abilityId = ModHelpers.res("armageddon_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Armageddon();
    }
}
