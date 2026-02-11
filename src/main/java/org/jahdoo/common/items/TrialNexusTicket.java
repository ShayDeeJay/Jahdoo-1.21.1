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
import org.jahdoo.common.registers.mod.LevelBoonReg;
import org.jahdoo.trial_nexus.boon.level_boons.AbstractLevelBoon;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.registers.ComponentReg.CORE_DATA;
import static org.jahdoo.common.registers.ComponentReg.STORE_INTEGER;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_MAX_TIME;
import static org.jahdoo.trial_nexus.level_manager.LevelGenerator.createLevelAndStartingRoom;
import static org.jahdoo.trial_nexus.rarity.JahdooRarity.*;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.getSoundWithPositionV;

public class TrialNexusTicket extends Item implements JahdooItem {

    public static final List<Integer> colourWithRarity= List.of(
        color(225, 176, 73),
        RARE.getColour(),
        EPIC.getColour(),
        MYTHIC.getColour(),
        COMMON.getColour()
    );

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
        var getType = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        var name = super.getName(stack).getString();
        var colour = getType == null ? ColourHelpers.getSubHeaderColour() : colourWithRarity.get(getType.value()-1);

        return TextHelpers.withStyleComponent(name, colour);
    }

    @Override
    public String descriptionId() {
        return "description.item.jahdoo.ticket";
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var itemInHand = player.getItemInHand(usedHand);
        if(CoreData.isFull(itemInHand)) player.startUsingItem(usedHand);
        return super.use(level, player, usedHand);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltips, TooltipFlag tooltipFlag) {
        var getType = stack.get(DataComponents.CUSTOM_MODEL_DATA);

        if(getType != null){
            var getTicketMods = stack.get(ComponentReg.TICKET_DATA);
            var getUses = stack.get(STORE_INTEGER).intValue();

            tooltips.add(TextHelpers.withStyleComponentTrans("info.jahdoo.ticket.teleport", ColourHelpers.getSubHeaderColour()));
            tooltips.add(Component.literal(" "));
            appendCapacity(tooltips, stack);
            usesTooltips(tooltips, getType.value(), getUses);

            modifierTooltips(tooltips, getTicketMods, stack);
        }
    }

    public static void appendCapacity(List<Component> toolTips, ItemStack stack) {

        if(stack.has(CORE_DATA) && !CoreData.isFull(stack)){
            var current = CoreData.getFilled(stack);
            var max = CoreData.getRequired(stack);
            var capacity = TextHelpers.withStyleComponentTrans("info.jahdoo.capacity", ColourHelpers.getSubHeaderColour());
            var capacity1 = TextHelpers.withStyleComponent(current + "/" + max, ColourHelpers.colourByPercent(max, current, true));
            var append = capacity.copy().append(capacity1);
            toolTips.add(append);
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
        var prefix = TextHelpers.withStyleComponent("Uses: ", ColourHelpers.getHeaderColour());
        var useRemainingColour = ColourHelpers.colourByPercent(getTypeValue, getUseValue, true);
        var suffix = TextHelpers.withStyleComponent(getUseValue + "/" + getTypeValue, useRemainingColour);
        tooltips.add(prefix.copy().append(suffix));
    }

    private static void comp(List<Component> tooltipComponents, @Nullable TicketData getTicketMods, String key, boolean isPercent) {
        var getBoon = LevelBoonReg.fromId(key).orElseThrow();
        var colour = getBoon.getHeaderColour();
        if(getTicketMods != null && getTicketMods.get(key) > 0){
            var v = getTicketMods.get(key);
            var s = Objects.equals(key, KEY_MAX_TIME) ? org.shaydee.shaydeeapi.Maths.ticksToTime(v + "") : org.shaydee.shaydeeapi.Maths.roundNonWholeString(org.shaydee.shaydeeapi.Maths.doubleFormattedDouble(v));
            tooltipComponents.add(TextHelpers.withStyleComponent("+" + s + (isPercent ? "% " : " ") + TextHelpers.stringIdToName(key), colour));
        }
    }

    public static void modifierTooltips(List<Component> tooltips, TicketData getTicketMods, ItemStack stack) {
        if(getTicketMods != null && !getTicketMods.values().isEmpty()){
            var hasPos = 0;
            for (var s : getTicketMods.values().keySet()) {
                hasPos += getFromReg(LevelBoonReg.positiveFromId(s), tooltips, getTicketMods, s, hasPos);
            }
            if(hasPos > 0) {
                tooltips.add(tooltips.size() - hasPos, TextHelpers.withStyleComponentTrans("info.jahdoo.ticket.positive_modifiers", ColourHelpers.getSubHeaderColour()));
            }

            var hasNeg = 0;
            for (var s : getTicketMods.values().keySet()) {
                hasNeg += getFromReg(LevelBoonReg.negativeFromId(s), tooltips, getTicketMods, s, hasNeg);
            }
            if(hasNeg > 0) {
                tooltips.add(tooltips.size() - hasNeg, Component.literal(" "));
                tooltips.add(tooltips.size() - hasNeg, TextHelpers.withStyleComponentTrans("info.jahdoo.ticket.negative_modifiers", ColourHelpers.getHeaderColour()));
            }
        }
    }

    public static int getFromReg(Optional<AbstractLevelBoon> boonOptional, List<Component> tooltips, TicketData getTicketMods, String key, int counter){
        if(boonOptional.isPresent()){
            var boon = boonOptional.get();
            comp(tooltips, getTicketMods, key, boon.isPercentageOf());
            return 1;
        }
        return 0;
    }

    private static void onComplete(Level level, LivingEntity player, ServerPlayer serverPlayer, int ticksUsingItem, int getCastTime) {
        if(ticksUsingItem >= getCastTime){
            if (level instanceof ServerLevel serverLevel) {
                var dimTrans = createLevelAndStartingRoom(serverPlayer, serverLevel);
                var nPos = dimTrans.pos();

                serverPlayer.teleportTo(dimTrans.newLevel(), nPos.x, nPos.y, nPos.z, 90, 0);
                serverPlayer.stopUsingItem();

                var preData = player.getMainHandItem().get(ComponentReg.TICKET_DATA);
                if(preData != null){
                    var buffs = preData.values() ;
                    for (var buff : buffs.entrySet()) {
                        var getBoonNeg = LevelBoonReg.fromId(buff.getKey());
                        getBoonNeg.ifPresent(b -> b.execute(dimTrans.newLevel(), buff.getValue()));
                    }
                }

                var serverStack = serverPlayer.getItemInHand(player.getUsedItemHand());
                var i = serverStack.get(STORE_INTEGER).intValue();
                serverStack.set(STORE_INTEGER, i - 1);

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
                serverPlayer.connection.send(new ClientboundSetTitleTextPacket(TextHelpers.withStyleComponent("Loading" + dots, ColourHelpers.getOffWhite())));
                serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(TextHelpers.withStyleComponent(String.valueOf(countdown), ColourHelpers.getOffWhite())));
                getSoundWithPositionV(player.level(), player.position(), SoundReg.TIMER.get(), 0.5F, 0.8F);
            }
        }
    }

}
