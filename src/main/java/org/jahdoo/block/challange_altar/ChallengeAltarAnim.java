package org.jahdoo.block.challange_altar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

import static net.minecraft.sounds.SoundEvents.TRIAL_SPAWNER_AMBIENT;
import static net.minecraft.sounds.SoundEvents.TRIAL_SPAWNER_AMBIENT_OMINOUS;
import static org.jahdoo.block.loot_chest.LootChestBlock.FACING;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.SOFT_PARTICLE_SELECTION;
import static org.jahdoo.registers.BlocksRegister.LOOT_CHEST;
import static org.jahdoo.registers.BlocksRegister.TRAIL_PORTAL;
import static org.jahdoo.utils.ModHelpers.Random;

public class ChallengeAltarAnim {

    public static void idleParticleAnim(BlockPos pPos, int privateTicks, Level level) {
        PositionGetters.getInnerRingOfRadiusRandom(pPos, 0.25, 2,
            positions -> {
                var colourDarker = ModHelpers.getColourDarker(ColourStore.PERK_GREEN, 0.5f);
                var randomColouredParticle = ModHelpers.getRandomColouredParticle(ColourStore.PERK_GREEN, colourDarker, 10, 1, false);
                ChallengeAltarAnim.placeParticle(positions, randomColouredParticle, level, privateTicks);
            }
        );

        if(privateTicks > 100){
            if (Random.nextInt(20) == 0) {
                var randomSound = List.of(TRIAL_SPAWNER_AMBIENT, TRIAL_SPAWNER_AMBIENT_OMINOUS).get(Random.nextInt(2));
                ModHelpers.getSoundWithPosition(level, pPos, randomSound, 1, 2f);
            }
        }
    }

    static @NotNull BiConsumer<BlockPos, ServerLevel> getBlockPosServerLevelBiConsumer(Level pLevel, BlockState mossyStoneBricks) {
        return (targetPos, level1) -> {
            var breakSound = mossyStoneBricks.getSoundType(pLevel, targetPos, null).getBreakSound();
            if (!level1.getBlockState(targetPos).is(mossyStoneBricks.getBlock())) {
                ModHelpers.getSoundWithPosition(pLevel, targetPos, breakSound);
                level1.setBlockAndUpdate(targetPos, mossyStoneBricks);
            }
        };
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
        ParticleHandlers.sendParticles(level, par1, pos.subtract(0,0.4,0), privateTicks <= 100 ? 5 : 0, 0, randomY,0, privateTicks == 94 ? 1.4 : privateTicks > 90 ? 0.4 : 0.1);
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
