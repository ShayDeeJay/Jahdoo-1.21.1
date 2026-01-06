package org.jahdoo.common.block.chaos_cube;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.trial_nexus.ability.AbilityComponentHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.world.level.block.CrafterBlock.TRIGGERED;

public class ChaosCubeBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public ChaosCubeBlock() {
        super(Properties.of().strength(1f).sound(SoundType.DEEPSLATE_BRICKS).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(TRIGGERED, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new ChaosCubeBlock());
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.PUSH_ONLY;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChaosCubeEntity(pos,state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return openWandGUI(player, pos, level);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tComp, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tComp, tooltipFlag);
        var holder = stack.get(ComponentReg.ABILITY_HOLDER);
        if(holder == null) return;
        var ability = AbilityReg.getFirstSpellByTypeId(holder.abilityName());

        if(ability.isEmpty()) return;
        AbilityComponentHelper.onlyToolTip(ability.get(), holder, false, context.level(), tComp);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        var flag = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
        var flag1 = state.getValue(TRIGGERED);
        if (flag && !flag1) {
            level.scheduleTick(pos, this, 4);
            level.setBlock(pos, state.setValue(TRIGGERED, true), 2);
            var blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ChaosCubeEntity automationBlock) {
                automationBlock.useAugment(level, true);
            }
        } else if (!flag && flag1) {
            level.setBlock(pos, state.setValue(TRIGGERED, false), 2);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        var blockEntity = level.getBlockEntity(pos);
//        if(EventHelpers.setChaosCubeAbility(player, level, pos, stack)) return ItemInteractionResult.SUCCESS;
//        if (blockEntity instanceof ChaosCubeEntity entity) {
//            System.out.println(entity.getHolder());
//            var ability = stack.get(ComponentReg.ABILITY_HOLDER);
//            var element = ElementReg.utility();
//            if(ability != null){
//                if (ability != AbilityHolder.DEFAULT) {
//                    entity.setHolder(ability);
//                    stack.shrink(1);
//                    for (int i = 0; i < 10; i++) {
//                        var part = ParticleHandlers.getAllParticleTypes(element, 20, 2);
//                        ParticleHandlers.particleBurst(level, pos.getCenter(), 1, part);
//                    }
//
//                    getSoundWithPosition(level, pos, SoundReg.SUSPEND.get(), 1, 0.5F);
//                    entity.updateBlock();
//                    return ItemInteractionResult.SUCCESS;
//                } else {
//                    var message = "You don't have this ability";
//                    var messageComponent = withStyleComponent(message, element.textColourA());
//                    player.sendSystemMessage(messageComponent);
//                }
//            }
//        }
        openWandGUI(player, pos, level);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ChaosCubeEntity automationBlock) {
            var t = stack.get(ComponentReg.ABILITY_HOLDER);
            if(t != null) automationBlock.setHolder(t);
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        player.awardStat(Stats.BLOCK_MINED.get(this));
        player.causeFoodExhaustion(0.005F);
        if(level instanceof ServerLevel serverLevel){
            var lootBuilder = new LootParams
                .Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, tool)
                .withOptionalParameter(LootContextParams.BLOCK_STATE, state)
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos));
            var drops = state.getDrops(lootBuilder);
            if (blockEntity instanceof ChaosCubeEntity automationBlock) {
                for (var drop : drops) {
                    drop.set(ComponentReg.ABILITY_HOLDER, automationBlock.getHolder());
                    serverLevel.addFreshEntity(new ItemEntity(serverLevel, pos.getX(), pos.getY(), pos.getZ(), drop));
                }
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        var itemstack = super.getCloneItemStack(state, target, level, pos, player);
        level.getBlockEntity(pos, BlockEntityReg.CREATOR_BE.get()).ifPresent(entity -> entity.saveToItem(itemstack, level.registryAccess()));
        return itemstack;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(
            entityType,
            BlockEntityReg.MODULAR_CHAOS_CUBE_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    private InteractionResult openWandGUI(Player player, BlockPos blockPos, Level level){
        var fail = InteractionResult.FAIL;
        if (!(level.getBlockEntity(blockPos) instanceof ChaosCubeEntity cubeEntity)) return fail;
        if(!(player instanceof ServerPlayer serverPlayer)) return fail;
        serverPlayer.openMenu(cubeEntity, blockPos);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state,level,pos,newState,movedByPiston);
    }
}

