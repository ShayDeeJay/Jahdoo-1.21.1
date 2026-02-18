package org.jahdoo.trial_nexus.level_manager;

import net.blay09.mods.waystones.api.WaystoneStyle;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.casual.arcade.dimensions.ArcadeDimensions;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.casual.arcade.dimensions.level.LevelPersistence;
import net.casual.arcade.dimensions.level.builder.CustomLevelBuilder;
import net.casual.arcade.dimensions.utils.impl.VoidChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static net.blay09.mods.waystones.Waystones.id;
import static net.minecraft.world.level.biome.Biomes.THE_VOID;
import static net.minecraft.world.level.portal.DimensionTransition.DO_NOTHING;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.DamageTypeReg.BIOME_SOURCE;
import static org.jahdoo.trial_nexus.level_manager.StructureManager.*;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.res;

public class LevelGenerator {

    public static final String LEVEL_PREFIX = "the_trial_nexus";

    public static void removeLevel(CustomLevel customLevel) {
        ArcadeDimensions.delete(customLevel.getServer(), customLevel);
    }

    public static void debugLevels(ServerLevel serverLevel, Player player) {
        for (ServerLevel allLevel : serverLevel.getServer().getAllLevels()) {
            if (allLevel instanceof CustomLevel cLevel && LevelGenerator.isNexus(cLevel)) {
                var rgb = ColourHelpers.getRgb();
                player.sendSystemMessage(TextHelpers.withStyleComponent(cLevel.getDescription().getString(), rgb));
                player.sendSystemMessage(TextHelpers.withStyleComponent(cLevel.getData(INSTANCE_DATA).toString(), rgb));
            }
        }
    }

    public static boolean isNexusLevel(Level level){
        return level instanceof CustomLevel cLevel && LevelGenerator.isNexus(cLevel);
    }

    public static boolean isNexus(Level level){
        return level.getDescription().getString().contains(LevelGenerator.LEVEL_PREFIX);
    }

    public static void removeCustomLevels(ServerLevel serverLevel) {
        var levelsToRemove = new ArrayList<CustomLevel>();
        for (ServerLevel allLevel : serverLevel.getServer().getAllLevels()) {
            if (allLevel instanceof CustomLevel cLevel && LevelGenerator.isNexus(cLevel) && cLevel.players().isEmpty()) levelsToRemove.add(cLevel);
        }
        for (CustomLevel cLevel : levelsToRemove) removeLevel(cLevel);
    }

    public static Optional<ServerLevel> findLevel(String id, ServerLevel serverLevel) {
        var levels = serverLevel.getServer().getAllLevels();
        for (var level : levels) {
            var isLevel = level.dimension().location().equals(res(id));
            if(isLevel) return Optional.of(level);
        }
        return Optional.empty();
    }

    private static void generateNexusDim(ServerLevel serverLevel, String key) {
        var builder = new CustomLevelBuilder()
            .timeOfDay(18000)
            .dimensionType(BuiltinDimensionTypes.OVERWORLD)
            .chunkGenerator(new VoidChunkGenerator(serverLevel.getServer(), BIOME_SOURCE))
            .dimensionKey(res(key == null ? UUID.randomUUID().toString() : key))

            .difficulty(
                difficulty -> {
                    difficulty.setValue(Difficulty.HARD);
                    return null;
                }
            )
            .weather(
                weather -> {
                    weather.setThundering(false);
                    return null;
                }
            )
            .gameRules(
                gameRules -> {
                    gameRules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, null);
                    gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
                    gameRules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, null);
                    gameRules.getRule(GameRules.RULE_NATURAL_REGENERATION).set(false, null);
                    gameRules.getRule(GameRules.RULE_MOBGRIEFING).set(false, null);
                    gameRules.getRule(GameRules.RULE_DOFIRETICK).set(false, null);
                    gameRules.getRule(GameRules.RULE_DOENTITYDROPS).set(false, null);
                    return null;
                }
            );
        builder.setPersistence(LevelPersistence.Persistent);
        ArcadeDimensions.add(serverLevel.getServer(), builder);
    }

    public static DimensionTransition generateNewHome(ServerLevel serverLevel, Player player, BlockPos pos) {
        var builder = new CustomLevelBuilder()
            .timeOfDay(18000)
            .dimensionType(BuiltinDimensionTypes.OVERWORLD)
            .dimensionKey(res(getPlayerHome(player)))
            .chunkGenerator(new VoidChunkGenerator(serverLevel.getServer(), THE_VOID))
            .difficulty(
                difficulty -> {
                    difficulty.setValue(Difficulty.HARD);
                    return null;
                }
            )
            .weather(
                weather -> {
                    weather.setThundering(false);
                    weather.setRaining(false);
                    return null;
                }
            )
            .gameRules(
                gameRules -> {
                    gameRules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, null);
                    gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
                    gameRules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, null);
                    gameRules.getRule(GameRules.RULE_MOBGRIEFING).set(false, null);
                    gameRules.getRule(GameRules.RULE_DOINSOMNIA).set(false, null);
                    return null;
                }
            );

        builder.setPersistence(LevelPersistence.Persistent);
        var level = ArcadeDimensions.add(serverLevel.getServer(), builder);

        var posX = new BlockPos(-72, GLOBAL_Y, -73);
        var getAllChunks = ChunkPos.rangeClosed(new ChunkPos(posX), 1).toList();
        var settings = new StructurePlaceSettings();

        settings.setRotationPivot(new BlockPos(posX.getX() + 23, posX.getY(), posX.getZ() + 25));
        settings.setRotation(Rotation.CLOCKWISE_90);

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, true);
        placeStructure(level, posX, new StructurePlaceSettings() , "base");
        var pos1 = new BlockPos(0, 89, 0);
        var waystone = WaystonesAPI.placeWaystone(level, pos1, new WaystoneStyle(id("waystone")));

        if(player instanceof ServerPlayer serverPlayer && waystone.isPresent()) {
            WaystonesAPI.activateWaystone(serverPlayer, waystone.get());
        }

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, false);

        return new DimensionTransition(level, posX.getCenter(), player.getDeltaMovement(), 90, 0, DO_NOTHING);
    }

    public static @NotNull String getPlayerHome(Player player) {
        return player.getName().getString().toLowerCase() + "s_home";
    }


    public static DimensionTransition createLevelAndStartingRoom(Player player, ServerLevel serverLevel) {
        var levelKey = LEVEL_PREFIX + "-" + UUID.randomUUID();
        var getLevel = new AtomicReference<ServerLevel>();

        generateNexusDim(serverLevel, levelKey);

        findLevel(levelKey, serverLevel).ifPresent(
            level -> {
                generateStartingRoom(level);
                getLevel.set(level);
            }
        );

        return new DimensionTransition(getLevel.get(), StructureManager.SPAWN_POSITION, player.getDeltaMovement(), 90, 0, DO_NOTHING);
    }

}
