package org.jahdoo.items.runes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.*;
import org.jahdoo.ability.JahdooRarity;
import org.jahdoo.components.RuneData;
import org.jahdoo.items.augments.AugmentItemHelper;
import org.jahdoo.registers.DataComponentRegistry;

import java.util.List;

    import static org.jahdoo.components.RuneData.RuneHelpers.*;

public class RuneItem extends Item {
    public RuneItem() {
        super(new Properties().component(DataComponentRegistry.RUNE_DATA.get(), RuneData.DEFAULT));
    }

    @Override
    public Component getName(ItemStack stack) {
        return getNameWithStyle(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(JahdooRarity.attachRuneRarityTooltip(stack));
        tooltipComponents.add(standAloneAttributes(stack));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(!level.isClientSide){
            var stack = player.getMainHandItem();
            var newStack = stack.copyWithCount(1);
            stack.shrink(1);
            generateRandomTypAttribute(newStack);
            AugmentItemHelper.throwOrAddItem(player, newStack);
        }
        return InteractionResultHolder.fail(player.getMainHandItem());
    }

}
