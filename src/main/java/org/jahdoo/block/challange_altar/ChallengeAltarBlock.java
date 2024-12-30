package org.jahdoo.block.challange_altar;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.abilities.ability_data.EscapeDecoyAbility;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.challenge_game_mode.RewardLootTables;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.Nullable;

import static org.jahdoo.attachments.player_abilities.ChallengeAltarData.resetWithRound;
import static org.jahdoo.registers.BlocksRegister.sharedBlockBehaviour;
import static org.jahdoo.utils.ModHelpers.Random;

public class ChallengeAltarBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE_BASE = Block.box(4.5, 5, 4.5, 11.5, 32, 11.5);
    public static final VoxelShape SHAPE_BASE_SECOND = Block.box(3.5, 0, 3.5, 12.5, 5, 12.5);
    public static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE_SECOND, SHAPE_BASE);

    public ChallengeAltarBlock() {
        super(sharedBlockBehaviour());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((x) -> new ChallengeAltarBlock());
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE_COMMON;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof ChallengeAltarBlockEntity altarE)) return ItemInteractionResult.FAIL;
        if(!ChallengeAltarData.isCompleted(altarE)){
            var startingRound = Math.max(1, ChallengeAltarData.getProperties(altarE).round);
            var endingRound = startingRound;
            resetWithRound(altarE, startingRound, endingRound);
        } else {
            if(level instanceof ServerLevel serverLevel){
                var rewards = RewardLootTables.getCompletionLoot(serverLevel, pos.getCenter());
                System.out.println(rewards);
                for (var reward : rewards) {
                    var itemEntity = new ItemEntity(serverLevel, pos.getCenter().x(), pos.getCenter().y() , pos.getCenter().z(), reward);
                    var angle = Random.nextDouble() * 2 * Math.PI; // Random angle between 0 and 2π
                    var horizontalOffset = 0.2 + Random.nextDouble() * 0.35; // Small horizontal variation
                    var offsetX = Math.cos(angle) * horizontalOffset;
                    var offsetZ = Math.sin(angle) * horizontalOffset;
                    var velocity = new Vec3(offsetX * (Math.random() - 0.5), Random.nextDouble(0.2, 0.5), offsetZ * (Math.random() - 0.5));
                    itemEntity.setDeltaMovement(velocity);
                    itemEntity.setDefaultPickUpDelay();

                    for (var abstractElement : ElementRegistry.REGISTRY) {
                        successfulCraftVisual(serverLevel, pos, abstractElement);
                    }
                    serverLevel.addFreshEntity(itemEntity);
                }
                ModHelpers.getSoundWithPosition(level, pos, SoundEvents.VAULT_OPEN_SHUTTER, 1f, 1.8f);
                ModHelpers.getSoundWithPosition(level, pos, SoundEvents.ILLUSIONER_CAST_SPELL, 1f, 1f);
                ModHelpers.getSoundWithPosition(level, pos, SoundRegister.EXPLOSION.get(), 1f, 0.9f);
                level.destroyBlock(pos, false);
            }
        }
//        altarClickToStart(altarE);
//        resetWithRound(altarE, 70);
        return ItemInteractionResult.SUCCESS;
    }

    public static void successfulCraftVisual(ServerLevel serverLevel, BlockPos blockPos, AbstractElement element){
        ParticleHandlers.particleBurst(
            serverLevel, blockPos.getCenter().add(0, 0.5f, 0), 1,
            EscapeDecoyAbility.getFromAllRandom(element, 20, 1),
            0, 0.7, 0, 0.2f, 3
        );
    }

    public static void throwItemFromChest(BlockEntity chestEntity, ItemStack itemStack) {
        // Get the position of the chest-like object
        double chestX = chestEntity.getBlockPos().getX() + 0.5; // Center of the block
        double chestY = chestEntity.getBlockPos().getY() + 1;   // Slightly above the chest
        double chestZ = chestEntity.getBlockPos().getZ() + 0.5;

        // Randomized horizontal direction
        double angle = Random.nextDouble() * 2 * Math.PI; // Random angle between 0 and 2π
        double horizontalOffset = 0.2 + Random.nextDouble() * 0.3; // Small horizontal variation
        double offsetX = Math.cos(angle) * horizontalOffset;
        double offsetZ = Math.sin(angle) * horizontalOffset;

        // Upward velocity with slight random variation
        double upwardVelocity = 0.4 + Random.nextDouble() * 0.2;

        // Final velocity vector
        Vec3 velocity = new Vec3(offsetX, upwardVelocity, offsetZ);

        // Call BehaviorUtils to throw the item
        throwItemFromBlock(chestEntity, itemStack,new Vec3(chestX, chestY, chestZ), velocity , 1);
    }

    public static void throwItemFromBlock(BlockEntity blockEntity, ItemStack stack, Vec3 offset, Vec3 speedMultiplier, float yOffset) {
        // Calculate the spawn position relative to the block
        double spawnX = blockEntity.getBlockPos().getX() + 0.5 + offset.x; // Center of the block with horizontal offset
        double spawnY = blockEntity.getBlockPos().getY() + yOffset;       // Adjusted height offset
        double spawnZ = blockEntity.getBlockPos().getZ() + 0.5 + offset.z; // Center of the block with horizontal offset

        // Create the item entity
        ItemEntity itemEntity = new ItemEntity(blockEntity.getLevel(), spawnX, spawnY, spawnZ, stack);

        // Apply velocity based on the speed multiplier
        Vec3 velocity = new Vec3(
            speedMultiplier.x * (Math.random() - 0.5), // Randomized horizontal direction
            speedMultiplier.y,
            speedMultiplier.z * (Math.random() - 0.5)
        );
        itemEntity.setDeltaMovement(velocity);

        // Set default pickup delay
        itemEntity.setDefaultPickUpDelay();

        // Add the item entity to the world
        System.out.println(itemEntity.getItem());
        blockEntity.getLevel().addFreshEntity(itemEntity);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ChallengeAltarBlockEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {

        return createTickerHelper(
            pBlockEntityType,
            BlockEntitiesRegister.CHALLENGE_ALTAR_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }
}

