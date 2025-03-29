package org.jahdoo.common.block.loot_chest;

import com.mojang.serialization.MapCodec;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.common.items.KeyItem;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static net.minecraft.sounds.SoundEvents.LODESTONE_COMPASS_LOCK;
import static net.minecraft.sounds.SoundEvents.VAULT_EJECT_ITEM;
import static net.minecraft.world.ItemInteractionResult.FAIL;
import static net.minecraft.world.ItemInteractionResult.SUCCESS;
import static org.jahdoo.ascension.trading_post.RewardLootTables.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public class LootChestBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    private static final VoxelShape SHAPE = Block.box(0.5, 0.475, 1.475, 15.5, 9.225, 14.475);
    private static final VoxelShape SHAPE2 = Block.box(1.5, 0.475, 0.475, 14.5, 9.225, 15.475);

    public LootChestBlock() {
        super(
            BlockBehaviour.Properties.of()
                .strength(1f)
                .sound(SoundType.EMPTY)
                .noOcclusion()
                .strength(-1.0F, 3600000.0F)
        );
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new LootChestBlock());
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE2 : SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LootChestEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper
            (entityType, BlockEntityReg.LOOT_CHEST_BE.get(), (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    public static void particleBurst(ServerLevel serverLevel, Vec3 center, int colour, int multiplier) {
        var fade = getColourDarker(colour, 0.5);
        var randomColouredParticle = getRandomColouredParticle(colour, fade, Random.nextInt(10, 20), 1f, false);
        var pos = center.add(0, 0.3f, 0);

        ParticleHandlers.particleBurst(serverLevel, pos, 2 * multiplier, randomColouredParticle, 0, 0.3, 0, 0.2f, 3);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(
        ItemStack stack,
        BlockState state,
        Level level,
        BlockPos pos,
        Player player,
        InteractionHand hand,
        BlockHitResult hitResult
    ) {
        if (!(level.getBlockEntity(pos) instanceof LootChestEntity cEntity)) return FAIL;
        if (!(level instanceof ServerLevel serverLevel)) return FAIL;

        if(serverLevel instanceof CustomLevel && !cEntity.isOpen){
            var data = serverLevel.getData(INSTANCE_DATA);

            if(cEntity.isCoinChest()){
                return coinChestGetter(pos, serverLevel, cEntity, player);
            } else {
                var success = lootChestGetter(stack, serverLevel, pos, cEntity, data.getClearedRooms());
                if (success != null) return success;
            }

            getSoundWithPosition(level, pos, LODESTONE_COMPASS_LOCK, 1,1.8F);
        }

        return SUCCESS;
    }

    private static @NotNull ItemInteractionResult coinChestGetter(
        BlockPos pos,
        ServerLevel serverLevel,
        LootChestEntity lootChestEntity,
        Player player
    ) {
        var coinItems = getCoinItems(lootChestEntity.getData(INSTANCE_DATA));
        if(!coinItems.isEmpty()){
            lootChestEntity.setOpen(true);
            lootsplosian(pos.getCenter(), serverLevel, 10, ColourStore.ABSORPTION_YELLOW, coinItems, false, 0);
            openingSoundEffect(pos, serverLevel, false);
        } else {
            player.displayClientMessage(withStyleComponent("Chest is empty!", ColourStore.NEGATIVE_RED), true);
        }
        return SUCCESS;
    }

    private static ItemInteractionResult lootChestGetter(
        ItemStack stack,
        ServerLevel serverLevel,
        BlockPos pos,
        LootChestEntity lootChestEntity,
        int clearedRooms
    ) {
        var keyData = stack.get(CUSTOM_MODEL_DATA);
        if (stack.is(ItemReg.LOOT_KEY) && keyData != null) {

            var value = keyData.value();
            var isValid = value == lootChestEntity.getRarity;
            if (isValid) {
                lootChestEntity.setOpen(true);
                var getId = new CustomModelData(lootChestEntity.getRarity);
                var colour = KeyItem.getJahdooRarity(getId).getColour();
                var setLootValue = clearedRooms + (value * value);
                var lootMultiplier = value + 1;
                var rewards = getCompletionLoot(serverLevel, pos.getCenter(), setLootValue, value);

                lootsplosian(pos.getCenter(), serverLevel, lootMultiplier, colour, rewards, true, 30);
                openingSoundEffect(pos, serverLevel, true);
                stack.shrink(1);
                return SUCCESS;
            }
        }

        return null;
    }

    public static void lootsplosian(
        Vec3 pos,
        ServerLevel serverLevel,
        int level,
        int colour,
        List<ItemStack> rewards,
        boolean shouldDropExperience,
        int pickupDelay
    ) {
        for (var reward : rewards) {
            var itemEntity = new ItemEntity(serverLevel, pos.x(), pos.y() + 0.2, pos.z(), reward);
            var angle = Random.nextDouble() * 2 * Math.PI;
            var horizontalOffset = 0.2 + Random.nextDouble() * 0.35;
            var offsetX = Math.cos(angle) * horizontalOffset;
            var offsetZ = Math.sin(angle) * horizontalOffset;
            var velocity = new Vec3(offsetX * (Math.random() - 0.5), 0.35, offsetZ * (Math.random() - 0.5));
            var itemStackMain= itemEntity.getItem();
            var rarity = JahdooRarity.getRarity();

            itemEntity.setDeltaMovement(velocity);
            itemEntity.setPickUpDelay(pickupDelay);

            if(shouldDropExperience && Random.nextInt(10) == 0) {
                var exp = ItemReg.EXPERIENCE_ORB.get();
                var itemStack = new ItemStack(exp);

                switch (JahdooRarity.getRarity()) {
                    case COMMON, RARE -> itemStack.set(CUSTOM_MODEL_DATA, new CustomModelData(1));
                    case EPIC -> itemStack.set(CUSTOM_MODEL_DATA, new CustomModelData(2));
                    case LEGENDARY, ETERNAL -> { /*No Data*/ }
                }

                var itemEntity1 = new ItemEntity(serverLevel, pos.x(), pos.y() + 0.2, pos.z(), itemStack);
                itemEntity1.setDeltaMovement(velocity);
                itemEntity1.setPickUpDelay(pickupDelay);
                serverLevel.addFreshEntity(itemEntity1);
            }

            particleBurst(serverLevel, pos, colour, level);
            attachItemData(serverLevel, rarity, itemStackMain, false, null);
            serverLevel.addFreshEntity(itemEntity);
        }
    }

    private static void openingSoundEffect(BlockPos pos, ServerLevel serverLevel, boolean isLootChest) {
        getSoundWithPosition(serverLevel, pos, VAULT_EJECT_ITEM, 2F, 0.8F);
        getSoundWithPosition(serverLevel, pos, LODESTONE_COMPASS_LOCK, 2F, 1.4F);
        getSoundWithPosition(serverLevel, pos, SoundEvents.VAULT_PLACE, 2F, 0.4F);

        if(isLootChest) {
            getSoundWithPosition(serverLevel, pos, SoundReg.LOOTBOX_OPEN.get(), 2F, 1F);
        } else {
            getSoundWithPosition(serverLevel, pos, SoundReg.COINBOX_OPEN.get(), 0.6F, 1F);
        }
    }
}

