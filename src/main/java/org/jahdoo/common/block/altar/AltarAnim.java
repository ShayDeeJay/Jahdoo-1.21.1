package org.jahdoo.common.block.altar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.SoundReg;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

import static net.minecraft.sounds.SoundEvents.TRIAL_SPAWNER_AMBIENT;
import static net.minecraft.sounds.SoundEvents.TRIAL_SPAWNER_AMBIENT_OMINOUS;
import static org.jahdoo.trial_nexus.utils.Helpers.Random;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;

public class AltarAnim {

    public static void placeParticle(Vec3 pos, ParticleOptions par1, Level level){
        double randomY = Helpers.Random.nextDouble(0.0, 0.4);
        ParticleHandlers.sendParticles(level, par1, pos.subtract(0,0.4,0), 5, 0, randomY,0, 0.1);
    }

    public static void onActivationAnim(Level level, BlockPos posA) {
        PositionFinders.getOuterRingOfRadius(
            posA.getCenter().subtract(0,0.03,0), 0.1, 50, posB -> setShockwaveNova(posB.subtract(0, 0,0), posA, level)
        );
        Helpers.getSoundWithPosition(level, posA, SoundEvents.DEEPSLATE_BREAK, 1f, 0.6f);
        Helpers.getSoundWithPosition(level, posA, SoundReg.SET_ACTIVE.get(), 3f, 1f);
    }

    static @NotNull BiConsumer<BlockPos, ServerLevel> getBlockPosServerLevelBiConsumer(Level level, BlockState state) {
        return (targetPos, levelA) -> {
            var breakSound = state.getSoundType(level, targetPos, null).getBreakSound();
            if (!levelA.getBlockState(targetPos).is(state.getBlock())) {
                Helpers.getSoundWithPosition(level, targetPos, breakSound);
                levelA.setBlockAndUpdate(targetPos, state);
            }
        };
    }

    private static void setShockwaveNova(Vec3 pos, BlockPos blockPos, Level level){
        var directions = pos.subtract(blockPos.getCenter()).normalize();
        var lifetime = 3;
        var col1 = -8487298;
        var col2 = -13355980;
        var genericParticle = ParticleHandlers.genericParticle(SOFT_PARTICLE, lifetime, 0.06f, col1, col2, true);

        ParticleHandlers.sendParticles(
            level, genericParticle, pos, 0, directions.x, directions.y, directions.z, Random.nextDouble(0.2, 0.6)
        );
    }

    public static void idleParticleAnim(BlockPos pos, int ticks, Level level) {
        PositionFinders.innerRadiusRandom(pos, 0.25, 2,
            positions -> {
                var colourDarker = Helpers.getColourDarker(ColourStore.PERK_GREEN, 0.5f);
                var randomColouredParticle = Helpers.getRandomColouredParticle(ColourStore.PERK_GREEN, colourDarker, 10, 1, false);
                AltarAnim.placeParticle(positions, randomColouredParticle, level);
            }
        );

        if(ticks > 100){
            if (Random.nextInt(20) == 0) {
                var randomSound = List.of(TRIAL_SPAWNER_AMBIENT, TRIAL_SPAWNER_AMBIENT_OMINOUS).get(Random.nextInt(2));
                Helpers.getSoundWithPosition(level, pos, randomSound, 1, 2f);
            }
        }
    }

}
