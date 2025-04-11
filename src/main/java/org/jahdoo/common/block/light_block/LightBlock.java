package org.jahdoo.common.block.light_block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.particle.ParticleHandlers;

import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;

public class LightBlock extends Block {

    private static final VoxelShape RESULT = Block.box(6, 6, 6, 10, 10, 10);

    public LightBlock() {
        super(
            BlockBehaviour.Properties
                .ofFullCopy(Blocks.TORCH)
                .noCollission()
                .instabreak()
                .lightLevel((blockState) -> 15)
        );
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return RESULT;
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        AbstractElement type;

        if(level.dimension() == Level.NETHER){
            type = ElementReg.inferno();
        } else if (level.dimension() == Level.END) {
            type = ElementReg.mystic();
        } else {
            type = ElementReg.utility();
        }

        var pos = blockPos.getCenter().subtract(0,0.05,0);
        var lifetime = 10;
        var size = 0.8f;
        var bakedParticle = bakedParticle(type.id(), lifetime, size + 0.2f, false);
        var generic = ParticleHandlers.genericParticle(GENERIC_PARTICLE, lifetime, size, type.partColourA(), type.partColourFade(), false);

        ParticleHandlers.invisibleLight(level, pos, bakedParticle, 0.03, 0.04,50);
        ParticleHandlers.invisibleLight(level, pos, generic, 0.03, 0.04, 50);
    }


}
