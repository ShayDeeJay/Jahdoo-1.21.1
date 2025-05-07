package org.jahdoo.common.items.magnet;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.common.items.JahdooItem;
import org.jahdoo.common.registers.SoundReg;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.common.entities.EntityMovers.entityMover;
import static org.jahdoo.common.registers.ComponentReg.MAGNET_DATA;

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

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> toolTips, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, toolTips, tooltipFlag);
        var magnetData = MagnetData.getMagnetData(stack);
        this.appendItemToolTips(stack, context, toolTips, true);

        toolTips.add(Helpers.withStyleComponent("Range: " + magnetData.range(), MAGNET_RANGE_GREEN));
        toolTips.add(Helpers.withStyleComponent("Strength: " + Maths.roundNonWholeString(magnetData.strength()), MAGNET_STRENGTH_RED));
        bonusModifierTooltip(stack, toolTips, context, true);
        runeSpacer(stack, toolTips);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var switchActive = Helpers.getUsedItem(player);
        var magnetData = switchActive.get(MAGNET_DATA);

        if(magnetData == null) return InteractionResultHolder.fail(switchActive);
        MagnetData.updateActive(switchActive, !magnetData.active());
        var active = Helpers.withStyleComponent("Active", ColourStore.MAGNET_RANGE_GREEN);
        var deactivate = Helpers.withStyleComponent("Deactivated", ColourStore.MAGNET_STRENGTH_RED);
        player.displayClientMessage(!magnetData.active() ? active : deactivate, true);
        player.playSound(SoundReg.SELECT.get());
        return super.use(level, player, usedHand);
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
            case 4 -> "Ancient ";
            case 5 -> "Unique ";
            default -> "Lesser ";
        };

        var rarityColour = JahdooRarity.getAllRarities(id);

        return withStyleComponent(typeName + suffix, rarityColour.getColour());
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
            for (var item : itemEntities) {
                if(level instanceof ServerLevel){
                    if (!item.hasPickUpDelay()) {
                        entityMover(player, item, magnetData.strength());
                    }
                }
            }
            for (var xp : expEntities) entityMover(player, xp, magnetData.strength());
        }

        ICurioItem.super.curioTick(slotContext, stack);
    }
}
