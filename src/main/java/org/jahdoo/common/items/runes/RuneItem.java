package org.jahdoo.common.items.runes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jahdoo.ascension.ability.AbilityComponentHelper;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.items.runes.rune_data.RuneData;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import org.jahdoo.common.registers.ComponentReg;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.ascension.utils.ColourStore.HEADER_COLOUR;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.common.items.runes.rune_data.RuneHelpers.getNameWithStyle;
import static org.jahdoo.common.items.runes.rune_data.RuneHelpers.standAloneAttributes;

public class RuneItem extends Item implements JahdooItem {
    public RuneItem() {
        super(new Properties().component(ComponentReg.RUNE_DATA.get(), RuneData.DEFAULT));
    }

    @Override
    public Component getName(ItemStack stack) {
        return getNameWithStyle(stack);
    }

    @Override
    public void appendHoverText(
        ItemStack stack,
        TooltipContext context,
        List<Component> tooltipComponents,
        TooltipFlag tooltipFlag
    ) {
        tooltipComponents.addAll(hoverToolTip(stack));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
        Level level,
        Player player,
        InteractionHand usedHand
    ) {
//        return rollRandomRune(level, player);
        return InteractionResultHolder.fail(player.getItemInHand(usedHand));
    }

//    static @NotNull InteractionResultHolder<ItemStack> rollRandomRune(
//        Level level,
//        Player player
//    ) {
//        var stack = Helpers.getUsedItem(player);
//        if(!level.isClientSide){
//            var newStack = stack.copyWithCount(1);
//            stack.shrink(1);
//            generateRandomTypAttribute(newStack, null);
//            Helpers.throwOrAddItem(player, newStack);
//        }
//        return InteractionResultHolder.fail(stack);
//    }

    public static List<Component> hoverToolTip(ItemStack stack) {
        var tooltipComponents = new ArrayList<Component>();
        var component = standAloneAttributes(stack);
        var description = RuneHelpers.getDescription(stack);
        var hasTier = RuneHelpers.getTier(stack);
        var componentRune = JahdooRarity.attachRuneTierTooltip(stack);

        if(!component.getString().isEmpty()) {
            tooltipComponents.add(component);
            if (hasTier != -1) tooltipComponents.add(componentRune);
        }

        if(!description.getString().isEmpty() && !AbilityComponentHelper.shiftForDetails(tooltipComponents, true)) {
            tooltipComponents.add(Helpers.withStyleComponent(description.getString(), ColourStore.HEADER_COLOUR));
        }

        var carriedRuneCost = String.valueOf(RuneHelpers.getCostFromRune(stack));
        var carriedCostComponent = withStyleComponent(carriedRuneCost, -1);
        var potentialCostPreFix = withStyleComponent("Potential Cost: ", HEADER_COLOUR);
        tooltipComponents.add(potentialCostPreFix.copy().append(carriedCostComponent));
        return tooltipComponents;

    }

}
