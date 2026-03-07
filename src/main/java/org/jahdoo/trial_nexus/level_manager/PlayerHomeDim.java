package org.jahdoo.trial_nexus.level_manager;

import net.casual.arcade.dimensions.ArcadeDimensions;
import net.casual.arcade.dimensions.level.LevelPersistence;
import net.casual.arcade.dimensions.level.builder.CustomLevelBuilder;
import net.casual.arcade.dimensions.utils.impl.VoidChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.level.biome.Biomes.THE_VOID;
import static net.minecraft.world.level.portal.DimensionTransition.DO_NOTHING;
import static org.jahdoo.trial_nexus.level_manager.StructureManager.GLOBAL_Y;
import static org.jahdoo.trial_nexus.level_manager.StructureManager.placeStructure;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.res;

public class PlayerHomeDim {

    public static boolean isPlayerHome(Player player) {
        return player.level().getDescription().getString().contains(getPlayerHome(player));
    }

    public static @NotNull String getPlayerHome(Player player) {
        return player.getName().getString().toLowerCase() + "s_home";
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

//        var pos1 = new BlockPos(0, 89, 0);
//        var waystone = WaystonesAPI.placeWaystone(level, pos1, new WaystoneStyle(id("waystone")));
//
//        if(player instanceof ServerPlayer serverPlayer && waystone.isPresent()) {
//            WaystonesAPI.activateWaystone(serverPlayer, waystone.get());
//        }

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, false);

        return new DimensionTransition(level, posX.getCenter(), player.getDeltaMovement(), 90, 0, DO_NOTHING);
    }
}
