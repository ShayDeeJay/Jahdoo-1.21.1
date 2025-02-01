package org.jahdoo.block.loot_chest;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
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
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.items.KeyItem;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.*;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static org.jahdoo.challenge.RewardLootTables.*;
import static org.jahdoo.challenge.RewardLootTables.attachItemData;
import static org.jahdoo.utils.ModHelpers.Random;
import static org.jahdoo.utils.ModHelpers.getRandomColouredParticle;

public class LootChestBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final VoxelShape SHAPE = Block.box(0.5, 0.475, 1.475, 15.5, 9.225, 14.475);
    public static final VoxelShape SHAPE2 = Block.box(1.5, 0.475, 0.475, 14.5, 9.225, 15.475);

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

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return pState.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE2 : SHAPE;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof LootChestEntity lootChestEntity)) return ItemInteractionResult.FAIL;

        if(!lootChestEntity.isOpen){
            var isKey = stack.is(ItemsRegister.LOOT_KEY);
            if (level instanceof ServerLevel serverLevel && isKey) {
                var value = stack.get(DataComponents.CUSTOM_MODEL_DATA).value();
                var isValid = value == lootChestEntity.getRarity;
                if (isValid) {
                    lootChestEntity.setOpen(true);
                    var hasData = lootChestEntity.hasData(AttachmentRegister.CHALLENGE_ALTAR);
                    var getData = lootChestEntity.getData(AttachmentRegister.CHALLENGE_ALTAR).maxRound() - 1;
                    var lootLevel = hasData ? getData : 1;
                    var getId = new CustomModelData(lootChestEntity.getRarity);
                    var colour = KeyItem.getJahdooRarity(getId).getColour();
                    var setLootValue = lootLevel + (value * value);
                    int lootMultiplier = value + 1;
                    for(int i = 0; i < lootMultiplier; i++){
                        var rewards = getCompletionLoot(serverLevel, pos.getCenter(), setLootValue);
                        lootsplosian(pos, serverLevel, lootMultiplier,  colour, rewards, true);
                    }
                    stack.shrink(1);
                    return ItemInteractionResult.SUCCESS;
                }
            }
            ModHelpers.getSoundWithPosition(level, pos, SoundEvents.LODESTONE_COMPASS_LOCK, 1,1.8F);
        }

        return ItemInteractionResult.SUCCESS;
    }

    public static void lootsplosian(BlockPos pos, ServerLevel serverLevel, int level, int colour, List<ItemStack> rewards, boolean shouldDropExperience) {
        for (var reward : rewards) {
            var pCenter = pos.getCenter();
            var itemEntity = new ItemEntity(serverLevel, pCenter.x(), pCenter.y() + 0.2, pCenter.z(), reward);
            var angle = Random.nextDouble() * 2 * Math.PI;
            var horizontalOffset = 0.2 + Random.nextDouble() * 0.35;
            var offsetX = Math.cos(angle) * horizontalOffset;
            var offsetZ = Math.sin(angle) * horizontalOffset;
            var velocity = new Vec3(offsetX * (Math.random() - 0.5), 0.35, offsetZ * (Math.random() - 0.5));
            itemEntity.setDeltaMovement(velocity);
            itemEntity.setPickUpDelay(30);
            if(shouldDropExperience && Random.nextInt(10) == 0) {
                var exp = ItemsRegister.EXPERIENCE_ORB.get();
                var itemStack = new ItemStack(exp);

                switch (JahdooRarity.getRarity()) {
                    case COMMON, RARE -> itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1));
                    case EPIC -> itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(2));
                    case LEGENDARY, ETERNAL -> {/*No Data*/}
                }

                var itemEntity1 = new ItemEntity(serverLevel, pCenter.x(), pCenter.y() + 0.2, pCenter.z(), itemStack);
                itemEntity1.setDeltaMovement(velocity);
                itemEntity1.setPickUpDelay(30);
                serverLevel.addFreshEntity(itemEntity1);
            }
            particleBurst(serverLevel, pCenter, colour, level);
            var itemStack = itemEntity.getItem();
            var rarity = JahdooRarity.getRarity();
            attachItemData(serverLevel, rarity, itemStack, false, null);
            serverLevel.addFreshEntity(itemEntity);
        }

        ModHelpers.getSoundWithPosition(serverLevel, pos, SoundEvents.VAULT_OPEN_SHUTTER, 1f, 1.8f);
        ModHelpers.getSoundWithPosition(serverLevel, pos, SoundEvents.ILLUSIONER_CAST_SPELL, 1f, 1f);
        ModHelpers.getSoundWithPosition(serverLevel, pos, SoundRegister.EXPLOSION.get(), 0.8f, 0.9f);
    }

    public static void particleBurst(ServerLevel serverLevel, Vec3 pCenter, int colour, int multiplier) {
        var fade = ModHelpers.getColourDarker(colour, 0.5);
        var randomColouredParticle = getRandomColouredParticle(colour, fade, Random.nextInt(10, 20), 1f, false);
        var pos = pCenter.add(0, 0.3f, 0);

        ParticleHandlers.particleBurst(serverLevel, pos, 2 * multiplier, randomColouredParticle, 0, 0.3, 0, 0.2f, 3);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new LootChestEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper
            (pBlockEntityType, BlockEntitiesRegister.LOOT_CHEST_BE.get(), (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }
}

