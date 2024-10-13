package org.jahdoo.block.light_block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.all_magic.ElementProperties;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.particle.ParticleHandlers;

public class LightBlock extends Block {

    public LightBlock(Properties pProperties) {
        super(pProperties);
    }

    VoxelShape result = Block.box(6, 6, 6, 10, 10, 10);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return result;
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        super.animateTick(blockState, level, blockPos, randomSource);
        ElementProperties type;
        if(level.dimension() == Level.NETHER){
            type = ElementRegistry.INFERNO.get().getParticleGroup();
        } else if (level.dimension() == Level.END) {
            type = ElementRegistry.MYSTIC.get().getParticleGroup();
        } else {
            type = ElementRegistry.UTILITY.get().getParticleGroup();
        }
        ParticleHandlers.invisibleLight(level, blockPos.getCenter().subtract(0,0.1,0), type.bakedSlow(), 0.01, 0.02);
        ParticleHandlers.invisibleLight(level, blockPos.getCenter().subtract(0,0.1,0), type.genericSlow(), 0.02, 0.03);
    }

}
