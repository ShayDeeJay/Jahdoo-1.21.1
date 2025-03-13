package org.jahdoo.ascension.ability.abilities.boltz;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.EntityDataReg;
import org.jahdoo.common.registers.EntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.ability.AbilityBuilder;

import static org.jahdoo.common.components.DataComponentHelper.getSpecificValue;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.ascension.utils.Helpers.getSoundWithPosition;

public class BoltzAbility extends AbilityRegistrar {

    private static final String totalBolts = "Total Boltz";
    public static final ResourceLocation abilityId = Helpers.res("boltz");
    public static final String dischargeRadius = "Discharge Radius";

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
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
        return ElementReg.frost();
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setStaticMana(50)
            .setStaticCooldown(200)
            .setDamage(30,10,5)
            .setEffectDuration(300,100,50)
            .setEffectStrength(10, 0, 1)
            .setEffectChance(20,5,5)
            .setAbilityTagModifiersRandom(dischargeRadius, 3, 1,true,1)
            .setAbilityTagModifiersRandom(totalBolts, 6, 2,true,1)
            .build();
    }

    @Override
    public void invokeAbility(Player player) {
        var amplifier = 1;
        var totalShots = (int) getSpecificValue(player, Helpers.getUsedItem(player), totalBolts) * amplifier;
        var direction = player.getLookAngle();
        var particleOptions = ParticleHandlers.genericParticle(ParticleStore.ELECTRIC_PARTICLE, this.getElemenType(), 5, 1.2f, 0.5);

        for (int i = 0; i < totalShots; i++) {
            var elementProjectile = new ElementProjectile(
                EntityReg.FROST_ELEMENT_PROJECTILE.get(),
                player,
                EntityDataReg.BOLTZ.get().setAbilityId(),
                0,
                abilityId.getPath().intern()
            );
            var spread =  0.8 * amplifier; // Adjust the spread value as needed
            var spreadX = direction.x + (Math.random() - 0.5) * spread;
            var spreadY = direction.y + (Math.random() - 0.5) * spread;
            var spreadZ = direction.z + (Math.random() - 0.5) * spread;

            fireProjectileDirection(elementProjectile, player, 0.5f, new Vec3(spreadX, spreadY, spreadZ));
        }

        for(int i = 0; i < totalShots * 5; i++){
            var spread = 0.8; // Adjust the spread value as needed
            var spreadX = direction.x + (Math.random() - 0.5) * spread;
            var spreadY = direction.y + (Math.random() - 0.5) * spread;
            var spreadZ = direction.z + (Math.random() - 0.5) * spread;
            sendParticles(player.level(), particleOptions, player.position().add(0,1.5,0), 0, spreadX, spreadY, spreadZ, 1);
        }

        getSoundWithPosition(player.level(), player.blockPosition(), SoundReg.ORB_CREATE.get(), 0.5f,1.5f);
    }

}
