package org.jahdoo.common.block.altar;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.ascension.ability.effects.JahdooMobEffect;
import org.jahdoo.ascension.utils.Helpers;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.ItemInteractionResult.*;
import static org.jahdoo.common.block.altar.AltarBlockEntity.startAltar;
import static org.jahdoo.common.registers.BlockEntityReg.CHALLENGE_ALTAR_BE;
import static org.jahdoo.common.registers.BlockReg.sharedBehaviour;

public class AltarBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE_BASE = Block.box(0, 0, 0, 16, 16, 16);

    public AltarBlock() {
        super(sharedBehaviour.strength(-1.0F, 3600000.0F));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new AltarBlock());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BASE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AltarBlockEntity(pos,state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(
            entityType, CHALLENGE_ALTAR_BE.get(), (levelA, pos, bState, bEntity) -> bEntity.tick(levelA, pos, bState)
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
        if (!(level instanceof ServerLevel serverLevel)) return FAIL;

        var consume = highlightMobs(altarE, serverLevel);
        if (consume != null) return consume;

        return startAltar(pos, altarE, serverLevel);
    }

    private static @Nullable ItemInteractionResult highlightMobs(AltarBlockEntity altarE, ServerLevel serverLevel) {
        if(altarE.started) {
            for (var entity : serverLevel.getEntities().getAll()) {
                if(!(entity instanceof Player) && entity instanceof LivingEntity livingEntity){
                    var glowing = MobEffects.GLOWING;
                    if(!livingEntity.hasEffect(glowing)){
                        livingEntity.addEffect(new JahdooMobEffect(glowing, 200, 1));
                        Helpers.getSoundWithPositionV(serverLevel, livingEntity.position(), SoundEvents.BELL_BLOCK, 2, 0.8F);
                    }
                }
            }
            return CONSUME;
        }
        return null;
    }

}

