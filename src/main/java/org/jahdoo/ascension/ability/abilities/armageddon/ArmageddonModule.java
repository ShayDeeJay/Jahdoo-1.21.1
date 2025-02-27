package org.jahdoo.ascension.ability.abilities.armageddon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.abilities.fireball.FireballAbility;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.common.registers.EntitiesRegister;
import org.jahdoo.common.registers.EntityPropertyRegister;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.common.components.DataComponentHelper;
import org.jahdoo.ascension.utils.PositionFinders;

import static org.jahdoo.common.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.ascension.ability.AbilityBuilder.DAMAGE;
import static org.jahdoo.common.particle.ParticleStore.rgbToInt;

public class ArmageddonModule extends DefaultEntityBehaviour {

    public static final String name = "armageddon_module";
    public static final String buddy = "buddy";
    int privateTicks;
    double aoe = 0.05;

    public WandAbilityHolder armageddonFireballModifiers() {
        double damageA = DataComponentHelper.getSpecificValue(name, this.aoeCloud.getwandabilityholder(), DAMAGE);
        return new AbilityBuilder(null, FireballAbility.abilityId.getPath().intern())
            .setDamageWithValue(0,0, damageA)
            .setEffectDurationWithValue(0,0,200)
            .setEffectChanceWithValue(0,0,20)
            .setEffectStrengthWithValue(0,0,0)
            .setModifier(FireballAbility.novaRange, 0,0,true, Helpers.Random.nextInt(4,6))
            .setModifierWithoutBounds(buddy, 1)
            .buildAndReturn();

    }

    @Override
    public void onTickMethod() {
        if(aoe <= aoeCloud.getRandomRadius()) aoe += 0.2;

//        fireballNova(aoeCloud.getRandomRadius());
        if(aoe >= aoeCloud.getRandomRadius()) {
            privateTicks++;
            if(privateTicks == 10) setProjectile();
        }

        if(privateTicks < 20) return;

        aoe -= 0.4;
        if(aoe < 0) {
            if(!(aoeCloud.level() instanceof  ServerLevel serverLevel)) return;
//            ParticleHandlers.spawnPoof(
//                serverLevel, aoeCloud.position(), 5,
//                ElementRegistry.MYSTIC.get().getParticleGroup().genericSlow(),0,0,0,0.1f
//            );
            aoeCloud.discard();
        }
    }

    @Override
    public void discardCondition() {
        if(aoeCloud.tickCount > 100) aoeCloud.discard();
    }

    private void setProjectile(){
        if(this.aoeCloud.getOwner() != null){
            var setRandomYHeight = Helpers.Random.nextFloat(0.3f, 0.6f);
            var fireProjectile = new ElementProjectile(
                EntitiesRegister.INFERNO_ELEMENT_PROJECTILE.get(),
                this.aoeCloud.getOwner(), aoeCloud.getX(), aoeCloud.getY(), aoeCloud.getZ(),
                EntityPropertyRegister.FIRE_BALL.get().setAbilityId(),
                armageddonFireballModifiers(),
                ArmageddonAbility.abilityId.getPath()
            );

            fireProjectile.setIsChildObject(true);

            fireProjectile.shoot(0, aoeCloud.getY(), 0, aoeCloud.getY() > 0 ? -setRandomYHeight : setRandomYHeight, 0);
            fireProjectile.setOwner(this.aoeCloud.getOwner());
            this.aoeCloud.getOwner().level().addFreshEntity(fireProjectile);
            Helpers.getSoundWithPosition(aoeCloud.level(), aoeCloud.blockPosition(), SoundEvents.BREEZE_SHOOT, 1f, 0.1f);

            var colour1 = rgbToInt(160,160,160);
            var colour2 = rgbToInt(61,61,61);
            var type = ParticleStore.GENERIC_PARTICLE_SELECTION;

            PositionFinders.getInnerRingOfRadiusRandom(aoeCloud.position(), aoe , 10,
                positions -> {
                    var size = Helpers.Random.nextFloat(0.8f, 1.2f);
                    var particle = genericParticleOptions(type, 6, size, colour1, colour2, true);
                    var yOff = Helpers.Random.nextDouble(1, 1.5);

                    ParticleHandlers.sendParticles(
                        aoeCloud.level(), particle, positions.add(0, yOff,0), 0, 0, 0, 0, 0.2
                    );
                }
            );
        }
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
        return ElementRegistry.inferno();
    }

    ResourceLocation abilityId = Helpers.res("armageddon_module_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new ArmageddonModule();
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return this.elementProjectile.getwandabilityholder();
    }

    @Override
    public String abilityId() {
        return name;
    }
}
