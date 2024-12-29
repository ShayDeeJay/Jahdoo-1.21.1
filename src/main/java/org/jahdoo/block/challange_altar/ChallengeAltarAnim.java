package org.jahdoo.block.challange_altar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.List;

import static net.minecraft.sounds.SoundEvents.TRIAL_SPAWNER_AMBIENT;
import static net.minecraft.sounds.SoundEvents.TRIAL_SPAWNER_AMBIENT_OMINOUS;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.SOFT_PARTICLE_SELECTION;
import static org.jahdoo.utils.ModHelpers.Random;

public class ChallengeAltarAnim {

    public static void idleParticleAnim(BlockPos pPos, int privateTicks, Level level) {
        var element = ElementRegistry.getRandomElement();
        var par1 = ParticleHandlers.bakedParticleOptions(element.getTypeId(), 20, 1.5f, false);
        var par2 = ParticleHandlers.genericParticleOptions(
            ParticleStore.GENERIC_PARTICLE_SELECTION, element, 20, 1.5f, false, 0.3
        );
        PositionGetters.getInnerRingOfRadiusRandom(pPos, 0.25, 2,
            positions -> ChallengeAltarAnim.placeParticle(positions, Random.nextInt(0, 3) == 0 ? par1 : par2, level, privateTicks)
        );

        if(privateTicks > 100){
            if (Random.nextInt(20) == 0) {
                var normal = TRIAL_SPAWNER_AMBIENT;
                var ominous = TRIAL_SPAWNER_AMBIENT_OMINOUS;
                var randomSound = List.of(normal, ominous).get(Random.nextInt(2));
                ModHelpers.getSoundWithPosition(level, pPos, randomSound, 1, 2f);
            }
        }
    }

    public static void onActivationAnim(Level pLevel, BlockPos pPos, int privateTicks) {
        PositionGetters.getOuterRingOfRadius(
            pPos.getCenter().subtract(0,0.03,0), 0.1, 50, pos -> setShockwaveNova(pos.subtract(0, 0,0), pPos, pLevel)
        );
        ModHelpers.getSoundWithPosition(pLevel, pPos, SoundEvents.DEEPSLATE_BREAK, 0.4f, 0.6f);
        ModHelpers.getSoundWithPosition(pLevel, pPos, SoundEvents.VAULT_OPEN_SHUTTER, 0.4f, 0f);
    }


    public static void placeParticle(Vec3 pos, ParticleOptions par1, Level level, int privateTicks){
        double randomY = ModHelpers.Random.nextDouble(0.0, 0.4);
        ParticleHandlers.sendParticles(level, par1, pos.subtract(0,0.4,0), privateTicks <= 100 ? 8 : 0, 0, randomY,0, privateTicks == 94 ? 1.4 : privateTicks > 94 ? 0.7 : 0.1);
    }

    private static void setShockwaveNova(Vec3 worldPosition, BlockPos pos, Level level){
        var directions = worldPosition.subtract(pos.getCenter()).normalize();
        var lifetime = 3;
        var col1 = -8487298;
        var col2 = -13355980;
        var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, lifetime, 0.06f, col1, col2, true);

        ParticleHandlers.sendParticles(
            level, genericParticle, worldPosition, 0, directions.x, directions.y, directions.z, Random.nextDouble(0.2, 0.6)
        );
    }

}