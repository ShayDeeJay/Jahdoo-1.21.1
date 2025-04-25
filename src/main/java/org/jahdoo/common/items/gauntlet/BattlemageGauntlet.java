package org.jahdoo.common.items.gauntlet;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.registers.ComponentReg;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class BattlemageGauntlet extends Item implements ICurioItem, JahdooItem {

    public BattlemageGauntlet() {
        super(
            new Properties()
                .durability(300)
                .stacksTo(1)
        );
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }



    @Override
    public List<Component> getSlotsTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        return ICurioItem.super.getSlotsTooltip(tooltips, context, stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Helpers.withStyleComponent(super.getName(stack).getString(), ColourStore.GOLD_COIN);
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        var getRarityId = stack.get(ComponentReg.JAHDOO_RARITY);
        this.appendItemToolTips(stack, context, tooltips, false);
        if(getRarityId != null){
            var getRarity = JahdooRarity.getAllRarities().get(getRarityId);
            tooltips.addFirst(JahdooRarity.addRarityTooltip(getRarity, context.level()));
        }
        tooltips.add(Component.empty());
        tooltips.add(Helpers.withStyleComponent("Offhand Wands", ColourStore.SUB_HEADER_COLOUR));
        return tooltips;
    }

}
