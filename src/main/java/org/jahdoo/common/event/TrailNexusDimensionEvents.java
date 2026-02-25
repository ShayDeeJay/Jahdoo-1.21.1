package org.jahdoo.common.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.utils.ModTags;

import static org.jahdoo.common.block.loot_pot.LootPotBlock.TEXTURE;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.BlockReg.LOOT_POT;

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

    public static void breakBlockEvent(BlockEvent.BreakEvent blockEvent){
        var state = blockEvent.getState();
        var player = blockEvent.getPlayer();
        getBlockExp(player, state);
    }

    public static void getBlockExp(Player player, BlockState state) {
        if(!(LevelGenerator.isNexus(player.level()))) return;

        if(player instanceof ServerPlayer serverPlayer){
            if (serverPlayer.level() instanceof ServerLevel level) {
                var instanceData = level.getData(INSTANCE_DATA.get());
                var base = getMineBlockExp(state);
                if(state.is(LOOT_POT)){
                    var potMultiplier = state.getValue(TEXTURE) + 1;
                    RunData.incrementPotsBroken(serverPlayer, instanceData.getDifficulty(), base * potMultiplier);
                } else {
                    RunData.incrementOres(serverPlayer, instanceData.getDifficulty(), base);
                }
            }
        }
    }

    public static void mineBlockEvent(PlayerEvent.BreakSpeed event){
        if(!(LevelGenerator.isNexus(event.getEntity().level()))) return;

        var isAcceptableBlockType = event.getState().is(ModTags.Block.MINEABLE_NEXUS);
        if(isAcceptableBlockType) return;

        if(blockInteractionRules(event.getEntity().level(), event.getEntity())){
            event.setCanceled(true);
        }
    }

    public static void mineBlockEvent(UseItemOnBlockEvent event){
        var player = event.getPlayer();
        if(player == null) return;
        if(blockInteractionRules(event.getLevel(), player)){
            player.getAbilities().mayBuild = false;
        }
    }

}
