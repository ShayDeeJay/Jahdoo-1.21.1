package org.jahdoo.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.items.runes.rune_data.RuneData;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.utils.ColourStore.AETHER_BLUE;
import static org.jahdoo.utils.ColourStore.PERK_GREEN;

public class TomeOfUnity extends RelicItem {

    public TomeOfUnity() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        var list = new ArrayList<Component>();
        var lists = stack.getAttributeModifiers().modifiers().stream().toList();
        list.add(Component.empty());
        if(!lists.isEmpty()){
            for (var entry : lists) {
                tooltips.add(RuneData.RuneHelpers.standAloneAttributes(entry));
            }
        }
        return list;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        var list = stack.getAttributeModifiers().modifiers().stream().toList();
        if(!list.isEmpty()){
            for (var entry : list) {
                tooltipComponents.add(RuneData.RuneHelpers.standAloneAttributes(entry));
            }
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        var name = super.getName(stack).getString();
        return ModHelpers.withStyleComponent(name, ColourStore.SUB_HEADER_COLOUR);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}
