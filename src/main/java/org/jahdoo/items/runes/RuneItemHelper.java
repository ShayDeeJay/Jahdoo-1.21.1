package org.jahdoo.items.runes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.items.runes.rune_data.RuneData;
import org.jahdoo.items.augments.AugmentItemHelper;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.jahdoo.items.runes.rune_data.RuneData.RuneHelpers.generateRandomTypAttribute;
import static org.jahdoo.items.runes.rune_data.RuneData.RuneHelpers.standAloneAttributes;

public class RuneItemHelper {

    static void hoverToolTip(ItemStack stack, List<Component> tooltipComponents) {
        var component = standAloneAttributes(stack);
        var description = RuneData.RuneHelpers.getDescription(stack);
        var hasTier = RuneData.RuneHelpers.getTier(stack);
        var componentRune = JahdooRarity.attachRuneTierTooltip(stack);

        if(!component.getString().isEmpty()) {
            tooltipComponents.add(component);
            if (hasTier != -1) tooltipComponents.add(componentRune);
        }

        if(description.getString().isEmpty() || AugmentItemHelper.shiftForDetails(tooltipComponents)) return;
        tooltipComponents.add(ModHelpers.withStyleComponent(description.getString(), ColourStore.HEADER_COLOUR));
    }

    static @NotNull InteractionResultHolder<ItemStack> rollRandomRune(Level level, Player player) {
        if(!level.isClientSide){
            var stack = player.getMainHandItem();
            var newStack = stack.copyWithCount(1);
            stack.shrink(1);
            generateRandomTypAttribute(newStack, null);
            AugmentItemHelper.throwOrAddItem(player, newStack);
        }
        return InteractionResultHolder.fail(player.getMainHandItem());
    }

}
