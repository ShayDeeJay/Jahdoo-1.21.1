package org.jahdoo.common.items.armor;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.items.runes.rune_data.RuneHolder;

import java.util.List;

import static org.jahdoo.common.items.caster_item.CasterItemHelper.bonusModifierTooltip;

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
        return ItemStack.EMPTY;
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var empty = Component.literal(" ");
        appendItemToolTips(stack, context, tooltipComponents, false);
        enchantmentTooltip(stack, tooltipComponents, empty);
        tooltipComponents.add(empty);
        baseArmorTooltip(stack, tooltipComponents);
        bonusModifierTooltip(stack, tooltipComponents, context, true);
        runeSpacer(stack, tooltipComponents, empty);

    }

    private static void runeSpacer(ItemStack stack, List<Component> tooltipComponents, MutableComponent empty) {
        var runeHolder = RuneHolder.getRuneholder(stack);
        if(runeHolder != null && !runeHolder.runeSlots().isEmpty()){
            tooltipComponents.add(empty);
        }
    }

    private static void baseArmorTooltip(ItemStack stack, List<Component> tooltipComponents) {
        tooltipComponents.add(Helpers.withStyleComponent("When Equipped", ColourStore.SUB_HEADER_COLOUR));
        for (var modifier : stack.getAttributeModifiers().modifiers()) {
            var value = Maths.roundNonWholeString(modifier.modifier().amount());

            if(modifier.attribute() == Attributes.ARMOR){
                tooltipComponents.add(Helpers.withStyleComponent("+"+value+" Armor", ColourStore.MAGNET_STRENGTH_RED));
            }
            if(modifier.attribute() == Attributes.ARMOR_TOUGHNESS){
                tooltipComponents.add(Helpers.withStyleComponent("+"+value+" Armor Toughness", ColourStore.MAGNET_STRENGTH_RED));
            }
        }
    }

    private static void enchantmentTooltip(ItemStack stack, List<Component> tooltipComponents, MutableComponent empty) {
        var itemEnchantments = stack.get(DataComponents.ENCHANTMENTS);
        if(itemEnchantments != null && !itemEnchantments.entrySet().isEmpty()){
            tooltipComponents.add(empty);
            tooltipComponents.add(Helpers.withStyleComponent("Enchantments", ColourStore.SUB_HEADER_COLOUR));
            for (var holderEntry : itemEnchantments.entrySet()) {
                var value = holderEntry.getKey().value();
                var string = value.description().getString();
                var s = JahdooRarity.romanNumeralConverter(holderEntry.getIntValue() - 1);
                tooltipComponents.add(Helpers.withStyleComponent(string + " " + s, ColourStore.NETHERITE_BOX));
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
