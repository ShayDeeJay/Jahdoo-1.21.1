package org.jahdoo.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ability.rarity.JahdooRarity;
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

public class TomeOfUnity extends Item implements ICurioItem, JahdooItem {

    public TomeOfUnity() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        var list = new ArrayList<Component>();
        var getRarityId = stack.get(DataComponentRegistry.JAHDOO_RARITY);
        if(getRarityId != null){
            var getRarity = JahdooRarity.getAllRarities().get(getRarityId);
            list.addFirst(JahdooRarity.addRarityTooltip(getRarity));
        }
        list.add(Component.empty());
        for (var tooltip : tooltips) {
            var text = "attribute.name.jahdoo.mana.mana_regen";
            var contains = tooltip.contains(Component.translatable(text));
            var text1 = "attribute.name.jahdoo.mana.mana_pool";
            var contains1 = tooltip.contains(Component.translatable(text1));
            if(contains){
                var value = getValue(tooltip);
                list.add(ModHelpers.withStyleComponent("+" + value + "% ", AETHER_BLUE).copy().append(Component.translatable(text)));
            }

            if(contains1){
                var value = getValue(tooltip);
                list.add(ModHelpers.withStyleComponent(value + " ", AETHER_BLUE).copy().append(Component.translatable(text1)));
            }
        }
        return list;
    }

    private static @NotNull String getValue(Component tooltip) {
        return tooltip.plainCopy().toString().substring(93).replaceAll("\\D+$", "");
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
