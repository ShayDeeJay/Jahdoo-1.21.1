package org.jahdoo.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.items.wand.WandItemHelper;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.utils.ColourStore.*;
import static org.jahdoo.utils.ColourStore.AETHER_BLUE;
import static org.jahdoo.utils.ModHelpers.*;
import static org.jahdoo.utils.ModHelpers.Random;

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
        var data = stack.get(DataComponentRegistry.RUNE_HOLDER);
        if(data != null){
            WandItemHelper.appendRefinementPotential(tooltipComponents, stack);
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
