package org.jahdoo.common.items.armor;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import java.util.List;

public abstract class BaseArmor extends ArmorItem implements JahdooItem {

    public BaseArmor(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
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
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canGrindstoneRepair(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public float getXpRepairRatio(ItemStack stack) {
        return -1;
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        appendItemToolTips(stack, context, tooltipComponents, false);
        if(isItemBroken(stack)){
            brokenGearMessage(tooltipComponents, stack);
            return;
        }
        enchantmentTooltip(stack, tooltipComponents, true, context.level());
        tooltipComponents.add(Component.literal(" "));
        baseArmorTooltip(stack, tooltipComponents);
        bonusModifierTooltip(stack, tooltipComponents, context, true);
        runeSpacer(stack, tooltipComponents);
    }

    private static void baseArmorTooltip(ItemStack stack, List<Component> tooltipComponents) {
        tooltipComponents.add(JahdooHelpers.withStyleComponent("When Equipped", ColourStore.SUB_HEADER_COLOUR));
        for (var modifier : stack.getAttributeModifiers().modifiers()) {
            var value = org.shaydee.shaydeeapi.Maths.roundNonWholeString(modifier.modifier().amount());

            if(modifier.attribute() == Attributes.ARMOR){
                tooltipComponents.add(JahdooHelpers.withStyleComponent("+"+value+" Armor", ColourStore.MAGNET_STRENGTH_RED));
            }
            if(modifier.attribute() == Attributes.ARMOR_TOUGHNESS){
                tooltipComponents.add(JahdooHelpers.withStyleComponent("+"+value+" Armor Toughness", ColourStore.MAGNET_STRENGTH_RED));
            }
        }
    }

    @Override
    public int getDefense() {
        return super.getDefense();
    }

    @Override
    public float getToughness() {
        return super.getToughness();
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return super.getEquipmentSlot();
    }
}
