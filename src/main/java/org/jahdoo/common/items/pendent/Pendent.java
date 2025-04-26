package org.jahdoo.common.items.pendent;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.items.caster_item.CasterItemHelper;
import org.jahdoo.common.registers.ComponentReg;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.*;

public class Pendent extends Item implements ICurioItem, JahdooItem {

    public Pendent() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        return new ArrayList<>();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        var data = stack.get(ComponentReg.RUNE_HOLDER);
        if(data != null){
            CasterItemHelper.appendPotentialComponent(tooltipComponents, stack);
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        var type = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        var suffix = "Rune Amulet";
        if(type == null) return withStyleComponent("Lesser " + suffix, SUB_HEADER_COLOUR);
        var id = type.value();
        var typeName = switch (id){
            case 2 -> "Greater " + suffix;
            case 3 -> "Ancient " + suffix;
            default -> "Simple " + suffix;
        };
        return withStyleComponent(typeName, id == 1 ? PERK_GREEN : id == 2 ? PENDENT_NAME : JahdooRarity.EPIC.getColour());
    }

}
