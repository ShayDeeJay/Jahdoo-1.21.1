package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.JahdooRarity;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.all_magic.AbilityBuilder;
import org.jahdoo.components.DataComponentHelper;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;

public class BoltzAbility extends AbstractAbility {

    public static final ResourceLocation abilityId = ModHelpers.res("boltz");
    public static final String dischargeRadius = "Discharge Radius";
    public static final String totalBolts = "Total Boltz";

    @Override
    public void invokeAbility(Player player) {
        int totalShots = (int) DataComponentHelper.getSpecificValue(player, player.getMainHandItem(), totalBolts);
        Vec3 direction = player.getLookAngle();
        GenericParticleOptions particleOptions = genericParticleOptions(ParticleStore.ELECTRIC_PARTICLE_SELECTION, this.getElemenType(), 5, 1.2f, 0.5);
        genericParticleOptions(this.getElemenType(), 30, 1f);

        for (int i = 0; i < totalShots; i++) {
            ElementProjectile elementProjectile = new ElementProjectile(
                EntitiesRegister.LIGHTNING_ELEMENT_PROJECTILE.get(),
                player,
                EntityPropertyRegister.BOLTZ.get().setAbilityId(),
                0,
                abilityId.getPath().intern()
            );
            double spread = 0.9; // Adjust the spread value as needed
            double spreadX = direction.x + (Math.random() - 0.5) * spread;
            double spreadY = direction.y + (Math.random() - 0.5) * spread;
            double spreadZ = direction.z + (Math.random() - 0.5) * spread;
            elementProjectile.shoot(spreadX, spreadY, spreadZ, 0.5f, 0);
            elementProjectile.setOwner(player);
            player.level().addFreshEntity(elementProjectile);
        }

        for(int i = 0; i < totalShots * 20; i++){
            double spread = 0.8; // Adjust the spread value as needed
            double spreadX = direction.x + (Math.random() - 0.5) * spread;
            double spreadY = direction.y + (Math.random() - 0.5) * spread;
            double spreadZ = direction.z + (Math.random() - 0.5) * spread;
            ParticleHandlers.sendParticles(player.level(), particleOptions, player.position().add(0,1.5,0), 0, spreadX, spreadY, spreadZ, 1);

        }

        ModHelpers.getSoundWithPosition(player.level(), player.blockPosition(), SoundRegister.ORB_CREATE.get(), 0.5f,1.5f);
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.UNCOMMON;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setMana(30,10,5)
            .setCooldown(400,100,50)
            .setDamage(30,15,5)
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
