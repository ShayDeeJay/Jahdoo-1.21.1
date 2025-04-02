package org.jahdoo.ascension.ability.abilities_combat.armageddon;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.abilities_combat.fireball.FireballAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.components.DataComponentHelper;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.EntityDataReg;
import org.jahdoo.common.registers.EntityReg;

import static org.jahdoo.ascension.ability.AbilityBuilder.DAMAGE;
import static org.jahdoo.common.particle.ParticleStore.rgbToInt;

public class ArmageddonModule extends DefaultEntityBehaviour {

    private int privateTicks;
    private double aoe = 0.05;
    public static final String buddy = "buddy";
    public static final String name = "armageddon_module";
    private static final ResourceLocation abilityId = Helpers.res("armageddon_module_property");

    @Override
    public void discardCondition() {
        if(cloud.tickCount > 100) cloud.discard();
    }

    @Override
    public AbstractElement getElementType() {
        return ElementReg.inferno();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new ArmageddonModule();
    }

    @Override
    public AbilityHolder getAbilityHolder() {
        return this.element.getAbilityHolder();
    }

    @Override
    public String abilityId() {
        return name;
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

    public AbilityHolder armageddonFireballModifiers() {
        var damageA = DataComponentHelper.getSpecificValue(this.cloud.getAbilityHolder(), DAMAGE);

        return new AbilityBuilder(null, FireballAbility.abilityId.getPath().intern())
            .setDamageWithValue(0,0, damageA)
            .setEffectDurationWithValue(0,0,200)
            .setEffectChanceWithValue(0,0,20)
            .setEffectStrengthWithValue(0,0,0)
            .setModifier(FireballAbility.NOVA_RANGE, 0,0,true, Helpers.Random.nextInt(4,6))
            .setModifierWithoutBounds(buddy, 1)
            .buildAndReturn();

    }

    @Override
    public void onTickMethod() {
        if(aoe <= cloud.getRandomRadius()) aoe += 0.2;
        if(aoe >= cloud.getRandomRadius()) {
            privateTicks++;
            if(privateTicks == 10) setProjectile();
        }

        if(privateTicks < 20) return;

        aoe -= 0.4;

        if(aoe < 0) cloud.discard();
    }

    private void setProjectile(){
        if(this.cloud.getOwner() != null){
            var setRandomYHeight = Helpers.Random.nextFloat(0.3f, 0.6f);
            var fireProjectile = new ElementProjectile(
                EntityReg.INFERNO_ELEMENT_PROJECTILE.get(),
                this.cloud.getOwner(), cloud.getX(), cloud.getY(), cloud.getZ(),
                EntityDataReg.FIRE_BALL.get().setAbilityId(),
                armageddonFireballModifiers(),
                ArmageddonAbility.abilityId.getPath()
            );

            fireProjectile.setIsChildObject(true);

            fireProjectile.shoot(0, cloud.getY(), 0, cloud.getY() > 0 ? -setRandomYHeight : setRandomYHeight, 0);
            fireProjectile.setOwner(this.cloud.getOwner());
            this.cloud.getOwner().level().addFreshEntity(fireProjectile);
            Helpers.getSoundWithPosition(cloud.level(), cloud.blockPosition(), SoundEvents.BREEZE_SHOOT, 1f, 0.1f);

            var colour1 = rgbToInt(160,160,160);
            var colour2 = rgbToInt(61,61,61);
            var type = ParticleStore.GENERIC_PARTICLE;

            PositionFinders.innerRadiusRandom(cloud.position(), aoe , 10,
                positions -> {
                    var size = Helpers.Random.nextFloat(0.8f, 1.2f);
                    var particle = ParticleHandlers.genericParticle(type, 6, size, colour1, colour2, true);
                    var yOff = Helpers.Random.nextDouble(1, 1.5);

                    ParticleHandlers.sendParticles(
                        cloud.level(), particle, positions.add(0, yOff,0), 0, 0, 0, 0, 0.2
                    );
                }
            );
        }
    }

}
