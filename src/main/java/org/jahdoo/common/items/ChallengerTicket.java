package org.jahdoo.common.items;

import net.casual.arcade.dimensions.level.CustomLevel;
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
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.registers.SoundReg;

import java.util.List;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.trial_nexus.level_manager.LevelGenerator.createLevelAndStartingRoom;
import static org.jahdoo.trial_nexus.utils.Helpers.getSoundWithPositionV;

public class ChallengerTicket extends Item implements JahdooItem {

    public ChallengerTicket() { super(new Properties()); }

    @Override
    public Component getName(ItemStack stack) {
        return Helpers.withStyleComponent("Challenger Ticket", color(225, 176, 73));
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
        player.startUsingItem(usedHand);
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.empty());
        tooltipComponents.add(Helpers.withStyleComponent("Teleports you to the challenger arena", ColourStore.OFF_WHITE));
        tooltipComponents.add(Helpers.withStyleComponent("! Consumed on use !", ColourStore.NEGATIVE_RED));
    }

    @Override
    public void onUseTick(Level level, LivingEntity player, ItemStack stack, int remainingUseDuration) {
        if (!(player instanceof ServerPlayer serverPlayer) || level instanceof CustomLevel) return;

        if(player.isUsingItem()){
            var getCastTime = 80;
            var ticksUsingItem = serverPlayer.getTicksUsingItem();
            var castTimeSplit = getCastTime/3;
            if (ticksUsingItem % castTimeSplit == 0) {
                var i = ticksUsingItem / castTimeSplit;
                var countdown = 3 - i;
                var dots = new StringBuilder(".");

                dots.append(".".repeat(Math.max(0, i)));

                if(countdown != 0){
                    serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(5, 10, 5));
                    serverPlayer.connection.send(new ClientboundSetTitleTextPacket(Helpers.withStyleComponent("Loading" + dots, ColourStore.OFF_WHITE)));
                    serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(Helpers.withStyleComponent(String.valueOf(countdown), ColourStore.UNIQUE_A)));
                    getSoundWithPositionV(player.level(), player.position(), SoundReg.TIMER.get(), 0.5F, 0.8F);
                }
            }

            if(ticksUsingItem >= getCastTime){
                if (level instanceof ServerLevel serverLevel) {
                    var dimTrans = createLevelAndStartingRoom(serverPlayer, serverLevel);
                    var nPos = dimTrans.pos();
                    serverPlayer.teleportTo(dimTrans.newLevel(), nPos.x, nPos.y, nPos.z, 90, 0);
                    serverPlayer.stopUsingItem();
                    serverPlayer.getItemInHand(player.getUsedItemHand()).shrink(1);
                    serverPlayer.playNotifySound(SoundReg.START_TRIAL.get(), SoundSource.AMBIENT, 1, 1);
//                    RunData.addNewQuest(serverPlayer);
                }
            }
        }

        super.onUseTick(level, player, stack, remainingUseDuration);
    }

}
