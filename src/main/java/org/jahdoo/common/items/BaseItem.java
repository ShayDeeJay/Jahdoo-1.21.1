package org.jahdoo.common.items;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import java.util.List;

public abstract class BaseItem extends BaseJahdooItem implements JahdooItem {

    public BaseItem(Properties properties) {
        super(properties);
    }

    public void implicitModifiers(ItemStack stack, List<Component> tooltipComponents){
        tooltipComponents.add(Component.literal(" "));
        tooltipComponents.add(JahdooHelpers.withStyleComponentTrans("info.jahdoo.implicit_modifiers", ColourStore.SUB_HEADER_COLOUR));
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment != Enchantments.MENDING;
    }

    @Override
    public ItemStack applyEnchantments(ItemStack stack, List<EnchantmentInstance> enchantments) {
        enchantments.removeIf(s -> s.enchantment.equals(Enchantments.MENDING));
        return super.applyEnchantments(stack, enchantments);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        this.appendItemToolTips(stack, context, tooltipComponents, false);
        implicitModifiers(stack, tooltipComponents);
        bonusModifierTooltip(stack, tooltipComponents, context, true);
        enchantmentTooltip(stack, tooltipComponents, true, context.level());
        runeSpacer(stack, tooltipComponents);
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
