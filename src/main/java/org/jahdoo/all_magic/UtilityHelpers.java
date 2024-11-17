package org.jahdoo.all_magic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import org.jahdoo.particle.ParticleHandlers;

import static net.minecraft.world.level.block.Blocks.AIR;

public class UtilityHelpers {
    public static Range<Float> range = Range.of(0.0f, 10.0f);

    public static void dropItemsOrBlock(Projectile newProjectile, BlockPos pos, boolean isSilkTouch, boolean voidBlocks){
        var fluidState = newProjectile.level().getFluidState(pos);
        if(UtilityHelpers.range.contains(UtilityHelpers.destroySpeed(pos, newProjectile.level())) || !fluidState.isEmpty()){
            var blockstate = newProjectile.level().getBlockState(pos);
            var level = newProjectile.level();
            newProjectile.level().setBlock(pos, AIR.defaultBlockState(), 3);
            if(!voidBlocks){
                var centre = pos.getCenter();
                if (isSilkTouch) {
                    var getBlock = new ItemStack(blockstate.getBlock());
                    var itementity = new ItemEntity(level, centre.x, centre.y, centre.z, getBlock);
                    level.addFreshEntity(itementity);
                } else {
                    if(!(level instanceof ServerLevel serverLevel)) return;

                    var lootBuilder = new LootParams
                        .Builder(serverLevel)
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                        .withParameter(LootContextParams.TOOL, new ItemStack(Items.DIAMOND_PICKAXE))
                        .withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos));

                    var drops = blockstate.getDrops(lootBuilder);
                    for (ItemStack itemStack : drops) {
                        ItemEntity item;
                        item = new ItemEntity(level, centre.x, centre.y, centre.z, itemStack);
                        level.addFreshEntity(item);
                    }
                }
            }
            var blockPart = new BlockParticleOption(ParticleTypes.BLOCK, blockstate);
            ParticleHandlers.sendParticles(level, blockPart,  pos.getCenter(), 5,0, 0, 0, 1);
            level.removeBlock(pos, false);
        }
    }


    public static void harvestBreaker(Projectile newProjectile, BlockPos pos, boolean voidBlocks){
        var blockstate = newProjectile.level().getBlockState(pos);
        var level = newProjectile.level();
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

    public static float destroySpeed(BlockPos blockPos, Level level){
        return level.getBlockState(blockPos).getDestroySpeed(level, blockPos);
    }
}
