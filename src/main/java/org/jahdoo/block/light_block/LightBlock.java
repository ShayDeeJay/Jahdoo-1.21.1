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

import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
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
        AbstractElement type;
        if(level.dimension() == Level.NETHER){
            type = ElementRegistry.INFERNO.get();
        } else if (level.dimension() == Level.END) {
            type = ElementRegistry.MYSTIC.get();
        } else {
            type = ElementRegistry.UTILITY.get();
        }

        var pos = blockPos.getCenter().subtract(0,0.05,0);
        var lifetime = 10;
        var size = 0.8f;
        var bakedParticle = bakedParticleOptions(type.getTypeId(), lifetime, size + 0.2f, false);
        var generic = genericParticleOptions(GENERIC_PARTICLE_SELECTION, lifetime, size, type.particleColourPrimary(), type.particleColourFaded(), false);

        ParticleHandlers.invisibleLight(level, pos, bakedParticle, 0.03, 0.04,50);
        ParticleHandlers.invisibleLight(level, pos, generic, 0.03, 0.04, 50);
    }


}
