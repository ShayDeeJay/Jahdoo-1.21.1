package org.jahdoo.challenge;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.Pair;
import net.casual.arcade.dimensions.ArcadeDimensions;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.casual.arcade.dimensions.level.builder.CustomLevelBuilder;
import net.casual.arcade.dimensions.utils.impl.VoidChunkGenerator;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.Level;
import org.jahdoo.JahdooMod;
import org.jahdoo.utils.ModHelpers;

import javax.annotation.Nullable;
import java.util.*;

public class LevelGenerator {

    public static void removeCustomLevels(ServerLevel serverLevel) {
        var levelsToRemove = new ArrayList<CustomLevel>();
        for (ServerLevel allLevel : serverLevel.getServer().getAllLevels()) {
            if (allLevel instanceof CustomLevel cLevel) levelsToRemove.add(cLevel);
        }
        for (CustomLevel cLevel : levelsToRemove) removeLevel(serverLevel, cLevel);
    }

    public static void removeLevel(ServerLevel serverLevel, CustomLevel customLevel) {
        ArcadeDimensions.delete(serverLevel.getServer(), customLevel);
    }

    public static void createNewWorld(Player player, ServerLevel serverLevel) {
        var testKey = "end_"+UUID.randomUUID();
        generateNewLevel(serverLevel, testKey);
        findLevel(testKey, serverLevel).ifPresent(level -> generateStructure(player, level));
    }

    private static void generateNewLevel(ServerLevel serverLevel, @Nullable String key) {
        var dimensionWithBiomes = Pair.of(BuiltinDimensionTypes.OVERWORLD, List.of(Biomes.THE_VOID, Biomes.SNOWY_TAIGA, Biomes.PLAINS));
        var randomBiome = ModHelpers.Random.nextInt(dimensionWithBiomes.second().size());
        var biome = dimensionWithBiomes.second().get(randomBiome);
        var builder = new CustomLevelBuilder()
            .timeOfDay(biome == Biomes.PLAINS ? 13000 : 18000)
            .dimensionType(dimensionWithBiomes.first())
            .weather(
                weather -> {
                    weather.setRaining(biome == Biomes.SNOWY_TAIGA);
                    weather.setThundering(false);
                    return null;
                }
            )
            .difficulty(
                difficulty -> {
                    difficulty.setValue(Difficulty.HARD);
                    return null;
                }
            )
            .chunkGenerator(new VoidChunkGenerator(serverLevel.getServer(), biome))
            .dimensionKey(ModHelpers.res(key == null ? UUID.randomUUID().toString() : key))
            .gameRules(
                (gameRules) -> {
                    gameRules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, null);
                    gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
                    gameRules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, null);
                    gameRules.getRule(GameRules.RULE_NATURAL_REGENERATION).set(false, null);
                    gameRules.getRule(GameRules.RULE_MOBGRIEFING).set(false, null);
                    return null;
                }
            );

        ArcadeDimensions.add(serverLevel.getServer(), builder);
    }

    private static void generateStructure(Player player, ServerLevel level){
        var pos = new BlockPos(0, 40, 0);
        var getAllChunks = ChunkPos.rangeClosed(new ChunkPos(pos), 5).toList();
        for (var chunkPos : getAllChunks) {
            level.setChunkForced(chunkPos.x, chunkPos.z, true);
        }

        try {
            placeStructure(level, pos, ModHelpers.res("trial"));
        } catch (CommandSyntaxException e) {
            JahdooMod.logger.log(Level.ALL, e);
            throw new RuntimeException(e);
        }

        for (var chunkPos : getAllChunks) {
            level.setChunkForced(chunkPos.x, chunkPos.z, false);
        }

        var center = pos.getCenter();

        teleportToPlatform(player, level, center);
    }

    private static void teleportToPlatform(Player player, ServerLevel level, Vec3 center) {
        player.changeDimension(
            new DimensionTransition(
                level, center.subtract(24, 2, 4), player.getDeltaMovement(), player.getYRot(), player.getXRot(), DimensionTransition.DO_NOTHING
            )
        );
        player.removeAllEffects();
    }

    private static Optional<ServerLevel> findLevel(String id, ServerLevel serverLevel) {
        var levels = serverLevel.getServer().getAllLevels();
        for (var level : levels) {
            var isLevel = level.dimension().location().equals(ModHelpers.res(id));
            if(isLevel) return Optional.of(level);
        }
        return Optional.empty();
    }


    public static void placeStructure(ServerLevel sLevel, BlockPos pos, ResourceLocation id) throws CommandSyntaxException {
        var registry = sLevel.registryAccess().registry(Registries.STRUCTURE);
        if(registry.isEmpty()) return;

        var structure = registry.get().get(id);
        if(structure == null) return;

        var chunkgenerator = sLevel.getChunkSource().getGenerator();

        var structurestart = structure.generate(
            sLevel.registryAccess(),
            chunkgenerator,
            chunkgenerator.getBiomeSource(),
            sLevel.getChunkSource().randomState(),
            sLevel.getStructureManager(),
            sLevel.getSeed(),
            new ChunkPos(pos),
            0,
            sLevel,
            (biomeHolder) -> true
        );

        if (structurestart.isValid()) {
            var boundingbox = structurestart.getBoundingBox();
            var chunkPosPrimary = new ChunkPos(SectionPos.blockToSectionCoord(boundingbox.minX()), SectionPos.blockToSectionCoord(boundingbox.minZ()));
            var chunkPosSecondary = new ChunkPos(SectionPos.blockToSectionCoord(boundingbox.maxX()), SectionPos.blockToSectionCoord(boundingbox.maxZ()));
            checkLoaded(sLevel, chunkPosPrimary, chunkPosSecondary);
            for (var cPos : ChunkPos.rangeClosed(chunkPosPrimary, chunkPosSecondary).toList()) {
                var box = new BoundingBox(cPos.getMinBlockX(), sLevel.getMinBuildHeight(), cPos.getMinBlockZ(), cPos.getMaxBlockX(), sLevel.getMaxBuildHeight() , cPos.getMaxBlockZ());
                structurestart.placeInChunk(sLevel, sLevel.structureManager(), chunkgenerator, sLevel.getRandom(), box, cPos);
            }
        }
    }

    private static void checkLoaded(ServerLevel level, ChunkPos start, ChunkPos end) throws CommandSyntaxException {
        if (ChunkPos.rangeClosed(start, end).anyMatch((chunkPos) -> !level.isLoaded(chunkPos.getWorldPosition()))) {
            throw BlockPosArgument.ERROR_NOT_LOADED.create();
        }
    }

}
