package org.jahdoo.all_magic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Range;
import org.jahdoo.utils.GeneralHelpers;

public class UtilityHelpers {
    public static Range<Float> range = Range.of(0.0f, 10.0f);

    public static void dropItemsOrBlock(Projectile newProjectile, BlockPos pos, boolean isSilkTouch, boolean voidBlocks){
        if(UtilityHelpers.range.contains(UtilityHelpers.destroySpeed(pos, newProjectile.level()))){
            BlockState blockstate = newProjectile.level().getBlockState(pos);
            if(!voidBlocks){
                if (isSilkTouch) {
                    newProjectile.spawnAtLocation(new ItemStack(blockstate.getBlock()));
                } else {
                    LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) newProjectile.level())).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, new ItemStack(Items.DIAMOND_PICKAXE));
                    newProjectile.level().getBlockState(pos).getDrops(lootparams$builder).forEach(itemStack -> newProjectile.spawnAtLocation(itemStack.getItem()));
                }
            }
            if (newProjectile.level() instanceof ServerLevel serverLevel) {
                GeneralHelpers.generalHelpers.sendParticles(serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, blockstate),  pos.getCenter(), 5,0, 0, 0, 1);
            }
            newProjectile.level().removeBlock(pos, false);
        }
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
