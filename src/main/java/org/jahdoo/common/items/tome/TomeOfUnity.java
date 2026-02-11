package org.jahdoo.common.items.tome;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.items.BaseItem;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

import static org.jahdoo.common.items.caster_item.CasterItem.addSlot;
import static org.jahdoo.common.items.caster_item.CasterItem.removeSlot;

public class TomeOfUnity extends BaseItem implements ICurioItem {

    public TomeOfUnity() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public Component getName(ItemStack stack) {
        return TextHelpers.withStyleComponent(super.getName(stack).getString(), ColourHelpers.getPerkGreen());
    }

    @Override
    public void implicitModifiers(ItemStack stack, List<Component> toolTips) {
        super.implicitModifiers(stack, toolTips);
        var list = stack.getAttributeModifiers().modifiers().stream().toList();
        if(!list.isEmpty()){
            for (var entry : list) toolTips.add(RuneHelpers.standAloneAttributes(entry));
        }
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        addSlot(slotContext.entity(), "relic", JahdooHelpers.res("relics/new"), 2);
        ICurioItem.super.onEquip(slotContext, prevStack, stack);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        removeSlot(slotContext.entity(), "relic", JahdooHelpers.res("relics/new"));
        ICurioItem.super.onUnequip(slotContext, newStack, stack);
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
        return super.canEquip(stack, armorType, entity);
    }

}
