package org.jahdoo.common.block.altar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.utils.ColourStore;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.shaydee.shaydeeapi.Helpers;

import java.util.List;

import static net.minecraft.sounds.SoundEvents.TRIAL_SPAWNER_AMBIENT;
import static net.minecraft.sounds.SoundEvents.TRIAL_SPAWNER_AMBIENT_OMINOUS;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public class AltarAnim {

    public static void placeParticle(Vec3 pos, ParticleOptions par1, Level level){
        double randomY = JahdooHelpers.Random.nextDouble(0.0, 0.4);
        ParticleHandlers.sendParticles(level, par1, pos.subtract(0,0.4,0), 5, 0, randomY,0, 0.1);
    }

    public static void onActivationAnim(Level level, BlockPos posA) {
        PositionFinders.getOuterRingOfRadius(
            posA.getCenter().subtract(0,0.03,0), 0.1, 50, posB -> setShockwaveNova(posB.subtract(0, 0,0), posA, level)
        );
        Helpers.getSoundWithPosition(level, posA, SoundEvents.DEEPSLATE_BREAK, SoundSource.BLOCKS, 1f, 0.6f);
        Helpers.getSoundWithPosition(level, posA, SoundReg.SET_ACTIVE.get(), SoundSource.BLOCKS, 3f, 1f);
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
                var colourDarker = JahdooHelpers.getColourDarker(ColourStore.PERK_GREEN, 0.5f);
                var randomColouredParticle = JahdooHelpers.getRandomColouredParticle(ColourStore.PERK_GREEN, colourDarker, 10, 1, false);
                AltarAnim.placeParticle(positions, randomColouredParticle, level);
            }
        );

        if(ticks > 100){
            if (Random.nextInt(20) == 0) {
                var randomSound = List.of(TRIAL_SPAWNER_AMBIENT, TRIAL_SPAWNER_AMBIENT_OMINOUS).get(Random.nextInt(2));
                Helpers.getSoundWithPosition(level, pos, randomSound, SoundSource.BLOCKS, 1F, 2f);
            }
        }
    }

}
