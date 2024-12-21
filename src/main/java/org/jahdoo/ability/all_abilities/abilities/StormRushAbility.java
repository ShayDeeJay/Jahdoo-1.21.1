package org.jahdoo.ability.all_abilities.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.all_abilities.abilities.raw_abilities.StormRush;
import org.jahdoo.ability.JahdooRarity;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;
import org.jahdoo.ability.AbilityBuilder;

public class StormRushAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = ModHelpers.res("storm_rush");
    public static final String launchDistance = "Launch Distance";

    @Override
    public void invokeAbility(Player player) {
        new StormRush(player).launchPlayerDirection();
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
            .setMana(30, 10,  5)
            .setCooldown(110, 10, 20)
            .setDamage(30, 10, 5)
            .setEffectChance(10, 1, 1)
            .setEffectDuration(300, 100, 50)
            .setEffectStrength(10, 1, 1)
            .setAbilityTagModifiersRandom(launchDistance, 3,1, true, 0.5)
            .build();
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_PLACER;
    }

    @Override
    public int getCastType() {
        return AREA_CAST;
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
