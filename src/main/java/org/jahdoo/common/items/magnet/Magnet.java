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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.common.items.BaseItem;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.MathHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.common.entities.EntityMovers.entityMover;
import static org.jahdoo.common.registers.ComponentReg.MAGNET_DATA;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.hurtAndKeepItem;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.stackDurability;

public class Magnet extends BaseItem implements ICurioItem {
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
    public void implicitModifiers(ItemStack stack, List<Component> toolTips) {
        super.implicitModifiers(stack, toolTips);
        var magnetData = MagnetData.getMagnetData(stack);
        toolTips.add(TextHelpers.withStyleComponent("Range: " + magnetData.range(), ColourHelpers.getMagnetRangeGreen()));
        toolTips.add(TextHelpers.withStyleComponent("Strength: " + MathHelpers.roundNonWholeString(magnetData.strength()), ColourHelpers.getMagnetStrengthRed()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var switchActive = JahdooHelpers.getUsedItem(player);
        var magnetData = switchActive.get(MAGNET_DATA);

        if(magnetData == null) return InteractionResultHolder.fail(switchActive);
        MagnetData.updateActive(switchActive, !magnetData.active());
        var active = TextHelpers.withStyleComponent("Active", ColourHelpers.getMagnetRangeGreen());
        var deactivate = TextHelpers.withStyleComponent("Deactivated", ColourHelpers.getMagnetStrengthRed());
        player.displayClientMessage(!magnetData.active() ? active : deactivate, true);
        player.playSound(SoundReg.SELECT.get());
        return super.use(level, player, usedHand);
    }

    @Override
    public Component getName(ItemStack stack) {
        var type = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        var suffix = "Magnet";
        if(type == null) return TextHelpers.withStyleComponent("Lesser " + suffix, ColourHelpers.getSubHeaderColour());
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

        return TextHelpers.withStyleComponent(typeName + suffix, rarityColour.getColour());
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        var player = slotContext.entity();
        var level = player.level();
        if(!(level instanceof ServerLevel serverLevel)) return;

        var magnetData = MagnetData.getMagnetData(stack);
        var bounding = player.getBoundingBox().inflate(magnetData.range());
        var itemEntities = serverLevel.getEntitiesOfClass(ItemEntity.class, bounding);
        var expEntities = serverLevel.getEntitiesOfClass(ExperienceOrb.class, bounding);
        var durability = stackDurability(stack);
        var isPullingItem = !itemEntities.isEmpty() || !expEntities.isEmpty();

        if(magnetData.active() && durability > 0){
            if(isPullingItem) hurtAndKeepItem(stack, 1, serverLevel, player);
            for (var item : itemEntities) {
                if (!item.hasPickUpDelay()) {
                    entityMover(player, item, magnetData.strength());
                }
            }
            for (var xp : expEntities) entityMover(player, xp, magnetData.strength());
        }

        ICurioItem.super.curioTick(slotContext, stack);
    }
}
