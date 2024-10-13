package org.jahdoo.all_magic.all_abilities.utility.raw_utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.UtilityHelpers;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.particle.ParticleHandlers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class VeinMiner extends AbstractUtilityProjectile {
    private static final Direction[] ALL_DIRECTIONS = Direction.values();
    private static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("vein_miner_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new VeinMiner();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        if (genericProjectile.level().isClientSide) return;

        if (genericProjectile.level() instanceof ServerLevel serverLevel){
            BlockPos start = blockHitResult.getBlockPos();
            BlockState target = genericProjectile.level().getBlockState(start);
            if (target.isAir()) return;

            double x = genericProjectile.getX();
            double y = genericProjectile.getY();
            double z = genericProjectile.getZ();
            genericProjectile.level().playSound(null, x, y, z, genericProjectile.level().getBlockState(start).getSoundType().getBreakSound(), SoundSource.BLOCKS, 1, 1);

            this.forAllBlocksAroundOf(start, genericProjectile.level(), target.getBlock(), 128,
                (pos, state) -> {
                    UtilityHelpers.dropItemsOrBlock(genericProjectile, pos, true, true);
                    ParticleHandlers.spawnPoof(serverLevel, pos.getCenter(), 1, ElementRegistry.UTILITY.get().getParticleGroup().genericSlow(), 0, 0, 0, 0.005f, 1);
                }
            );
            genericProjectile.discard();
        }
    }

    private void forAllBlocksAroundOf(
        BlockPos pos,
        BlockGetter access,
        Block target,
        int limit,
        BiConsumer<BlockPos, BlockState> consumer
    ) {
        HashSet<BlockPos> checked = new HashSet<>();
        ArrayDeque<BlockPos> deque = new ArrayDeque<>();
        deque.add(pos);
        MutableInt found = new MutableInt();
        while (!deque.isEmpty() && found.intValue() < limit) {
            BlockPos next = deque.poll();
            this.forAllBlocksAroundOf(
                next, access, target, checked, deque, found, limit, consumer
            );
        }
    }

    private void forAllBlocksAroundOf(
        BlockPos pos,
        BlockGetter access,
        Block target,
        Set<BlockPos> checked,
        Deque<BlockPos> deque,
        MutableInt found,
        int limit,
        BiConsumer<BlockPos, BlockState> consumer
    ) {
        if (found.intValue() > limit || checked.contains(pos)) {
            return;
        }
        checked.add(pos);

        BlockState state = access.getBlockState(pos);
        if (!state.getBlock().equals(target)) {
            return;
        }
        found.increment();
        consumer.accept(pos, state);

        for (Direction direction : ALL_DIRECTIONS) {
            BlockPos next = pos.relative(direction);
            deque.addLast(next);
            if (direction.getAxis() != Direction.Axis.Y) {
                continue;
            }

            for (Direction horizontal : HORIZONTAL_DIRECTIONS) {
                BlockPos nextDiagonal = next.relative(horizontal);
                deque.addLast(nextDiagonal);
            }
        }
    }
}
