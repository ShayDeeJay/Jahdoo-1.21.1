package org.jahdoo.common.block.creator;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shaydee.shaydeeapi.helpers.ItemHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import static net.minecraft.sounds.SoundSource.BLOCKS;
import static net.minecraft.world.ItemInteractionResult.FAIL;
import static net.minecraft.world.ItemInteractionResult.SUCCESS;
import static net.minecraft.world.level.block.SoundType.DEEPSLATE_BRICKS;
import static org.jahdoo.common.block.BlockInteractionHandler.removeItemsFromHandToSlot;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;


public class CreatorBlock extends BaseEntityBlock{

    public static VoxelShape SHAPE_COMMON = Shapes.or(
        Block.box(6, 4, 6, 10, 7, 10),
        Block.box(5.25, 0, 5.25, 10.75, 2, 10.75),
        Block.box(6.25, 2, 6.25, 9.75, 5.5, 9.75),
        Block.box(0, 13, 0, 16, 16, 16)
    );

    public CreatorBlock() {
        super(
            Properties
                .of()
                .strength(1f)
                .sound(DEEPSLATE_BRICKS)
                .noOcclusion()
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((t) -> new CreatorBlock());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_COMMON;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(
        BlockState pState,
        Level pLevel,
        BlockPos pPos,
        BlockState pNewState,
        boolean pMovedByPiston
    ) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof CreatorEntity creatorEntity) {
                creatorEntity.dropsAllInventory(pLevel);
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(
        ItemStack pStack,
        BlockState pState,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult result
    ) {
        if(!(level.getBlockEntity(pos) instanceof CreatorEntity wandManager)) return FAIL;
        var stack = player.getMainHandItem();
        var inputIHandler = wandManager.getInputItemHandler();
        var outputIHandler = wandManager.getOutputItemHandler();

        if(setChaosCubeAbility(player, level, pos, stack)) return SUCCESS;

        if(!stack.isEmpty()){
            for (int i = 0; i < inputIHandler.getSlots(); i++) {
                if(inputIHandler.getStackInSlot(i).isEmpty()){
                    if (removeItemsFromHandToSlot(inputIHandler, i, player, 1, hand)) return SUCCESS;
                }
            }
            return FAIL;
        } else {
            if(!outputIHandler.getStackInSlot(0).isEmpty()){
                ItemHelpers.throwOrAddItem(player, outputIHandler.getStackInSlot(0));
                return SUCCESS;
            } else {
                for (int i = 0; i < inputIHandler.getSlots(); i++) {
                    int entry = inputIHandler.getSlots() - (i+1);
                    if(!inputIHandler.getStackInSlot(entry).isEmpty()){
                        ItemHelpers.throwOrAddItem(player, inputIHandler.getStackInSlot(entry));
                        return SUCCESS;
                    }
                }
                return FAIL;
            }
        }
    }


    public static boolean setChaosCubeAbility(Player player, Level level, BlockPos pos, ItemStack item) {
        if(level.getBlockEntity(pos) instanceof CreatorEntity entity){
            if(item.isEmpty() && entity.getRecipe().isPresent() && !entity.canCraft()){
                var casterData = player.getData(CASTER_DATA.get());
                var ability = AbilityReg.getFirstSpellByTypeId(casterData.getSelectedAbility());
                if(ability.isPresent()) {
                    var element = ElementReg.utility();
                    var getAbility = ability.get();
                    if (getAbility.getElemenType() == element) {
                        var holder = CasterData.entityHolderWithSelected(player);
                        if (holder != AbilityHolder.DEFAULT) {
                            entity.setHolder(holder);
                            particleBurst(level, pos);
                            SoundHelpers.getSoundWithPosition(level, pos, SoundReg.SUSPEND.get(), BLOCKS, 1F, 0.5F);
                            return true;
                        } else {
                            var message = "You don't have this ability";
                            var messageComponent = TextHelpers.withStyleComponent(message, element.textColourA());
                            player.sendSystemMessage(messageComponent);
                        }
                    } else {
                        if(level.isClientSide){
                            var message = getAbility.getAbilityName();
                            var messageComponent = TextHelpers.withStyleComponent(message, getAbility.getElemenType().textColourA());
                            var append = messageComponent.copy().append(TextHelpers.withStyleComponent(" Is not compatible", -1));
                            player.sendSystemMessage(append);
                        }
                    }
                }
            }

        }
        return false;
    }

    public static void particleBurst(Level level, BlockPos pos) {
        for (int i = 0; i < 10; i++) {
            var part = ParticleHandlers.getAllParticleTypes(ElementReg.utility(), 16, 1.5f);
            ParticleHandlers.particleBurst(level, pos.getCenter().add(0,0.8,0), 1, part);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new CreatorEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(
            pBlockEntityType,
            BlockEntityReg.CREATOR_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

}
