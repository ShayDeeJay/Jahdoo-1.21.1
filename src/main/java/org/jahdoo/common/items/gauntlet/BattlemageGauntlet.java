package org.jahdoo.common.items.gauntlet;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

public class BattlemageGauntlet extends Item implements ICurioItem, JahdooItem {

    public BattlemageGauntlet() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        var getRarityId = stack.get(ComponentReg.JAHDOO_RARITY);
        if(getRarityId != null){
            var getRarity = JahdooRarity.getAllRarities().get(getRarityId);
            tooltips.addFirst(JahdooRarity.addRarityTooltip(getRarity, context.level()));
        }
        var component = new ArrayList<Component>();
        component.add(Component.empty());
        component.add(Helpers.withStyleComponent("Allows the user to offhand wands", ColourStore.SUB_HEADER_COLOUR));
        return component;
    }

}
