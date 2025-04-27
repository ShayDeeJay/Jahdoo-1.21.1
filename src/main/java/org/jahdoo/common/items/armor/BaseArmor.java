package org.jahdoo.common.items.armor;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;

import java.util.List;

public class BaseArmor extends ArmorItem implements JahdooItem {
    public BaseArmor(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        this.appendItemToolTips(stack, context, tooltipComponents, false);
        var empty = Component.literal(" ");

        for (var modifier : stack.getAttributeModifiers().modifiers()) {
            if(modifier.attribute() == Attributes.ARMOR){
                tooltipComponents.add(empty);
                tooltipComponents.add(RuneHelpers.standAloneAttributes(modifier));
            }
            if(modifier.attribute() == Attributes.ARMOR_TOUGHNESS){
                tooltipComponents.add(RuneHelpers.standAloneAttributes(modifier));
            }
        }

        tooltipComponents.add(empty);
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
