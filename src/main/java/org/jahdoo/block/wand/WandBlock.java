package org.jahdoo.block.wand;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.components.WandData;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static org.jahdoo.block.wand.WandBlockEntity.GET_WAND_SLOT;
//import static org.jahdoo.registers.DataComponentRegistry.ABILITY_SLOTS;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;
import static org.jahdoo.registers.ElementRegistry.getElementByWandType;

public class WandBlock extends BaseEntityBlock {

    VoxelShape result = Block.box(7, 0, 7, 9, 17, 9);

    public WandBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return  simpleCodec(WandBlock::new);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof WandBlockEntity wandBlock) {
            return wandBlock.inputItemHandler.getStackInSlot(GET_WAND_SLOT);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (!(level.getBlockEntity(blockPos) instanceof WandBlockEntity wandBlock)) return;
        if(wandBlock.getWandItemFromSlot().isEmpty()) return;
        List<AbstractElement> getType = getElementByWandType(wandBlock.getWandItemFromSlot().getItem());
        if(!level.isClientSide() && !getType.isEmpty()) return;
        AbstractElement element = getType.get(0);

        BakedParticleOptions par1 = new BakedParticleOptions(element.getTypeId(), 20, 1.5f, false);
        GenericParticleOptions par2 = new GenericParticleOptions(
            ParticleStore.GENERIC_PARTICLE_SELECTION,
            element.particleColourPrimary(),
            element.particleColourFaded(),
            20, 1.5f, false, 0.3
        );

        GeneralHelpers.getInnerRingOfRadiusRandom(blockPos, 0.1, 2,
            positions -> this.placeParticle(level, positions, element, randomSource.nextInt(0,3) == 0 ? par1 : par2)
        );
    }

    public void placeParticle(Level level, Vec3 pos, AbstractElement element, ParticleOptions par1){
        double randomY = GeneralHelpers.Random.nextDouble(0.0, 0.2);
        level.addParticle(par1, pos.x, pos.y - 0.3, pos.z, 0, randomY, 0);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return result;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof WandBlockEntity wandBlockEntity) {
                wandBlockEntity.dropsAllInventory(pLevel);
            }
        }
        GeneralHelpers.getSoundWithPosition(pLevel, pPos, SoundEvents.BEACON_DEACTIVATE, 0.4f, 1.5f);
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        ItemStack heldItem = pPlayer.getItemInHand(pHand);

        if (pPlayer.getMainHandItem().isEmpty() && pPlayer.isShiftKeyDown()) {
            this.pickUpWand(pPlayer, pPos, pLevel);
            return ItemInteractionResult.SUCCESS;
        }

        if (pLevel.getBlockEntity(pPos) instanceof WandBlockEntity wandBlock) {
            if (heldItem.getItem() == ItemsRegister.AUGMENT_CORE.get()) {
                ItemStack internalWand = wandBlock.getWandItemFromSlot();
                var wandData = internalWand.get(WAND_DATA);

                if(wandData != null && wandData.abilitySlots() < 10){
                    heldItem.shrink(1);
                    int addSlotOrMax = Math.min(wandData.abilitySlots() + 1, 10);
                    internalWand.update(WAND_DATA, WandData.DEFAULT, data -> data.setAbilitySlots(addSlotOrMax));
                } else {
                    return ItemInteractionResult.FAIL;
                }

//            this.setRightClickedAugmentToWand(wandBlock, heldItem, blockPos, level, player);
            } else {
                openWandGUI(pPlayer, pPos, pLevel);
            }
        }

        return ItemInteractionResult.SUCCESS;
    }


    private void openWandGUI(Player player, BlockPos blockPos, Level level){
        if (!(player.getMainHandItem().isEmpty())) return;
        if (!(level.getBlockEntity(blockPos) instanceof WandBlockEntity wandBlock)) return;
        if(!(player instanceof ServerPlayer serverPlayer)) return;
        serverPlayer.openMenu(wandBlock, blockPos);
    }

    private void pickUpWand(Player player, BlockPos blockPos, Level level){
        if (level.getBlockEntity(blockPos) instanceof WandBlockEntity wandBlock) {
            player.setItemInHand(player.getUsedItemHand(), wandBlock.getWandItemFromSlot().copy());
            wandBlock.getWandItemFromSlot().shrink(1);
            level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new WandBlockEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }
        return createTickerHelper(
            pBlockEntityType,
            BlockEntitiesRegister.WAND_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }
}

