package org.jahdoo.items.runes;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jahdoo.items.runes.rune_data.RuneData;
import org.jahdoo.items.JahdooItem;
import org.jahdoo.registers.DataComponentRegistry;

import java.util.List;

import static org.jahdoo.items.runes.rune_data.RuneData.RuneHelpers.getNameWithStyle;
import static org.jahdoo.items.runes.RuneItemHelper.hoverToolTip;
import static org.jahdoo.items.runes.RuneItemHelper.rollRandomRune;

public class RuneItem extends Item implements JahdooItem {
    public RuneItem() {
        super(new Properties().component(DataComponentRegistry.RUNE_DATA.get(), RuneData.DEFAULT));
    }

    @Override
    public Component getName(ItemStack stack) {
        return getNameWithStyle(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        hoverToolTip(stack, tooltipComponents);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return rollRandomRune(level, player);
    }

}
