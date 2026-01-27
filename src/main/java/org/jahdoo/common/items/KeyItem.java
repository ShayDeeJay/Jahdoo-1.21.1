package org.jahdoo.common.items;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.level_manager.StructureManager;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static org.jahdoo.trial_nexus.utils.ColourStore.*;
import static org.jahdoo.trial_nexus.utils.Helpers.withStyleComponent;

public class KeyItem extends Item implements JahdooItem {

    public static final int EXIT_KEY = 5;
    public static final int BAZAAR_KEY = 6;
    public static final int CRYPT_KEY = 7;
    public static final int SANCTUARY_KEY = 8;
    public static final int KEY_PIECE = 9;


    public enum KeyTypes {
        EXIT_KEY("exit_key", ABSORPTION_YELLOW, StructureManager.EXIT_ROOM_COMPONENT),
        BAZAAR_KEY("bazaar_key", AETHER_BLUE, StructureManager.BAZAAR_COMPONENT),
        CRYPT_KEY("crypt_key", UNIQUE_B, StructureManager.LOOT_CRYPT_COMPONENT),
        KEY_PIECE("key_piece", SUB_HEADER_COLOUR, Component.empty()),
        SANCTUARY_KEY("sanctuary_key", ElementReg.mystic().textColourA(), StructureManager.SANCTUARY_COMPONENT);

        private final String id;
        private final int color;
        private final Component roomId;

        KeyTypes(
            String id,
            int color,
            Component roomId
        ) {
            this.id = id;
            this.color = color;
            this.roomId = roomId;
        }

        public static KeyTypes getById(String id){
            for (var value : KeyTypes.values()) {
                if(id.contains(value.id)) return value;
            }
            return KEY_PIECE;
        }

        public Component getRoomId(){
            return roomId;
        }
    }

    public KeyItem() {
        super(new Properties());
    }

    public static KeyTypes isLockKey(ItemStack stack){
        for (var value : KeyTypes.values()) {
            if (stack.getDescriptionId().contains(value.id)) return value;
        }

        return KeyTypes.KEY_PIECE;
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
            var prefix = withStyleComponent("Usable Here: ", SUB_HEADER_COLOUR).copy();
            var valid = withStyleComponent("" + equals, equals ? MAGNET_RANGE_GREEN : MAGNET_STRENGTH_RED);
            tooltipComponents.add(prefix.append(valid));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public Component getDescription() {
        return Helpers.withStyleComponent("Once keys enter the nexus, they will be bound to that instance.", SUB_HEADER_COLOUR);
    }

    @Override
    public Component getName(ItemStack stack) {
        var type = KeyTypes.getById(stack.getDescriptionId());
        var colour = type.color;
        return withStyleComponent(Helpers.stringIdToName(type.id), colour);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(level instanceof CustomLevel customLevel){
            stack.set(ComponentReg.ID, customLevel.getDescriptionKey());
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
