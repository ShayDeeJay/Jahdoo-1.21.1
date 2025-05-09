package org.jahdoo.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jahdoo.ascension.attachments.CasterData;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.Helpers;

import java.util.List;

public class StoneOfRegret extends Item implements JahdooItem {

    public StoneOfRegret() {
        super(new Properties());
    }

    @Override
    public Component getName(ItemStack stack) {
        return Helpers.withStyleComponent(super.getName(stack).getString(), JahdooRarity.EPIC.getColour());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Helpers.withStyleComponent("Reset all abilities and skills.", Helpers.getColourLight(JahdooRarity.EPIC.getColour(), 1.4)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var item = player.getItemInHand(usedHand);
        CasterData.regretAbilities(player);
        item.shrink(1);
        return super.use(level, player, usedHand);
    }

}

