package org.jahdoo.common.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jahdoo.common.components.TicketData;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;

import java.util.Arrays;
import java.util.List;

import static org.jahdoo.trial_nexus.attachments.InstanceData.*;
import static org.jahdoo.trial_nexus.utils.Helpers.Random;

public class Stamp extends Item implements JahdooItem{

    public Stamp() {
        super(new Properties());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var item = player.getItemInHand(usedHand);

        if(!level.isClientSide){
            var value = Random.nextInt(1, 11);
            item.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(value));
            StampType.addBoon(value, item, JahdooRarity.getRarity());
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public Component getName(ItemStack stack) {
        var type = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        var name = super.getName(stack);
        if(type == null) return name;

        var value = type.value();
        var fromId = StampType.fromIndex(value-1);
        var withName = Helpers.stringIdToName(fromId.getName())+" "+ name.getString();
        var color = fromId.getColor();
        return Helpers.withStyleComponent(withName, color);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var getTicketMods = stack.get(ComponentReg.TICKET_DATA);
        TrialNexusTicket.modifierTooltips(tooltipComponents, getTicketMods);
    }

    public enum StampType {

        BRONZE_COIN("bronze_coin", ColourStore.BRONZE_COIN, KEY_BRONZE_COIN),
        SILVER_COIN("silver_coin", ColourStore.SILVER_COIN, KEY_SILVER_COIN),
        GOLD_COIN("gold_coin", ColourStore.GOLD_COIN, KEY_GOLD_COIN),
        COMMON_CHEST("common_chest", JahdooRarity.COMMON.getColour(), KEY_COMMON_LOOT_MULTIPLIER),
        RARE_CHEST("rare_chest", JahdooRarity.RARE.getColour(), KEY_RARE_LOOT_MULTIPLIER),
        LEGENDARY_CHEST("legendary_chest", JahdooRarity.LEGENDARY.getColour(), KEY_LEGENDARY_LOOT_MULTIPLIER),
        MYTHIC_CHEST("mythic_chest", JahdooRarity.MYTHIC.getColour(), KEY_MYTHIC_LOOT_MULTIPLIER),
        SAFE("safe", ColourStore.MAGNET_RANGE_GREEN, KEY_SAFE_LOOT_MULTIPLIER),
        QUEST_CRATE("quest_crate", ColourStore.WALLET_BROWN, KEY_QUEST_CRATE_MULTIPLIER),
        TIME("time", ColourStore.DIAMOND_BOX, KEY_MAX_TIME);

        private final String name;
        private final int color;
        private final String boonKey;

        StampType(
            String name,
            int color,
            String boonKey
        ) {
            this.name = name;
            this.color = color;
            this.boonKey = boonKey;
        }

        public int getColor() {
            return color;
        }

        public String getName() {
            return name;
        }

        public String getBoonKey() {
            return boonKey;
        }

        public static void addBoon(int id, ItemStack itemStack, JahdooRarity rarity){
            var value = switch (id){
                case 1,2,3 -> Math.max((int) rarity.getAttributes().getRandomCooldown(), 1);
                case 10 -> rarity.getAttributes().getRandomTime();
                default -> 1;
            };
            TicketData.addNewEntry(itemStack, fromIndex(id-1).boonKey, value);
        }

        public static StampType fromIndex(int id){
            var list = Arrays.stream(StampType.values()).toList();
            return list.get(Math.min(id, list.size()-1));
        }
    }

}
