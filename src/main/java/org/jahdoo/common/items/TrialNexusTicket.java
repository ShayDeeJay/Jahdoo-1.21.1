package org.jahdoo.common.items;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jahdoo.common.components.CoreData;
import org.jahdoo.common.components.TicketData;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.registers.ComponentReg.CORE_DATA;
import static org.jahdoo.common.registers.ComponentReg.STORE_INTEGER;
import static org.jahdoo.trial_nexus.attachments.InstanceData.*;
import static org.jahdoo.trial_nexus.level_manager.LevelGenerator.createLevelAndStartingRoom;
import static org.jahdoo.trial_nexus.rarity.JahdooRarity.*;
import static org.jahdoo.trial_nexus.utils.ColourStore.*;
import static org.jahdoo.trial_nexus.utils.Helpers.*;
import static org.jahdoo.trial_nexus.utils.Maths.*;

public class TrialNexusTicket extends Item implements JahdooItem {

    public TrialNexusTicket() {
        super(
            new Properties()
                .component(ComponentReg.TICKET_DATA, new TicketData(new HashMap<>()))
                .component(STORE_INTEGER, 0)
                .component(CORE_DATA, new CoreData(100, 0))
        );
    }

    @Override
    public Component getName(ItemStack stack) {
        var type = "Trial Ticket";
        var getType = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        return getType == null ? withStyleComponent("Empty "+type, SUB_HEADER_COLOUR) :
        withStyleComponent(
            type, switch (getType.value()){
                case 1 -> JahdooRarity.RARE.getColour();
                case 2 -> JahdooRarity.EPIC.getColour();
                case 3 -> JahdooRarity.MYTHIC.getColour();
                case 4 -> JahdooRarity.COMMON.getColour();
                default ->  color(225, 176, 73);
            }
        );
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(CoreData.isFull(player.getItemInHand(usedHand)))
            player.startUsingItem(usedHand);

        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltips, TooltipFlag tooltipFlag) {
        var getType = stack.get(DataComponents.CUSTOM_MODEL_DATA);

        if(stack.has(CORE_DATA) && !CoreData.isFull(stack)){
            var current = CoreData.getFilled(stack);
            var max = CoreData.getRequired(stack);
            tooltips.add(Helpers.withStyleComponent(current + "/" + max, ColourStore.PERK_GREEN));
        }

        if(getType != null){
            var getTicketMods = stack.get(ComponentReg.TICKET_DATA);
            var getUses = stack.get(STORE_INTEGER);

            tooltips.add(withStyleComponent("Teleports you to the trial nexus", ColourStore.SUB_HEADER_COLOUR));
            usesTooltips(tooltips, getType.value(), getUses);
            if(getTicketMods != null && !getTicketMods.values().isEmpty()){
                tooltips.add(Component.literal(" "));
                tooltips.add(withStyleComponent("⏮ Trail Modifiers ⏭", ColourStore.OFF_WHITE));
            }
            modifierTooltips(tooltips, getTicketMods);
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity player, ItemStack stack, int remainingUseDuration) {
        if (!(player instanceof ServerPlayer serverPlayer) || level instanceof CustomLevel) return;
        if(player.isUsingItem()){
            var getCastTime = 80;
            var ticksUsingItem = serverPlayer.getTicksUsingItem();
            loading(player, serverPlayer, getCastTime, ticksUsingItem);
            onComplete(level, player, serverPlayer, ticksUsingItem, getCastTime);
            return;
        }

        player.stopUsingItem();
        super.onUseTick(level, player, stack, remainingUseDuration);
    }

    private static void usesTooltips(List<Component> tooltips, int getTypeValue, int getUseValue) {
        var prefix = withStyleComponent("Uses: ", HEADER_COLOUR);
        var useRemainingColour = colourByPercent(getTypeValue, getUseValue, true);
        var suffix = withStyleComponent(getUseValue + "/" + getTypeValue, useRemainingColour);
        tooltips.add(prefix.copy().append(suffix));
    }

    private static void comp(List<Component> tooltipComponents, @Nullable TicketData getTicketMods, String keyBronzeCoin, int colour, boolean isPercent) {
        if(getTicketMods != null && getTicketMods.get(keyBronzeCoin) > 0){
            var v = getTicketMods.get(keyBronzeCoin);
            tooltipComponents.add(withStyleComponent("+" + roundNonWholeString(doubleFormattedDouble(v)) + (isPercent ? "% " : " ") + stringIdToName(keyBronzeCoin), colour));
        }
    }

    private static void mobComp(List<Component> tooltipComponents, @Nullable TicketData getTicketMods, String keyBronzeCoin, boolean isPercent) {
        if(getTicketMods != null && getTicketMods.get(keyBronzeCoin) > 0){
            var v = getTicketMods.get(keyBronzeCoin);
            tooltipComponents.add(withStyleComponent("+" + roundNonWholeString(doubleFormattedDouble(v)) + (isPercent ? "% " : " ") + stringIdToName(keyBronzeCoin), NEGATIVE_RED));
        }
    }

    public static void modifierTooltips(List<Component> tooltips, TicketData getTicketMods) {
        if(getTicketMods != null && !getTicketMods.values().isEmpty()){
            var keyMaxTime = KEY_MAX_TIME;
            if (getTicketMods.get(keyMaxTime) > 0) {
                var v = getTicketMods.get(keyMaxTime);
                tooltips.add(withStyleComponent("+" + ticksToTime(v + "") + " " + Helpers.stringIdToName(keyMaxTime),  PERK_GREEN));
            }

            comp(tooltips, getTicketMods, KEY_BRONZE_COIN, ColourStore.BRONZE_COIN, false);
            comp(tooltips, getTicketMods, KEY_SILVER_COIN, ColourStore.SILVER_COIN, false);
            comp(tooltips, getTicketMods, KEY_GOLD_COIN, ColourStore.CHAMPION_GOLD, false);
            comp(tooltips, getTicketMods, KEY_COMMON_LOOT_MULTIPLIER, COMMON.getColour(), false);
            comp(tooltips, getTicketMods, KEY_RARE_LOOT_MULTIPLIER, RARE.getColour(), false);
            comp(tooltips, getTicketMods, KEY_LEGENDARY_LOOT_MULTIPLIER, LEGENDARY.getColour(), false);
            comp(tooltips, getTicketMods, KEY_MYTHIC_LOOT_MULTIPLIER, MYTHIC.getColour(), false);
            comp(tooltips, getTicketMods, KEY_QUEST_CRATE_MULTIPLIER, WALLET_BROWN, false);
            comp(tooltips, getTicketMods, KEY_SAFE_LOOT_MULTIPLIER, GOLD_COIN, false);

            mobComp(tooltips, getTicketMods, KEY_HEALTH, true);
            mobComp(tooltips, getTicketMods, KEY_ARMOR, true);
            mobComp(tooltips, getTicketMods, KEY_SPEED, true);
            mobComp(tooltips, getTicketMods, KEY_HORDE, false);
            mobComp(tooltips, getTicketMods, KEY_SKELETON, false);
            mobComp(tooltips, getTicketMods, KEY_ETERNAL_WIZARD, false);
            mobComp(tooltips, getTicketMods, KEY_VOID_SPIDER, false);
            mobComp(tooltips, getTicketMods, KEY_INFERNO_CREEPER, false);
        }
    }

    private static void onComplete(Level level, LivingEntity player, ServerPlayer serverPlayer, int ticksUsingItem, int getCastTime) {
        if(ticksUsingItem >= getCastTime){
            if (level instanceof ServerLevel serverLevel) {
                var preData = player.getMainHandItem().get(ComponentReg.TICKET_DATA);
                var dimTrans = createLevelAndStartingRoom(serverPlayer, serverLevel, preData != null ? preData.values() : new HashMap<>());
                var nPos = dimTrans.pos();
                var serverStack = serverPlayer.getItemInHand(player.getUsedItemHand());

                serverPlayer.teleportTo(dimTrans.newLevel(), nPos.x, nPos.y, nPos.z, 90, 0);
                serverPlayer.stopUsingItem();
                var i = serverStack.get(STORE_INTEGER);
                serverStack.set(STORE_INTEGER, i - 1);

                System.out.println(i);
                if(i-1 == 0) serverStack.shrink(1);
                serverPlayer.playNotifySound(SoundReg.START_TRIAL.get(), SoundSource.AMBIENT, 1, 1);
            }
        }
    }

    private static void loading(LivingEntity player, ServerPlayer serverPlayer, int getCastTime, int ticksUsingItem) {
        var castTimeSplit = getCastTime /3;
        if (ticksUsingItem % castTimeSplit == 0) {
            var i = ticksUsingItem / castTimeSplit;
            var countdown = 3 - i;
            var dots = new StringBuilder(".");

            dots.append(".".repeat(Math.max(0, i)));

            if(countdown != 0){
                serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(5, 10, 5));
                serverPlayer.connection.send(new ClientboundSetTitleTextPacket(withStyleComponent("Loading" + dots, ColourStore.OFF_WHITE)));
                serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(withStyleComponent(String.valueOf(countdown), ColourStore.UNIQUE_A)));
                getSoundWithPositionV(player.level(), player.position(), SoundReg.TIMER.get(), 0.5F, 0.8F);
            }
        }
    }

}
