package org.jahdoo.trial_nexus.ability;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Range;
import org.jahdoo.common.block.chaos_cube.ChaosCubeEntity;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.trial_nexus.utils.EnchantmentHelpers;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.level.block.Blocks.AIR;
import static org.jahdoo.trial_nexus.ability.abilities_utility.fetch.Fetch.handlePlayerPickup;
import static org.jahdoo.trial_nexus.utils.ModTags.Block.MINEABLE_NEXUS;

public class UtilityHelpers {

    public static Range<Float> range = Range.of(0.0f, 10.0f);

    public static float destroySpeed(BlockPos blockPos, Level level){
        return level.getBlockState(blockPos).getDestroySpeed(level, blockPos);
    }

    public static void lavaWaterInteractionBehaviour(Entity entity){
        BlockPos blockPos = entity.blockPosition();
        FluidState fluidState = entity.level().getFluidState(blockPos);

        if(fluidState.is(FluidTags.LAVA)){

            BlockState obsidian = Blocks.OBSIDIAN.defaultBlockState();
            BlockState cobblestone = Blocks.COBBLESTONE.defaultBlockState();

            if (fluidState.is(Fluids.LAVA)) {
                entity.level().setBlockAndUpdate(blockPos, obsidian);
            }
            if (fluidState.is(Fluids.FLOWING_LAVA)) {
                entity.level().setBlockAndUpdate(blockPos, cobblestone);
            }
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1, 1);
            entity.discard();
        }
    }

    public static void harvestBreaker(Level level, BlockPos pos, boolean voidBlocks){
        var blockstate = level.getBlockState(pos);
        if(!voidBlocks){
            var centre = pos.getCenter();
            if(!(level instanceof ServerLevel serverLevel)) return;

            var lootBuilder = new LootParams
                .Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, new ItemStack(Items.DIAMOND_PICKAXE))
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos));

            var drops = blockstate.getDrops(lootBuilder);
            for (ItemStack itemStack : drops) {
                ItemEntity item = new ItemEntity(level, centre.x, centre.y, centre.z, itemStack);;

                if (item.getItem().getItem() instanceof BlockItem blockItem) {
                    if (blockItem.getBlock() instanceof CropBlock cropBlock) {
                        if (item.getItem().getItem() == cropBlock.getCloneItemStack(level, pos, blockstate).getItem()) {
                            item.getItem().shrink(1);
                        }
                    }
                }

                level.addFreshEntity(item);
            }
        }
        var blockPart = new BlockParticleOption(ParticleTypes.BLOCK, blockstate);
        ParticleHandlers.sendParticles(level, blockPart,  pos.getCenter(), 5,0, 0, 0, 1);
        level.removeBlock(pos, false);
    }

    public static boolean canBreakInDim(ServerLevel serverLevel, BlockPos pos){
        var a = serverLevel instanceof CustomLevel;
        var b = serverLevel instanceof CustomLevel && serverLevel.getBlockState(pos).is(MINEABLE_NEXUS);

        return !a || b;
    }

    public static void dropItemsOrBlock(
        GenericProjectile newProjectile,
        BlockPos pos,
        float breakSpeed,
        int fortuneLevel,
        boolean isSilkTouch,
        boolean voidBlocks,
        boolean smelt,
        boolean autoCollect
    ){
        var level = newProjectile.level();
        var fluidState = level.getFluidState(pos);

        if(Range.of(0.0f, 10 + breakSpeed).contains(UtilityHelpers.destroySpeed(pos, level)) || !fluidState.isEmpty()){
            var blockstate = level.getBlockState(pos);
            if(!blockstate.isAir()){
                level.setBlock(pos, AIR.defaultBlockState(), 3);
                if (!voidBlocks) {
                    var centre = pos.getCenter();
                    if (!(level instanceof ServerLevel serverLevel)) return;
                    if (isSilkTouch) {
                        var getBlock = new ItemStack(blockstate.getBlock());
                        var itementity = new ItemEntity(level, centre.x, centre.y, centre.z, getBlock);

                        collectOrDrop(newProjectile, autoCollect, itementity, level);
                    } else {
                        var drops = lootBuilder(pos, serverLevel, blockstate, level, getDiamondPickaxe(fortuneLevel, serverLevel));
                        for (ItemStack itemStack : drops) {
                            var canBurn = smeltable(serverLevel, itemStack);
                            var item = new ItemEntity(level, centre.x, centre.y, centre.z, smelt ? canBurn : itemStack);

                            collectOrDrop(newProjectile, autoCollect, item, level);
                        }
                    }
                }
                var blockPart = new BlockParticleOption(ParticleTypes.BLOCK, blockstate);
                ParticleHandlers.sendParticles(level, blockPart, pos.getCenter(), 5, 0, 0, 0, 1);
                level.removeBlock(pos, false);
            }
        }
    }

    public static void collectOrDrop(GenericProjectile newProjectile, boolean autoCollect, ItemEntity item, Level level) {
        if(autoCollect){
            var owner = (Player) newProjectile.getOwner();
            if (owner != null) {
                handlePlayerPickup(item, owner);
            } else {
                var pos = newProjectile.blockEntityPos;
                if(pos == null) return;
                var bE = level.getBlockEntity(BlockPos.containing(pos));
                if(bE instanceof ChaosCubeEntity autoEntity){
                    autoEntity.externalOutputInventory(level, item);
                }
            }

        } else {
            level.addFreshEntity(item);
        }
    }

    private static ItemStack getDiamondPickaxe(int fortuneLevel, ServerLevel serverLevel){
        var value = new ItemStack(Items.DIAMOND_PICKAXE);
        EnchantmentHelpers.enchant(value, serverLevel.registryAccess(), Enchantments.FORTUNE, fortuneLevel);
        return value;
    }

    public static @NotNull List<ItemStack> lootBuilder(BlockPos pos, ServerLevel serverLevel, BlockState blockstate, Level level, ItemStack value) {
        var lootBuilder = new LootParams
            .Builder(serverLevel)
            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
            .withParameter(LootContextParams.TOOL, value)
            .withOptionalParameter(LootContextParams.BLOCK_STATE, blockstate)
            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos));

        return blockstate.getDrops(lootBuilder);
    }

    public static ItemStack smeltable(Level level, ItemStack stack) {
        if (!stack.isEmpty()) {
            Optional<RecipeHolder<SmeltingRecipe>> optional = level
                .getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level);
            if (optional.isPresent()) {
                ItemStack itemstack = optional.get().value().getResultItem(level.registryAccess());
                if (!itemstack.isEmpty()) {
                    return itemstack.copyWithCount(stack.getCount() * itemstack.getCount());
                }
            }

        }
        return stack;
    }


}
