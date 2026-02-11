package org.jahdoo.common.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.utils.ModTags;

public class TrailNexusDimensionEvents {

    public static boolean blockInteractionRules(Level level, Player player){
        var isNotCreative = !player.isCreative();

        return LevelGenerator.isNexus(level) && isNotCreative;
    }

    public static void useItemBlockEvent(PlayerEvent.BreakSpeed event){
        var isAcceptableBlockType = event.getState().is(ModTags.Block.MINEABLE_NEXUS);
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
