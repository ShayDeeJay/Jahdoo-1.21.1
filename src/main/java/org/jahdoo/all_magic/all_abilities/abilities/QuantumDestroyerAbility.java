package org.jahdoo.all_magic.all_abilities.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.AbilityBuilder;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.JahdooRarity;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.utils.ModHelpers;

public class QuantumDestroyerAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = ModHelpers.modResourceLocation("quantum_destroyer");
    public static final String radius = "Energy Radius";
    public static final String gravitationalPull = "Gravitational Pull";

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
        fireProjectile(elementProjectile, player, 0.0f);
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
            .setMana(80, 40,  5)
            .setCooldown(2400, 800, 200)
            .setDamage(30, 10, 5)
            .setCastingDistance(30,10,5)
            .setLifetime(400, 200, 10)
            .setAbilityTagModifiersRandom(radius, 6,3, true, 1)
            .setAbilityTagModifiersRandom(gravitationalPull, 1.5,0.5, true, 0.2)
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
