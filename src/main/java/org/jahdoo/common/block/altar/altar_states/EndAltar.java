package org.jahdoo.common.block.altar.altar_states;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.common.block.loot_pot.LootPotBlockEntity;
import org.jahdoo.common.entities.safe.Safe;
import org.jahdoo.common.event.TriggerEvents;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.level_manager.BlockSetupManager;
import org.jahdoo.trial_nexus.utils.ModTags;
import org.shaydee.shaydeeapi.Colours;
import org.shaydee.shaydeeapi.Helpers;

import static org.jahdoo.common.block.altar.AltarBlockEntity.getAllBlockPos;
import static org.jahdoo.common.block.altar.AltarBlockEntity.roomBounding;
import static org.jahdoo.trial_nexus.attachments.RunData.incrementRoomExp;
import static org.jahdoo.trial_nexus.level_manager.BlockSetupManager.setCoinLootChest;
import static org.jahdoo.trial_nexus.level_manager.StructureManager.placeLocksWithData;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.withStyleComponentTrans;

public class EndAltar {

    public static void onEndAlter(AltarBlockEntity aEntity, ServerLevel serverLevel) {
        if (aEntity.getPrivateTicks() > 30 && aEntity.started && aEntity.onField.isEmpty() && aEntity.spawnableMobs.isEmpty()) {
            var pos = aEntity.getBlockPos();
            serverLevel.destroyBlock(pos, false);

            placeLocksWithData(serverLevel, pos.below(2), false, false);
            oneEndAltarNotification(serverLevel, pos);
            removeLootAndOres(serverLevel, pos);
            manageEndEntities(aEntity, serverLevel);
            setEndFocusBlock(aEntity, serverLevel, pos);

            aEntity.data().incrementClearedRooms();
        }
    }

    private static void manageEndEntities(AltarBlockEntity aEntity, ServerLevel serverLevel) {
        for (var entity : serverLevel.getEntities().getAll()) {
            if(entity instanceof Safe safe) safe.kill();
            if(entity instanceof ServerPlayer player) incrementRoomExp(player, aEntity.data().getDifficulty());
        }
    }

    public static void oneEndAltarNotification(ServerLevel serverLevel, BlockPos pos) {
        Helpers.getSoundWithPosition(serverLevel, pos, SoundReg.END_TRIAL.get(), SoundSource.BLOCKS, 2F, 1.5F);
        for (var player : serverLevel.players()) {
            var connection = player.connection;
            connection.send(new ClientboundSetTitlesAnimationPacket(5, 20, 20));
            connection.send(new ClientboundSetTitleTextPacket(withStyleComponentTrans("info.jahdoo.altar_complete", Colours.getMagnetRangeGreen())));
            TriggerEvents.triggerRoomClearEvent(player, serverLevel);
        }
    }

    private static void setEndFocusBlock(AltarBlockEntity aEntity, ServerLevel serverLevel, BlockPos pos) {
        var clearedRooms = aEntity.data().getClearedRooms();
        var interval = aEntity.getInstanceDifficulty().getSanctumIntervals();

        if(clearedRooms % interval == 0){
            BlockSetupManager.setPerkTable(serverLevel, pos, 4);
            return;
        }

        setCoinLootChest(serverLevel, pos, aEntity.direction, -1, true, clearedRooms-1);
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
