package org.jahdoo.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.entities.EntityMovers;
import org.jahdoo.items.runes.rune_data.RuneData;
import org.jahdoo.items.wand.WandItemHelper;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.utils.ColourStore.*;
import static org.jahdoo.utils.ModHelpers.*;

public class Magnet extends Item implements ICurioItem, JahdooItem {

    public Magnet() {
        super(new Properties().stacksTo(1).durability(300));
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        return new ArrayList<>();
    }

    //Changes true to display durability
    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    //Changes true to display durability
    @Override
    public boolean isDamaged(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(JahdooRarity.attachRarityTooltip(stack, (int) context.level().getGameTime()));
        tooltipComponents.add(Component.empty());
        var list = stack.getAttributeModifiers().modifiers().stream().toList();
        if(!list.isEmpty()){
            for (var entry : list) {
                tooltipComponents.add(RuneData.RuneHelpers.standAloneAttributes(entry));
            }
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

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        var player = slotContext.entity();
        var level = player.level();
        var range = 15;
        var strength = 2;
        var bounding = player.getBoundingBox().inflate(range);
        var itemEntities = level.getEntitiesOfClass(ItemEntity.class, bounding);
        var expEntities = level.getEntitiesOfClass(ExperienceOrb.class, bounding);
        var durability = stackDurability(stack);

        if(durability > 0){
            var isPullingItem = !itemEntities.isEmpty() || !expEntities.isEmpty();
            if(isPullingItem) {
                hurtAndKeepItem(stack, 1, level, player);
            }

            for (var item : itemEntities) {
                EntityMovers.entityMover(player, item, strength);
            }

            for (var xp : expEntities) {
                EntityMovers.entityMover(player, xp, strength);
            }
        }

        ICurioItem.super.curioTick(slotContext, stack);
    }
}
