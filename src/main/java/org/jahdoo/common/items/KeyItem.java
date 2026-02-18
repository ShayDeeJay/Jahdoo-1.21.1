package org.jahdoo.common.items;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.level_manager.RoomData;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.List;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;

public class KeyItem extends BaseJahdooItem {

    public KeyItem() {
        super(new Properties());
    }

    @Override
    public int customRecycleChance(ItemStack itemStack) {
        var getId = itemStack.get(CUSTOM_MODEL_DATA);
        if(getId == null) return -1;

        return (getId.value() + 1) * 20;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var s = stack.get(ComponentReg.ID);
        var level = context.level();

        if(s != null && level != null){
            var equals = s.equals(level.getDescriptionKey());
            var prefix = TextHelpers.withStyleComponent("Usable Here: ", ColourHelpers.getSubHeaderColour()).copy();
            var valid = TextHelpers.withStyleComponent("" + equals, equals ? ColourHelpers.getMagnetRangeGreen() : ColourHelpers.getMagnetStrengthRed());
            tooltipComponents.add(prefix.append(valid));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public Component getName(ItemStack stack) {
        var type = RoomData.getByItem(stack);
        var colour = type.getColor();
        return TextHelpers.withStyleComponentTrans(super.getName(stack).getString(), colour);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(level instanceof CustomLevel cLevel && LevelGenerator.isNexus(cLevel)){
            stack.set(ComponentReg.ID, cLevel.getDescriptionKey());
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
