package org.jahdoo.common.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import org.jahdoo.common.registers.BlockReg;

import static org.jahdoo.trial_nexus.level_manager.LevelGenerator.LEVEL_PREFIX;

public class TrailNexusDimensionEvents {

    public static boolean blockInteractionRules(Level level, Player player){
        var isTrailNexus = level.getDescription().getString().contains(LEVEL_PREFIX);
        var isNotCreative = !player.isCreative();

        return isTrailNexus && isNotCreative;
    }

    public static void useItemBlockEvent(PlayerEvent.BreakSpeed event){
        var isAcceptableBlockType = event.getState().is(BlockReg.NEXITE_ORE);
        if(isAcceptableBlockType) return;

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
