package org.jahdoo.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

public class BattlemageGauntlet extends Item implements ICurioItem, JahdooItem {

    public BattlemageGauntlet() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        var getRarityId = stack.get(DataComponentRegistry.JAHDOO_RARITY);
        if(getRarityId != null){
            var getRarity = JahdooRarity.getAllRarities().get(getRarityId);
            tooltips.addFirst(JahdooRarity.addRarityTooltip(getRarity, context.level()));
        }
        var component = new ArrayList<Component>();
        component.add(Component.empty());
        component.add(ModHelpers.withStyleComponent("Allows the user to offhand wands", ColourStore.SUB_HEADER_COLOUR));
        return component;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

}
