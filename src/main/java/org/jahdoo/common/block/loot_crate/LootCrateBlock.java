package org.jahdoo.common.block.loot_crate;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jahdoo.ascension.level_manager.InstanceDifficulty;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.LocalLootBeamData;
import org.jahdoo.common.components.LootCrateData;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.ItemInteractionResult.FAIL;
import static net.minecraft.world.ItemInteractionResult.SUCCESS;
import static org.jahdoo.ascension.loot.LootHelpers.lootsplosian;
import static org.jahdoo.ascension.loot.RewardLootTables.getCompletionLoot;
import static org.jahdoo.common.registers.BlockEntityReg.LOOT_CRATE_BE;

public class LootCrateBlock extends BaseEntityBlock implements SimpleWaterloggedBlock{

    public static final VoxelShape COMPLETION_CRATE = Shapes.or(
        Block.box(2, 14, 0, 14, 16, 2),
        Block.box(2, 2, 2, 14, 14, 14),
        Block.box(14, 0, 14, 16, 16, 16),
        Block.box(0, 0, 0, 2, 16, 2),
        Block.box(0, 0, 14, 2, 16, 16),
        Block.box(0, 0, 14, 2, 16, 16),
        Block.box(0, 0, 0, 2, 16, 2),
        Block.box(14, 0, 0, 16, 16, 2),
        Block.box(14, 0, 14, 16, 16, 16),
        Block.box(14, 0, 2, 16, 16, 2),
        Block.box(14, 14, 2, 16, 16, 14),
        Block.box(0, 14, 2, 2, 16, 14),
        Block.box(14, 0, 2, 16, 2, 14),
        Block.box(0, 0, 2, 2, 2, 14),
        Block.box(2, 0, 14, 14, 2, 16),
        Block.box(2, 14, 14, 14, 16, 16),
        Block.box(2, 0, 0, 14, 2, 2)
    );

    public LootCrateBlock() {
        super(
            Properties.of()
                .strength(1f)
                .sound(SoundType.WOOD)
                .noOcclusion()
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return  simpleCodec((x) -> new LootCrateBlock());
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
        super.attack(state, level, pos, player);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COMPLETION_CRATE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LootCrateEntity(pos,state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(!(level instanceof ServerLevel)) return;
        var entity = level.getBlockEntity(pos);
        if(entity instanceof LootCrateEntity lootCrate){
            lootCrate.inputItemHandler.setStackInSlot(0, stack.copyWithCount(1));
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        var entity = level.getBlockEntity(pos);
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (level.getBlockEntity(pos) instanceof LootCrateEntity lootCrate) {
            var handler = lootCrate.inputItemHandler.getStackInSlot(0);
            var inputInventory = new SimpleContainer(1);
            inputInventory.addItem(handler);
            Containers.dropContents(level, pos, inputInventory);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return new ArrayList<>();
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entityType) {
        return createTickerHelper(
            entityType, LOOT_CRATE_BE.get(),
            (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1)
        );
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        if(level instanceof ServerLevel serverLevel) {
            var entity = level.getBlockEntity(pos);
            if(!(entity instanceof LootCrateEntity lootCrate)) return FAIL;

            var handler = lootCrate.inputItemHandler;
            var crateData = handler.getStackInSlot(0).get(ComponentReg.LOOT_CRATE_DATA);
            if(crateData == null) return FAIL;

            var multipliers = crateData.multiplier();
            var difficulty = InstanceDifficulty.getFromName(crateData.difficulty());
            var playerLevel = crateData.level();
            var calculateMultiplier = (multipliers + (playerLevel/4)) * difficulty.getId();
            var chestRarity = difficulty.getId();

            for(int i = 0; i < calculateMultiplier; i++){
                var rewards = getCompletionLoot(serverLevel, pos.getCenter(), difficulty.getSerializedName(), chestRarity);
                lootsplosian(pos.getCenter(), serverLevel, Helpers.getRgb(), rewards, true, 50, chestRarity);
            }

            additionalRewards(pos, serverLevel, playerLevel, difficulty, crateData, chestRarity);
            Helpers.getSoundWithPositionV(level, pos.getCenter(), SoundReg.CRATE_OPEN.get(), 1.6F, 1);
            handler.setStackInSlot(0, ItemStack.EMPTY);
            level.destroyBlock(pos, false);
            return SUCCESS;
        }

        return FAIL;
    }

    private static void additionalRewards(BlockPos pos, ServerLevel serverLevel, int playerLevel, InstanceDifficulty difficulty, LootCrateData crateData, int chestRarity) {
        var individualAddons = new ArrayList<ItemStack>();
        var coinSack = new ItemStack(ItemReg.COIN_SACK);
        var coinCalc = (playerLevel + difficulty.expMultiplier()) * (crateData.completionTime()/10);
        coinSack.set(ComponentReg.STORE_INTEGER, coinCalc);
        LocalLootBeamData.attachCoinSackLootBeam(coinSack);
        individualAddons.add(coinSack);
        individualAddons.add(new ItemStack(ItemReg.CHALLENGER_TICKET));
        lootsplosian(pos.getCenter(), serverLevel, Helpers.getRgb(), individualAddons, true, 50, chestRarity);
    }
}

