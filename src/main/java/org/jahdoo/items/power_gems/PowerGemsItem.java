package org.jahdoo.items.power_gems;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jahdoo.ability.JahdooRarity;
import org.jahdoo.components.PowerGemData;
import org.jahdoo.items.augments.AugmentItemHelper;
import org.jahdoo.registers.DataComponentRegistry;

import java.util.List;

import static org.jahdoo.components.PowerGemData.PowerGemHelpers.*;

public class PowerGemsItem extends Item {
    public PowerGemsItem() {
        super(new Properties().component(DataComponentRegistry.POWER_GEM_DATA.get(), PowerGemData.DEFAULT));
    }

    @Override
    public Component getName(ItemStack stack) {
        return getNameWithStyle(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(JahdooRarity.attachGemRarityTooltip(stack));
//        tooltipComponents.add(Component.empty());
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
        return super.use(level, player, usedHand);
    }
}
