package org.jahdoo.ability.abilities.ability_data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.ability.AbilityBuilder;

import static org.jahdoo.components.DataComponentHelper.getSpecificValue;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.utils.ModHelpers.getSoundWithPosition;

public class BoltzAbility extends AbilityRegistrar {

    public static final ResourceLocation abilityId = ModHelpers.res("boltz");
    public static final String dischargeRadius = "Discharge Radius";
    public static final String totalBolts = "Total Boltz";

    @Override
    public void invokeAbility(Player player) {
        var amplifier = 0;
        var totalShots = (int) getSpecificValue(player, player.getMainHandItem(), totalBolts) * amplifier;
        var direction = player.getLookAngle();
        var particleOptions = genericParticleOptions(ParticleStore.ELECTRIC_PARTICLE_SELECTION, this.getElemenType(), 5, 1.2f, 0.5);

        for (int i = 0; i < totalShots; i++) {
            ElementProjectile elementProjectile = new ElementProjectile(
                EntitiesRegister.LIGHTNING_ELEMENT_PROJECTILE.get(),
                player,
                EntityPropertyRegister.BOLTZ.get().setAbilityId(),
                0,
                abilityId.getPath().intern()
            );
            double spread =  0.8 * amplifier; // Adjust the spread value as needed
            double spreadX = direction.x + (Math.random() - 0.5) * spread;
            double spreadY = direction.y + (Math.random() - 0.5) * spread;
            double spreadZ = direction.z + (Math.random() - 0.5) * spread;

            this.fireProjectileDirection(elementProjectile, player, 0.5f, new Vec3(spreadX, spreadY, spreadZ));
        }

        for(int i = 0; i < totalShots * 5; i++){
            double spread = 0.8; // Adjust the spread value as needed
            double spreadX = direction.x + (Math.random() - 0.5) * spread;
            double spreadY = direction.y + (Math.random() - 0.5) * spread;
            double spreadZ = direction.z + (Math.random() - 0.5) * spread;
            sendParticles(player.level(), particleOptions, player.position().add(0,1.5,0), 0, spreadX, spreadY, spreadZ, 1);
        }

        getSoundWithPosition(player.level(), player.blockPosition(), SoundRegister.ORB_CREATE.get(), 0.5f,1.5f);
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setStaticMana(50)
            .setStaticCooldown(600)
//            .setMana(60,20,10)
//            .setCooldown(500,100,100)
            .setDamage(30,10,5)
            .setEffectDuration(300,100,50)
            .setEffectStrength(10, 0, 1)
            .setEffectChance(20,5,5)
            .setAbilityTagModifiersRandom(dischargeRadius, 3, 1,true,1)
            .setAbilityTagModifiersRandom(totalBolts, 6, 2,true,1)
            .build();
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public int getCastType() {
        return PROJECTILE_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementRegistry.LIGHTNING.get();
    }
}
