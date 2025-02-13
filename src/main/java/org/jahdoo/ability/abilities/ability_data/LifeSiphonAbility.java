package org.jahdoo.ability.abilities.ability_data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.ability.AbilityBuilder;

import static org.jahdoo.registers.EntitiesRegister.*;

public class LifeSiphonAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("life_siphon");
    public static final String HEAL_VALUE = "Heal Value";
    public static final String PULSES = "Pulse Multiplier";

    @Override
    public void invokeAbility(Player player) {
        var projectile = new ElementProjectile(
            VITALITY_ELEMENT_PROJECTILE.get(), player,
            EntityPropertyRegister.OVERCHARGED.get().setAbilityId(),
            offsetShoot(player), abilityId.getPath().intern()
        );
        fireProjectile(projectile, player, 0.8f);
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
            .setStaticMana(100)
            .setStaticCooldown(1200)
            .setDamage(20, 10, 2)
            .setRange(2.5, 1.5, 0.2)
            .setAbilityTagModifiersRandom(HEAL_VALUE, 1.5,0.5, true, 0.2)
            .setAbilityTagModifiersRandom(PULSES, 5,1, true, 1)
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
        return ElementRegistry.VITALITY.get();
    }
}
