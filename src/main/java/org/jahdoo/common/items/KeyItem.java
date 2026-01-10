package org.jahdoo.common.items;

import net.casual.arcade.dimensions.level.CustomLevel;
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
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.level_manager.StructureManager;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
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
        EXIT_KEY(KeyItem.EXIT_KEY, "Exit", ABSORPTION_YELLOW, StructureManager.EXIT_ROOM_COMPONENT),
        BAZAAR_KEY(KeyItem.BAZAAR_KEY, "Bazaar", AETHER_BLUE, StructureManager.BAZAAR_COMPONENT),
        CRYPT_KEY(KeyItem.CRYPT_KEY, "Crypt", UNIQUE_B, StructureManager.LOOT_CRYPT_COMPONENT),
        KEY_PIECE(KeyItem.KEY_PIECE, "Key", SUB_HEADER_COLOUR, Component.empty()),
        SANCTUARY_KEY(KeyItem.SANCTUARY_KEY, "Sanctuary", ElementReg.mystic().textColourA(), StructureManager.SANCTUARY_COMPONENT);

        private final int id;
        private final String name;
        private final int color;
        private final Component roomId;

        KeyTypes(
            int id,
            String name,
            int color,
            Component roomId
        ) {
            this.id = id;
            this.name = name;
            this.color = color;
            this.roomId = roomId;
        }

        public static KeyTypes getById(int id){
            for (var value : KeyTypes.values()) {
                if(value.id == id) return value;
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
        var data = stack.get(CUSTOM_MODEL_DATA);
        if(data != null){
            for (var value : KeyTypes.values()) {
                if (value.id == data.value()) return value;
            }
        }

        return KeyTypes.KEY_PIECE;
    }

    @Override
    public double customRecycleChance(ItemStack itemStack) {
        var getId = itemStack.get(CUSTOM_MODEL_DATA);
        if(getId == null) return -1;

        return (getId.value() + 1) * 20;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return super.use(level, player, usedHand);
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
        if(!stack.has(CUSTOM_MODEL_DATA)) return super.getName(stack);
        var getId = stack.get(CUSTOM_MODEL_DATA).value();
        var name = "";
        var colour = 0;

        if(getId < 4) {
            var getRarity = getLootRarity(getId);
            name = getRarity.getSerializedName();
            colour = getRarity.getColour();
        } else {
            var type = KeyTypes.getById(getId);
            name = type.name;
            colour = type.color;
        }

        return withStyleComponent(name + " " + (name.equals("Key") ? "Piece" : "Key"), colour);
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
