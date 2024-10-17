package org.jahdoo.block.light_block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.ElementProperties;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.particle.ParticleHandlers;

import static org.jahdoo.particle.ParticleStore.SOFT_PARTICLE_SELECTION;

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
        AbstractElement type;
        if(level.dimension() == Level.NETHER){
            type = ElementRegistry.INFERNO.get();
        } else if (level.dimension() == Level.END) {
            type = ElementRegistry.MYSTIC.get();
        } else {
            type = ElementRegistry.UTILITY.get();
        }

        var bakedParticle = new BakedParticleOptions(type.getTypeId(), 16, 1, false);
        var generic = new GenericParticleOptions(SOFT_PARTICLE_SELECTION, type.particleColourPrimary(), type.particleColourFaded(), 16, 0.5f, false, 0.05);

        ParticleHandlers.invisibleLight(level, blockPos.getCenter().subtract(0,0.1,0), bakedParticle, 0.01, 0.02);
        ParticleHandlers.invisibleLight(level, blockPos.getCenter().subtract(0,0.1,0), generic, 0.02, 0.03);
    }


}
