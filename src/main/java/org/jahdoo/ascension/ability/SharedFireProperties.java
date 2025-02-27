package org.jahdoo.ascension.ability;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ascension.utils.ModTags;

public class SharedFireProperties {
    public static void fireTrailVegetationRemover(BlockState blockState, BlockPos blockPos, Entity entity){
        if (blockState.is(ModTags.Block.CAN_REPLACE_BLOCK) || blockState.canBeReplaced()) {
            if (!blockState.isAir() && !(entity.level() instanceof CustomLevel)) {
                entity.level().destroyBlock(blockPos, false);
            }
        }
    }
}
