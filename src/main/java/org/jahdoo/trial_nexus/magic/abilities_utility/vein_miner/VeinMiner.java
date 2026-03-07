package org.jahdoo.trial_nexus.magic.abilities_utility.vein_miner;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jahdoo.common.block.chaos_cube.ChaosCubeEntity;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.magic.AbstractUtilityProjectile;
import org.jahdoo.trial_nexus.magic.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.magic.UtilityHelpers;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;
import static org.jahdoo.trial_nexus.magic.AbilityBuilder.*;
import static org.jahdoo.trial_nexus.magic.UtilityHelpers.dropItemsOrBlock;
import static org.jahdoo.trial_nexus.magic.abilities_utility.hammer.Hammer.valueToBool;
import static org.jahdoo.trial_nexus.magic.abilities_utility.vein_miner.VeinMinerAbility.VEIN_MINE_SIZE;

public class VeinMiner extends AbstractUtilityProjectile {

    private static final Direction[] ALL_DIRECTIONS = Direction.values();
    private static final Direction[] HORIZONTAL_DIRECTIONS = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
    private final ResourceLocation abilityId = JahdooHelpers.res("vein_miner_property");
    private int veinSize;
    private double voidBlocks;
    private double fortune;
    private double silkTouch;
    private double smelter;
    private double collector;
    private double reinforced;

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new VeinMiner();
    }

    @Override
    public String abilityId() {
        return VeinMinerAbility.abilityId.getPath().intern();
    }

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.veinSize = (int) this.getTag(VEIN_MINE_SIZE);
        this.voidBlocks = this.getTag(VOID_BLOCKS);
        this.fortune = this.getTag(FORTUNE);
        this.silkTouch = this.getTag(SILK_TOUCH);
        this.smelter = this.getTag(SMELTER);
        this.collector = this.getTag(AUTO_COLLECT);
        this.reinforced = this.getTag(REINFORCED);
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

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        super.onBlockBlockHit(blockHitResult);
        var level = generic.level();
        if (!(level instanceof CustomLevel cLevel && LevelGenerator.isNexus(cLevel))) return;
        if(level.getBlockEntity(blockHitResult.getBlockPos()) instanceof ChaosCubeEntity) return;

        var start = blockHitResult.getBlockPos();
        var target = cLevel.getBlockState(start);
        if (target.isAir()) return;

        cLevel.playSound(
            null,
            generic.getX(), generic.getY(), generic.getY(),
            cLevel
            .getBlockState(start)
            .getSoundType(cLevel, generic.blockPosition(), generic)
            .getBreakSound(),
            SoundSource.BLOCKS, 1, 1
        );

        var part = ParticleHandlers.genericParticle(SOFT_PARTICLE, ElementReg.utility(), 6, 0.08f, true);
        var part2 = ParticleHandlers.genericParticle(GENERIC_PARTICLE, ElementReg.utility(), 3, 4f, false);
        this.forAllBlocksAroundOf(start, cLevel, target.getBlock(), veinSize,
            (pos, state) -> {
                var breakSpeed = reinforced == 2 ? 50 : 0;
                if(UtilityHelpers.canBreakInDim(cLevel, pos)){
                    dropItemsOrBlock(
                        generic,
                        pos,
                        breakSpeed,
                        (int) fortune,
                        valueToBool(silkTouch),
                        valueToBool(voidBlocks),
                        valueToBool(smelter),
                        valueToBool(collector)
                    );
                }
                ParticleHandlers.particleBurst(cLevel, pos.getCenter(), 1, part, 0, 0, 0, 0.005f, 1);
                ParticleHandlers.particleBurst(cLevel, pos.getCenter(), 1, part2, 0, 0, 0, 0.05f, 2);
            }
        );

        generic.discard();
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

        var state = access.getBlockState(pos);
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
