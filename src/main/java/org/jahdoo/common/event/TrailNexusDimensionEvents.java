package org.jahdoo.common.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import org.jahdoo.common.networking.server2client.RunDataS2CP;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.utils.ModTags;

import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.AttachmentReg.RUN_DATA;

public class TrailNexusDimensionEvents {

    public static boolean blockInteractionRules(Level level, Player player){
        var isNotCreative = !player.isCreative();

        return LevelGenerator.isNexus(level) && isNotCreative;
    }

    public static int getMineBlockExp(BlockState blockState){
        if(blockState.is(ModTags.Block.CHEAP_BLOCK)) return 5;
        if(blockState.is(ModTags.Block.MID_RANGE_BLOCK)) return 10;
        if(blockState.is(ModTags.Block.VALUABLE_BLOCK)) return 30;
        return 0;
    }

    public static void useItemBlockEvent(PlayerEvent.BreakSpeed event){

        var state = event.getState();
        var isAcceptableBlockType = state.is(ModTags.Block.MINEABLE_NEXUS);
        var player = event.getEntity();
        if(isAcceptableBlockType && player instanceof ServerPlayer serverPlayer) {
            if(serverPlayer.level() instanceof ServerLevel level){
                var runData = player.getData(RUN_DATA.get());
                var instanceData = level.getData(INSTANCE_DATA.get());

                runData.setExperienceGained(instanceData.getDifficulty(), getMineBlockExp(state));
                sendToPlayer(serverPlayer, new RunDataS2CP(runData));
                return;
            }
        }
        if(blockInteractionRules(event.getEntity().level(), event.getEntity())){
            event.setCanceled(true);
        }
    }

    public static void useItemBlockEvent(UseItemOnBlockEvent event){
        var player = event.getPlayer();
        if(player == null) return;
        if(blockInteractionRules(event.getLevel(), player)){
            player.getAbilities().mayBuild = false;
        }
    }

}
