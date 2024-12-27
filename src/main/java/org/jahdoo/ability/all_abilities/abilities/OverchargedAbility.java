package org.jahdoo.ability.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.ability.AbilityBuilder;

public class OverchargedAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("overcharged");
    public static final String gravitationalPull = "Gravitational Pull";
    public static final String instability = "Instability";

    @Override
    public void invokeAbility(Player player) {
        fireProjectile(
            new ElementProjectile(
                EntitiesRegister.LIGHTNING_ELEMENT_PROJECTILE.get(),
                player,
                EntityPropertyRegister.OVERCHARGED.get().setAbilityId(),
                0,
                abilityId.getPath().intern()
            ),
            player,
            0.8f
        );
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.LEGENDARY;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setStaticMana(80)
            .setStaticCooldown(1200)
            .setDamage(40, 15, 5)
            .setEffectDuration(500, 100, 100)
            .setEffectStrength(10, 0, 1)
            .setEffectChance(30, 5, 5)
            .setGravitationalPull(2, 1, 0.2)
            .setAbilityTagModifiersRandom(instability, 7,3, false, 1)
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
