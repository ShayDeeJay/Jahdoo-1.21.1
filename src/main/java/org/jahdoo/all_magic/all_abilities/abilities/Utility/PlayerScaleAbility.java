package org.jahdoo.all_magic.all_abilities.abilities.Utility;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.all_magic.AbilityBuilder;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.JahdooRarity;
import org.jahdoo.capabilities.player_abilities.PlayerScale;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.components.DataComponentHelper;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.GlobalStrings;

import static org.jahdoo.all_magic.AbilityBuilder.COOLDOWN;

public class PlayerScaleAbility extends AbstractAbility {
    public static final ResourceLocation abilityId = ModHelpers.modResourceLocation("size_shifter");
    public static final String SCALE_VALUE = "Player Scale Value";

    @Override
    public void invokeAbility(Player player) {
        var value = DataComponentHelper.getSpecificValue(player).get(SCALE_VALUE).setValue();
        PlayerScale.setToggleEffect(player, (float) value);
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.COMMON;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setMana(10, 5, 1)
            .setModifier(COOLDOWN, 10, 10, true, 10)
            .setModifierWithStep(SCALE_VALUE, 2,-1, true, 2, 0.5)
            .build();
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_PLACER;
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
        return ElementRegistry.UTILITY.get();
    }
}
