package org.jahdoo.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.registers.DataComponentRegistry;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class ArchmageGauntlet extends Item implements ICurioItem {

    public ArchmageGauntlet() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        var getRarityId = stack.get(DataComponentRegistry.JAHDOO_RARITY);
        if(getRarityId != null){
            var getRarity = JahdooRarity.getAllRarities().get(getRarityId);
            tooltips.addFirst(JahdooRarity.addRarityTooltip(getRarity));
        }
        return ICurioItem.super.getAttributesTooltip(tooltips, context, stack);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}
