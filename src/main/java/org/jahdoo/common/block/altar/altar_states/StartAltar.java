package org.jahdoo.common.block.altar.altar_states;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.trial_nexus.mobs.MobSpawnManager;

import java.util.HashSet;

import static org.jahdoo.common.block.altar.AltarAnim.onActivationAnim;
import static org.jahdoo.common.block.altar.AltarBlockEntity.roomBounding;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.level_manager.BlockSetupManager.blockExitBarrier;

public class StartAltar {

    public static void onStartAltar(AltarBlockEntity entity, ServerLevel serverLevel) {
        var pos = entity.getBlockPos();

        autoStartAltar(entity, serverLevel, pos);

        if(entity.getPrivateTicks() == 1) onActivationAnim(serverLevel, pos);
        if(entity.getPrivateTicks() == 30) MobSpawnManager.assignMobs(entity, entity.roomId);
    }

    private static void autoStartAltar(AltarBlockEntity aEntity, ServerLevel serverLevel, BlockPos pos) {
        if(!aEntity.started){
            var getWithBounding = roomBounding(pos);
            var entities = serverLevel.getEntities(null, getWithBounding);
            var players = entities.stream().filter(entity -> entity instanceof ServerPlayer).toList();
            var allPlayersJoined = new HashSet<>(players).containsAll(serverLevel.players());

            if(allPlayersJoined) startAltar(pos, aEntity, serverLevel);
        }
    }

    private static void startAltar(BlockPos pos, AltarBlockEntity altarE, ServerLevel serverLevel) {
        if(altarE.started) return;

        summonMobs(altarE);
        blockExitBarrier(serverLevel, pos);
        altarE.setData(INSTANCE_DATA, serverLevel.getData(INSTANCE_DATA));
    }

    private static void summonMobs(AltarBlockEntity altarE) {
        altarE.started = true;
        altarE.updateBlock();
    }

}
