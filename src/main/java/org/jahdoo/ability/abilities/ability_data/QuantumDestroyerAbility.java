package org.jahdoo.ability.abilities.ability_data;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbilityBuilder;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.utils.ModHelpers;

public class QuantumDestroyerAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("quantum_destroyer");
    public static final String radius = "Energy Radius";

    @Override
    public void invokeAbility(Player player) {
        ElementProjectile elementProjectile = new ElementProjectile(
            EntitiesRegister.MYSTIC_ELEMENT_PROJECTILE.get(),
            player,
            EntityPropertyRegister.QUANTUM_DESTROYER.get().setAbilityId(),
            0,
            abilityId.getPath().intern()
        );
        var position = player.pick(20, 0, false).getLocation();
        elementProjectile.moveTo(position.x, position.y + 0.3, position.z);
        fireProjectileNoSound(elementProjectile, player, 0.0f);
        player.level().playSound(null, BlockPos.containing(position), SoundRegister.ORB_FIRE.get(), SoundSource.NEUTRAL, 0.8f, 1.2f);
        player.level().playSound(null, BlockPos.containing(position), SoundRegister.MAGIC_EXPLOSION.get(), SoundSource.NEUTRAL, 0.2f, 1.4f);

    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.ETERNAL;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setStaticMana(160)
            .setStaticCooldown(6000)
            .setDamage(20, 10, 2)
            .setCastingDistance(30,10,5)
            .setLifetime(200, 100, 20)
            .setGravitationalPull(2,1, 0.2)
            .setAbilityTagModifiersRandom(radius, 6,3, true, 1)
            .build();
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public int getCastType() {
        return DISTANCE_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementRegistry.MYSTIC.get();
    }
}
