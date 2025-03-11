package org.jahdoo.common.block.altar;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.common.registers.BlockEntityReg;

import static net.minecraft.world.ItemInteractionResult.FAIL;
import static net.minecraft.world.ItemInteractionResult.SUCCESS;
import static org.jahdoo.ascension.BlockSetupManager.blockExitBarrier;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.BlockReg.sharedBehaviour;

public class AltarBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE_BASE = Block.box(4.5, 5, 4.5, 11.5, 32, 11.5);
    private static final VoxelShape SHAPE_BASE_SECOND = Block.box(3.5, 0, 3.5, 12.5, 5, 12.5);
    private static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE_SECOND, SHAPE_BASE);

    public AltarBlock() {
        super(sharedBehaviour.strength(-1.0F, 3600000.0F));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new AltarBlock());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_COMMON;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AltarBlockEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(
            entityType,
            BlockEntityReg.CHALLENGE_ALTAR_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    @Override
    protected ItemInteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hitResult
    ) {
        if (!(level.getBlockEntity(pos) instanceof AltarBlockEntity altarE)) return FAIL;
        if (!(level instanceof ServerLevel)) return FAIL;

        blockExitBarrier(level, pos);

        altarE.setData(INSTANCE_DATA, level.getData(INSTANCE_DATA));

        if(!altarE.started){
//            var mobs = altarE.getInstanceData().getZombies();
            altarE.summonMobs();
            return SUCCESS;
        }

        return FAIL;
    }


}

