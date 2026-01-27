package org.jahdoo.trial_nexus.level_manager;

import net.casual.arcade.dimensions.ArcadeDimensions;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.casual.arcade.dimensions.level.LevelPersistence;
import net.casual.arcade.dimensions.level.builder.CustomLevelBuilder;
import net.casual.arcade.dimensions.utils.impl.VoidChunkGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jahdoo.trial_nexus.utils.Helpers;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.world.level.portal.DimensionTransition.DO_NOTHING;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.DamageTypeReg.BIOME_SOURCE;
import static org.jahdoo.trial_nexus.level_manager.StructureManager.generateStartingRoom;
import static org.jahdoo.trial_nexus.utils.Helpers.res;

public class LevelGenerator {

    public static final String LEVEL_PREFIX = "the_trial_nexus";

    public static void removeLevel(CustomLevel customLevel) {
        ArcadeDimensions.delete(customLevel.getServer(), customLevel);
    }

    public static void debugLevels(ServerLevel serverLevel, Player player) {
        for (ServerLevel allLevel : serverLevel.getServer().getAllLevels()) {
            if (allLevel instanceof CustomLevel cLevel) {
                var rgb = Helpers.getRgb();
                player.sendSystemMessage(Helpers.withStyleComponent(cLevel.getDescription().getString(), rgb));
                player.sendSystemMessage(Helpers.withStyleComponent(cLevel.getData(INSTANCE_DATA).toString(), rgb));
            }
        }
    }

    public static void removeCustomLevels(ServerLevel serverLevel) {
        var levelsToRemove = new ArrayList<CustomLevel>();
        for (ServerLevel allLevel : serverLevel.getServer().getAllLevels()) {
            if (allLevel instanceof CustomLevel cLevel && cLevel.players().isEmpty()) levelsToRemove.add(cLevel);
        }
        for (CustomLevel cLevel : levelsToRemove) removeLevel(cLevel);
    }

    private static Optional<ServerLevel> findLevel(String id, ServerLevel serverLevel) {
        var levels = serverLevel.getServer().getAllLevels();
        for (var level : levels) {
            var isLevel = level.dimension().location().equals(res(id));
            if(isLevel) return Optional.of(level);
        }
        return Optional.empty();
    }

    private static void generateNewLevel(ServerLevel serverLevel, String key) {
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
                    return null;
                }
            );
        builder.setPersistence(LevelPersistence.Persistent);
        ArcadeDimensions.add(serverLevel.getServer(), builder);
    }

    public static DimensionTransition createLevelAndStartingRoom(Player player, ServerLevel serverLevel) {
        var levelKey = LEVEL_PREFIX + "-" + UUID.randomUUID();
        var getLevel = new AtomicReference<ServerLevel>();

        generateNewLevel(serverLevel, levelKey);

        findLevel(levelKey, serverLevel).ifPresent(
            level -> {
                generateStartingRoom(level);
                getLevel.set(level);
            }
        );

        return new DimensionTransition(getLevel.get(), StructureManager.SPAWN_POSITION, player.getDeltaMovement(), 90, 0, DO_NOTHING);
    }

}
