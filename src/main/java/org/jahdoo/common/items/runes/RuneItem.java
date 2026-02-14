package org.jahdoo.common.items.runes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jahdoo.common.items.BaseItem;
import org.jahdoo.common.items.runes.rune_data.RuneData;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.trial_nexus.ability.AbilityComponentHelper;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.ItemHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.common.items.runes.rune_data.RuneHelpers.getNameWithStyle;
import static org.jahdoo.common.items.runes.rune_data.RuneHelpers.standAloneAttributes;

public class RuneItem extends BaseItem {

    public RuneItem() {
        super(new Properties().component(ComponentReg.RUNE_DATA.get(), RuneData.DEFAULT));
    }

    @Override
    public int customRecycleChance(ItemStack itemStack) {
        return 100;
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
        var isNotBlank = !getNameWithStyle(stack).getString().contains("Blank");
        if(isNotBlank){
            tooltipComponents.addAll(hoverToolTip(stack, context, tooltipComponents));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
        Level level,
        Player player,
        InteractionHand usedHand
    ) {
        return InteractionResultHolder.fail(player.getItemInHand(usedHand));
    }

    static void randomRune(Player player, JahdooRarity tierRarity, JahdooRarity runeRarity) {
        var stack = JahdooHelpers.getUsedItem(player);
        if(!player.level().isClientSide){
            var newStack = stack.copyWithCount(1);
            stack.shrink(1);
            ItemHelpers.throwOrAddItem(player, newStack);
        }
    }

    public List<Component> hoverToolTip(ItemStack stack, TooltipContext context, List<Component> tooltips) {
        var tooltipComponents = new ArrayList<Component>();
        var component = standAloneAttributes(stack);
        var description = RuneHelpers.getDescription(stack);
        var hasTier = RuneHelpers.getTier(stack);
        var componentRune = JahdooRarity.attachRuneTierTooltip(stack);
        var carriedRuneCost = String.valueOf(RuneHelpers.getCostFromRune(stack));
        var carriedCostComponent = TextHelpers.withStyleComponent(carriedRuneCost, ColourHelpers.getDiamondBox());
        var potentialCostPreFix = TextHelpers.withStyleComponent("Potential Cost: ", ColourHelpers.getSubHeaderColour());

        if (!component.getString().isEmpty()) {
            tooltipComponents.add(potentialCostPreFix.copy().append(carriedCostComponent));
            if (hasTier != -1) tooltipComponents.add(componentRune);
            tooltipComponents.add(Component.empty());
            tooltipComponents.add(component);
        }

        if(!description.getString().isEmpty() && !AbilityComponentHelper.shiftForDetails(tooltipComponents, false)) {
            tooltipComponents.add(TextHelpers.withStyleComponent(description.getString(), ColourHelpers.getHeaderColour()));
        }

        return tooltipComponents;
    }

}
