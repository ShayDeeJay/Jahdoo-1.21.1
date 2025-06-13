package org.jahdoo.common.items;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ItemReg;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.jahdoo.trial_nexus.utils.ColourStore.*;
import static org.jahdoo.trial_nexus.utils.Helpers.withStyleComponent;

public class KeyItem extends Item implements JahdooItem {

    public KeyItem() {
        super(new Properties());
    }

    @Override
    public double customRecycleChance(ItemStack itemStack) {
        var getId = itemStack.get(DataComponents.CUSTOM_MODEL_DATA);
        if(getId == null) return -1;

        return (getId.value() + 1) * 20;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var s = stack.get(ComponentReg.ID);
        var level = context.level();
        if(s != null && level != null){
            var equals = s.equals(level.getDescriptionKey());
            var prefix = withStyleComponent("Usable Here: ", SUB_HEADER_COLOUR).copy();
            var valid = withStyleComponent("" + equals, equals ? MAGNET_RANGE_GREEN : MAGNET_STRENGTH_RED);
            tooltipComponents.add(prefix.append(valid));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public Component getName(ItemStack stack) {
        var getId = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if(stack.is(ItemReg.EXIT_KEY)) return withStyleComponent(super.getName(stack).getString(), ABSORPTION_TEXT_YELLOW);
        if(getId == null) return super.getName(stack);
        var getRarity = getJahdooRarity(getId);

        return withStyleComponent(getRarity.getSerializedName() + " Key", getRarity.getColour());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(!stack.has(ComponentReg.ID)){
            if(level instanceof CustomLevel customLevel){
                stack.set(ComponentReg.ID, customLevel.getDescriptionKey());
            }
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    public static @NotNull JahdooRarity getJahdooRarity(CustomModelData getId) {
        return getLootRarity(getId.value());
    }

    public static @NotNull JahdooRarity getLootRarity(int getId) {
        return switch (getId) {
            case 1 -> JahdooRarity.RARE;
            case 2 -> JahdooRarity.LEGENDARY;
            case 3 -> JahdooRarity.MYTHIC;
            default -> JahdooRarity.COMMON;
        };
    }

    public static boolean isValidKey(ItemStack stack, Level level){
        var getKeyId = stack.get(ComponentReg.ID);
        return getKeyId != null && getKeyId.equals(level.getDescriptionKey());
    }

}
