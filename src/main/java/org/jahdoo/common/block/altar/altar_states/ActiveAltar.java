package org.jahdoo.common.block.altar.altar_states;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.entities.safe.Safe;
import org.jahdoo.common.registers.SoundReg;
import org.shaydee.shaydeeapi.Helpers;
import org.shaydee.shaydeeapi.Maths;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import static net.minecraft.core.BlockPos.containing;
import static org.jahdoo.common.block.altar.AltarAnim.idleParticleAnim;
import static org.jahdoo.common.block.altar.AltarBlockEntity.roomBounding;
import static org.jahdoo.trial_nexus.mobs.MobSpawnManager.addAndPositionEntity;
import static org.jahdoo.trial_nexus.mobs.MobSpawnManager.championSpawn;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;
import static org.jahdoo.trial_nexus.utils.PositionFinders.innerRadiusRandom;

public class ActiveAltar {

    public static void onActiveAltar(AltarBlockEntity aEntity, ServerLevel serverLevel) {
        if(!aEntity.started) return;
        aEntity.incrementPrivateTicks();

        var blockPos = aEntity.getBlockPos();
        var validSpawnPositions = getValidSpawnPositions(blockPos, serverLevel);

        reAssignTarget(serverLevel, blockPos);
        removeKilledMobs(aEntity, serverLevel);
        manageMobSpawns(aEntity, serverLevel, validSpawnPositions);
        spawnSafe(aEntity, serverLevel, validSpawnPositions);
        idleParticleAnim(blockPos, aEntity.getPrivateTicks(), serverLevel);
    }

    private static boolean spawnNotFluid(ServerLevel serverLevel, Vec3 pos1) {
        var aPos = containing(pos1);
        var spotC = serverLevel.getBlockState(aPos.below());

        return spotC.getFluidState().isEmpty();
    }

    private static boolean hitboxFits(ServerLevel serverLevel, Vec3 pos1) {
        var aPos = containing(pos1);
        var spotA = serverLevel.getBlockState(aPos);
        var spotB = serverLevel.getBlockState(aPos.above());

        return spotA.isAir() && spotB.isAir();
    }

    private static Vec3 getValidSpawnPositions(BlockPos pos, ServerLevel serverLevel) {
        var randomPoses = innerRadiusRandom(pos.below(2).getCenter(), 20, 200)
            .stream()
            .filter(pos1 -> hitboxFits(serverLevel, pos1) && spawnNotFluid(serverLevel, pos1))
            .toList();

        return randomPoses.isEmpty() ? pos.getCenter() : Helpers.listRandom(randomPoses);
    }

    private static void removeKilledMobs(AltarBlockEntity aEntity, ServerLevel serverLevel) {
        for (var activeMob : aEntity.onField) {
            var entity = serverLevel.getEntity(activeMob);
            if(entity == null || !entity.isAlive()){
                aEntity.onField.remove(activeMob);
                aEntity.mobsSpawned++;
                return;
            }
        }
    }

    private static void manageMobSpawns(AltarBlockEntity aEntity, ServerLevel serverLevel, Vec3 pos) {
        if(aEntity.getPrivateTicks() % 2 != 0) return;
        var maxMobsOnField = aEntity.getInstanceDifficulty().getMaxMobsOnField();
        var hasMobsToSpawn = !aEntity.spawnableMobs.isEmpty();
        var maxNotReached = aEntity.onField.size() < maxMobsOnField;

        if(hasMobsToSpawn && maxNotReached){
            var entity = Helpers.listRandom(aEntity.spawnableMobs);
            addAndPositionEntity(serverLevel, containing(pos), entity);

            aEntity.spawnableMobs.remove(entity);
            aEntity.spawnedChampion = championSpawn(serverLevel, entity, aEntity.spawnedChampion);
            aEntity.onField.add(entity.getUUID());
        }
    }

    private static void spawnSafe(AltarBlockEntity aEntity, ServerLevel serverLevel, Vec3 pos) {
        if(!aEntity.spawnedSafe && Random.nextInt(200 - aEntity.getPrivateTicks()) == 0){
            var difficulty = aEntity.getInstanceDifficulty();
            var id = difficulty.getId();
            var percentageChance = (2 + id) * 10;

            if (Maths.percentageChance(percentageChance)) {
                var origin = id * 100;
                var getSafe = new Safe(serverLevel, Random.nextInt(origin, origin * 2), 20);

                getSafe.moveTo(pos);
                getSafe.lookAt(EntityAnchorArgument.Anchor.EYES, aEntity.getBlockPos().getCenter());
                serverLevel.addFreshEntity(getSafe);
                SoundHelpers.getSoundWithPosition(serverLevel, pos, SoundReg.SWORD_THUD.value(), SoundSource.HOSTILE, 5F, 1.8F);
            }

            aEntity.spawnedSafe = true;
        }
    }

    private static void reAssignTarget(Level level, BlockPos pos) {
        if (!(level instanceof CustomLevel cLevel)) return;

        var bounds = roomBounding(pos);
        var entities = cLevel.getEntities(null, bounds);
        var validTargets = entities.stream()
            .filter(ActiveAltar::isValidTarget)
            .map(e -> (LivingEntity) e)
            .toList();

        if (validTargets.isEmpty()) return;
        entities.stream()
            .filter(e -> e instanceof Mob mob && mob.getTarget() == null)
            .map(e -> (Mob) e)
            .forEach(mob -> mob.setTarget(Helpers.listRandom(validTargets)));
    }

    public static boolean isValidTarget(Entity e) {
        return e instanceof Player || (e instanceof ITamableEntity t && t.getOwner() != null);
    }

}
