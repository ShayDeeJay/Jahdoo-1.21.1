package org.jahdoo.items.magnet;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.items.JahdooItem;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.entities.EntityMovers.entityMover;
import static org.jahdoo.items.runes.rune_data.RuneData.RuneHelpers;
import static org.jahdoo.registers.DataComponentRegistry.MAGNET_DATA;
import static org.jahdoo.utils.ColourStore.*;
import static org.jahdoo.utils.ModHelpers.*;

public class Magnet extends Item implements ICurioItem, JahdooItem {

    public Magnet() {
        super(
            new Properties()
                .stacksTo(1)
                .durability(300)
                .component(MAGNET_DATA, MagnetData.DEFAULT)
        );
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        return new ArrayList<>();
    }

    //Changed true to display durability
    @Override
    public boolean isDamageable(ItemStack stack) {
        return true;
    }

    //Changed true to display durability
    @Override
    public boolean isDamaged(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> toolTips, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, toolTips, tooltipFlag);
        var list = stack.getAttributeModifiers().modifiers().stream().toList();
        var magnetData = MagnetData.getMagnetData(stack);
        if(!list.isEmpty()){
            for (var entry : list) toolTips.add(RuneHelpers.standAloneAttributes(entry));
        }
        toolTips.add(ModHelpers.withStyleComponent("Range: " + magnetData.range(), MAGNET_RANGE_GREEN));
        toolTips.add(ModHelpers.withStyleComponent("Strength: " + magnetData.strength(), MAGNET_STRENGTH_RED));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var switchActive = ModHelpers.getUsedItem(player);
        var magnetData = switchActive.get(MAGNET_DATA);

        if(magnetData == null) return InteractionResultHolder.fail(switchActive);
        MagnetData.updateActive(switchActive, !magnetData.active());
        var active = ModHelpers.withStyleComponent("Active", ColourStore.MAGNET_RANGE_GREEN);
        var deactivate = ModHelpers.withStyleComponent("Deactivated", ColourStore.MAGNET_STRENGTH_RED);
        player.displayClientMessage(!magnetData.active() ? active : deactivate, true);
        player.playSound(SoundRegister.SELECT.get());
        return super.use(level, player, usedHand);
    }

    @Override
    public List<Component> getSlotsTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        var rarity = JahdooRarity.attachRarityTooltip(stack, context.level());
        var newComp = new ArrayList<>(tooltips);

        newComp.add(rarity);
        if(!rarity.getString().isEmpty()) newComp.add(Component.empty());
        return newComp;
    }

    @Override
    public Component getName(ItemStack stack) {
        var type = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        var suffix = "Magnet";
        if(type == null) return withStyleComponent("Lesser " + suffix, SUB_HEADER_COLOUR);
        var id = type.value();
        var typeName = switch (id){
            case 1 -> "Simple ";
            case 2 -> "Greater ";
            case 3 -> "Perfect ";
            default -> "Ancient ";
        };
        var colour = id == 1 ? PERK_GREEN : id == 2 ? PENDENT_NAME : JahdooRarity.EPIC.getColour();

        return withStyleComponent(typeName + suffix, colour);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        var player = slotContext.entity();
        var level = player.level();
        var magnetData = MagnetData.getMagnetData(stack);
        var bounding = player.getBoundingBox().inflate(magnetData.range());
        var itemEntities = level.getEntitiesOfClass(ItemEntity.class, bounding);
        var expEntities = level.getEntitiesOfClass(ExperienceOrb.class, bounding);
        var durability = stackDurability(stack);
        var isPullingItem = !itemEntities.isEmpty() || !expEntities.isEmpty();

        if(magnetData.active() && durability > 0){
            if(isPullingItem) hurtAndKeepItem(stack, 1, level, player);
            for (var item : itemEntities) entityMover(player, item, magnetData.strength());
            for (var xp : expEntities) entityMover(player, xp, magnetData.strength());
        }

        ICurioItem.super.curioTick(slotContext, stack);
    }
}
