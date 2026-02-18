package org.jahdoo.trial_nexus.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.utils.ModTags;

public class SharedFireProperties {
    public static void fireTrailVegetationRemover(BlockState blockState, BlockPos blockPos, Entity entity){
        if (blockState.is(ModTags.Block.CAN_REPLACE_BLOCK) || blockState.canBeReplaced()) {
            if (!blockState.isAir() && !(LevelGenerator.isNexus(entity.level()))) return; {
                entity.level().destroyBlock(blockPos, false);
            }
        }
    }
}
