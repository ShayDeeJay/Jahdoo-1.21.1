package org.jahdoo.ascension.ability.abilities.storm_rush;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.ability.AbilityBuilder;

public class StormRushAbility extends AbilityRegistrar {
    public static final ResourceLocation abilityId = Helpers.res("storm_rush");
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
            .setStaticMana(30)
            .setStaticCooldown(100)
            .setDamage(30, 10, 5)
            .setEffectChance(10, 1, 1)
            .setEffectDuration(300, 100, 50)
            .setEffectStrength(10, 1, 1)
            .setAbilityTagModifiersRandom(launchDistance, 2.5,1.5, true, 0.2)
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
        return ElementRegistry.FROST.get();
    }
}
