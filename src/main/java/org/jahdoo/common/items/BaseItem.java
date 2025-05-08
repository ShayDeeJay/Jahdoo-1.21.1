package org.jahdoo.common.items;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;

import java.util.List;

public abstract class BaseItem extends Item implements JahdooItem {

    public BaseItem(Properties properties) {
        super(properties);
    }

    public void implicitModifiers(ItemStack stack, List<Component> tooltipComponents){
        tooltipComponents.add(Component.literal(" "));
        tooltipComponents.add(Helpers.withStyleComponent("Implicit Modifiers", ColourStore.SUB_HEADER_COLOUR));
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment != Enchantments.MENDING;
    }

    @Override
    public ItemStack applyEnchantments(ItemStack stack, List<EnchantmentInstance> enchantments) {
        return ItemStack.EMPTY;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        this.appendItemToolTips(stack, context, tooltipComponents, false);
        implicitModifiers(stack, tooltipComponents);
        bonusModifierTooltip(stack, tooltipComponents, context, true);
        enchantmentTooltip(stack, tooltipComponents, true);
        runeSpacer(stack, tooltipComponents);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        for (var holderEntry : book.get(DataComponents.STORED_ENCHANTMENTS).entrySet()) {
            if(holderEntry.getKey().value().description().getString().contains("Mending")){
                return false;
            }
        }
        return true;
    }
}
