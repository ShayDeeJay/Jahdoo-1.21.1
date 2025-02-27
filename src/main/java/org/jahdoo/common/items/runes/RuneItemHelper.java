package org.jahdoo.common.items.runes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.items.runes.rune_data.RuneData;
import org.jahdoo.common.items.augments.AugmentItemHelper;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.common.items.runes.rune_data.RuneData.RuneHelpers.generateRandomTypAttribute;
import static org.jahdoo.common.items.runes.rune_data.RuneData.RuneHelpers.standAloneAttributes;
import static org.jahdoo.ascension.utils.ColourStore.HEADER_COLOUR;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;

public class RuneItemHelper {

    static @NotNull InteractionResultHolder<ItemStack> rollRandomRune(
        Level level,
        Player player
    ) {
        var stack = Helpers.getUsedItem(player);
        if(!level.isClientSide){
            var newStack = stack.copyWithCount(1);
            stack.shrink(1);
            generateRandomTypAttribute(newStack, null);
            AugmentItemHelper.throwOrAddItem(player, newStack);
        }
        return InteractionResultHolder.fail(stack);
    }

    public static List<Component> hoverToolTip(ItemStack stack) {
        var tooltipComponents = new ArrayList<Component>();
        var component = standAloneAttributes(stack);
        var description = RuneData.RuneHelpers.getDescription(stack);
        var hasTier = RuneData.RuneHelpers.getTier(stack);
        var componentRune = JahdooRarity.attachRuneTierTooltip(stack);

        if(!component.getString().isEmpty()) {
            tooltipComponents.add(component);
            if (hasTier != -1) tooltipComponents.add(componentRune);
        }

        if(!description.getString().isEmpty() && !AugmentItemHelper.shiftForDetails(tooltipComponents)) {
            tooltipComponents.add(Helpers.withStyleComponent(description.getString(), ColourStore.HEADER_COLOUR));
        }

        var carriedRuneCost = String.valueOf(RuneData.RuneHelpers.getCostFromRune(stack));
        var carriedCostComponent = withStyleComponent(carriedRuneCost, -1);
        var potentialCostPreFix = withStyleComponent("Potential Cost: ", HEADER_COLOUR);
        tooltipComponents.add(potentialCostPreFix.copy().append(carriedCostComponent));
        return tooltipComponents;

    }

}
