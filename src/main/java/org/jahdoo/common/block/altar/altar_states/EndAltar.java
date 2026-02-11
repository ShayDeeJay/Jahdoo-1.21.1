package org.jahdoo.common.block.altar.altar_states;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.common.block.loot_pot.LootPotBlockEntity;
import org.jahdoo.common.entities.safe.Safe;
import org.jahdoo.common.event.TriggerEvents;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.level_manager.BlockSetupManager;
import org.jahdoo.trial_nexus.level_manager.InstanceDifficulty;
import org.jahdoo.trial_nexus.utils.ModTags;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import static org.jahdoo.common.block.altar.AltarBlockEntity.getAllBlockPos;
import static org.jahdoo.common.block.altar.AltarBlockEntity.roomBounding;
import static org.jahdoo.trial_nexus.attachments.RunData.incrementRoomExp;
import static org.jahdoo.trial_nexus.level_manager.BlockSetupManager.setCoinLootChest;
import static org.jahdoo.trial_nexus.level_manager.StructureManager.placeLocksWithData;

public class EndAltar {

    public static void onEndAlter(AltarBlockEntity aEntity, ServerLevel serverLevel) {
        if (aEntity.getPrivateTicks() > 30 && aEntity.started && aEntity.onField.isEmpty() && aEntity.spawnableMobs.isEmpty()) {
            var pos = aEntity.getBlockPos();
            serverLevel.destroyBlock(pos, false);
            aEntity.data().incrementClearedRooms();

            placeLocksWithData(serverLevel, pos.below(2), false, false);
            sendAlterEndNotification(serverLevel, pos);
            removeLootAndOres(serverLevel, pos);
            manageEndEntities(aEntity, serverLevel);
            setEndFocusBlock(aEntity.data(), serverLevel, pos, aEntity.direction);
        }
    }

    private static void manageEndEntities(AltarBlockEntity aEntity, ServerLevel serverLevel) {
        for (var entity : serverLevel.getEntities().getAll()) {
            if(entity instanceof Safe safe) safe.kill();
            if(entity instanceof ServerPlayer player) incrementRoomExp(player, aEntity.data().getDifficulty());
        }
    }

    public static void sendAlterEndNotification(ServerLevel serverLevel, BlockPos pos) {
        sendNotification(serverLevel, pos, SoundReg.END_TRIAL.get(), TextHelpers.withStyleComponentTrans("info.jahdoo.altar_complete", ColourHelpers.getMagnetRangeGreen()));
    }

    public static void sendNotification(ServerLevel serverLevel, BlockPos pos, SoundEvent event, Component component) {
        SoundHelpers.getSoundWithPosition(serverLevel, pos, event, SoundSource.BLOCKS, 2F, 1.5F);
        for (var player : serverLevel.players()) {
            var connection = player.connection;
            connection.send(new ClientboundSetTitlesAnimationPacket(5, 20, 20));
            connection.send(new ClientboundSetTitleTextPacket(component));
            TriggerEvents.triggerRoomClearEvent(player, serverLevel);
        }
    }

    public static void setEndFocusBlock(InstanceData data, ServerLevel serverLevel, BlockPos pos, Direction direction) {
        var clearedRooms = data.getClearedRooms();
        var interval = InstanceDifficulty.getFromLevel(data).getSanctumIntervals();

        System.out.println(clearedRooms);
        System.out.println(interval);
        if(clearedRooms % interval == 0){
            BlockSetupManager.setPerkTable(serverLevel, pos, 4);
            return;
        }

        setCoinLootChest(serverLevel, pos, direction, -1, true, clearedRooms);
    }

    public static void removeLootAndOres(ServerLevel serverLevel, BlockPos pos) {
        var box = roomBounding(pos);
        var bounding = getAllBlockPos(box);
        for (BlockPos blockPos : bounding) {
            var state = serverLevel.getBlockState(blockPos);

            if (state.is(BlockReg.LOOT_POT)) {
                if(serverLevel.getBlockEntity(blockPos) instanceof LootPotBlockEntity potBlock){
                    potBlock.setTheItem(ItemStack.EMPTY);
                    serverLevel.destroyBlock(blockPos, false);
                }
            }

            if (state.is(ModTags.Block.MINEABLE_NEXUS)) serverLevel.destroyBlock(blockPos, false);
        }
    }

}
