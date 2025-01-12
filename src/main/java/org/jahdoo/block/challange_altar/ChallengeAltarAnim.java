package org.jahdoo.block.challange_altar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.List;

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

    static void portalBuilder(Level pLevel, ServerLevel serverLevel, int buildTick, int placeCounter, BlockPos pos) {
        if (buildTick % 3 == 0) {
            var mossyStoneBricks = Blocks.MOSSY_STONE_BRICKS;
            var south = pos.south(4);
            var breakSound = mossyStoneBricks.defaultBlockState().getSoundType().getBreakSound();

            if (placeCounter < 5) {
                var above = south.east(2).above(placeCounter);
                var above1 = south.west(2).above(placeCounter);
                if(serverLevel.getBlockState(above1).isAir()){
                    ModHelpers.getSoundWithPosition(pLevel, above1, breakSound);
                    serverLevel.setBlockAndUpdate(above1, mossyStoneBricks.defaultBlockState());
                }
                if(serverLevel.getBlockState(above).canBeReplaced()){
                    ModHelpers.getSoundWithPosition(pLevel, above, breakSound);
                    serverLevel.setBlockAndUpdate(above, mossyStoneBricks.defaultBlockState());
                }
            } else if (placeCounter < 8) {
                int horizontalStep = placeCounter - 5; // Normalize to 0-2 range
                var above = south.east(horizontalStep).above(5);
                var above1 = south.west(horizontalStep).above(5);
                if(serverLevel.getBlockState(above1).isAir()){
                    ModHelpers.getSoundWithPosition(pLevel, above1, breakSound);
                    serverLevel.setBlockAndUpdate(above1, mossyStoneBricks.defaultBlockState());
                }
                if(serverLevel.getBlockState(above).isAir()){
                    ModHelpers.getSoundWithPosition(pLevel, above, breakSound);
                    serverLevel.setBlockAndUpdate(above, mossyStoneBricks.defaultBlockState());
                }
            }

            if(buildTick == 30){
                serverLevel.setBlockAndUpdate(pos, LOOT_CHEST.get().defaultBlockState().setValue(FACING, Direction.SOUTH));
                ModHelpers.getSoundWithPosition(pLevel, south, SoundEvents.END_PORTAL_SPAWN, 0.8F, 1.5F);
                for (int i = 0; i < 5; i ++){
                    serverLevel.setBlockAndUpdate(south.above(i), TRAIL_PORTAL.get().defaultBlockState());
                    serverLevel.setBlockAndUpdate(south.west().above(i), TRAIL_PORTAL.get().defaultBlockState());
                    serverLevel.setBlockAndUpdate(south.east().above(i), TRAIL_PORTAL.get().defaultBlockState());
                }
            }

            placeCounter++;
            if (placeCounter >= 8) placeCounter = 0;
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
