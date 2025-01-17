package org.jahdoo.block.loot_chest;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
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
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.abilities.ability_data.EscapeDecoyAbility;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.challenge.*;
import org.jahdoo.items.TomeOfUnity;
import org.jahdoo.items.augments.Augment;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.registers.AttachmentRegister;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.shaydee.loot_beams_neoforge.LootBeams;
import org.shaydee.loot_beams_neoforge.data_component.DataComponentsReg;
import org.shaydee.loot_beams_neoforge.data_component.LootBeamComponent;

import static net.minecraft.world.entity.ExperienceOrb.getExperienceValue;
import static org.jahdoo.attachments.player_abilities.ChallengeAltarData.*;
import static org.jahdoo.block.augment_modification_station.AugmentModificationBlock.SHAPE_COMBINED;
import static org.jahdoo.challenge.RewardLootTables.attachItemData;
import static org.jahdoo.particle.ParticleHandlers.getFromAllRandom;
import static org.jahdoo.registers.AttachmentRegister.*;
import static org.jahdoo.registers.BlocksRegister.CHALLENGE_ALTAR;
import static org.jahdoo.registers.BlocksRegister.sharedBlockBehaviour;
import static org.jahdoo.utils.ModHelpers.Random;

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

        if (level instanceof ServerLevel serverLevel && !lootChestEntity.isOpen) {
            lootChestEntity.setOpen(true);
            var hasData = lootChestEntity.hasData(AttachmentRegister.CHALLENGE_ALTAR);
            var getData = lootChestEntity.getData(AttachmentRegister.CHALLENGE_ALTAR).maxRound() - 1;
            var lootLevel = hasData ? getData : 1;
            lootSplosion(pos, serverLevel, lootLevel, 1);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.FAIL;
    }

    private static void lootSplosion(BlockPos pos, ServerLevel serverLevel, int level, int lootMultiplier) {
        for(int i = 0; i < lootMultiplier; i++){
            var rewards = RewardLootTables.getCompletionLoot(serverLevel, pos.getCenter(), level);
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

                ExperienceOrb.award(serverLevel, pCenter, 10 * level);
                for (var element : ElementRegistry.REGISTRY) particleBurst(serverLevel, element, pCenter);
                var itemStack = itemEntity.getItem();
                var rarity = JahdooRarity.getRarity();

                attachItemData(serverLevel, rarity, itemStack);
                serverLevel.addFreshEntity(itemEntity);
            }
        }

        ModHelpers.getSoundWithPosition(serverLevel, pos, SoundEvents.VAULT_OPEN_SHUTTER, 1f, 1.8f);
        ModHelpers.getSoundWithPosition(serverLevel, pos, SoundEvents.ILLUSIONER_CAST_SPELL, 1f, 1f);
        ModHelpers.getSoundWithPosition(serverLevel, pos, SoundRegister.EXPLOSION.get(), 0.8f, 0.9f);
    }

    private static void particleBurst(ServerLevel serverLevel, AbstractElement element, Vec3 pCenter) {
        ParticleHandlers.particleBurst(
            serverLevel, pCenter.add(0, 0.3f, 0), 1,
            getFromAllRandom(element, 10, Random.nextFloat(1, 2)),
            0, 0.3, 0, 0.2f, 3
        );
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

