package org.jahdoo.common.block.light_block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.element.AbstractElement;

import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
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
//        this.registerDefaultState(this.stateDefinition.any().setValue(TRIGGERED, false));s

    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
//        builder.add(TRIGGERED);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
//        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
//        level.scheduleTick(pos, state.getBlock(), 10);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {

    }

//    @Override
//    protected boolean isSignalSource(BlockState state) {
//        return true;
//    }

//    @Override
//    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
//        return 15;
//    }

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

        var pos = blockPos.getCenter().subtract(0, 0.05, 0);
        var lifetime = 10;
        var size = 0.8f;
        var bakedParticle = bakedParticle(type.id(), lifetime, size + 0.2f, false);
        var generic = ParticleHandlers.genericParticle(GENERIC_PARTICLE, lifetime, size, type.partColourA(), type.partColourFade(), false);

        ParticleHandlers.invisibleLight(level, pos, bakedParticle, 0.03, 0.04,50);
        ParticleHandlers.invisibleLight(level, pos, generic, 0.03, 0.04, 50);
    }

}
