package org.jahdoo.ascension;

import kotlin.Unit;
import net.casual.arcade.dimensions.ArcadeDimensions;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.casual.arcade.dimensions.level.LevelProperties;
import net.casual.arcade.dimensions.level.builder.CustomLevelBuilder;
import net.casual.arcade.dimensions.utils.impl.VoidChunkGenerator;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.portal.DimensionTransition;
import org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData;
import org.jahdoo.ascension.attachments.player_abilities.InstanceData;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.common.registers.SoundReg;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.world.level.biome.Biomes.THE_VOID;
import static net.minecraft.world.level.portal.DimensionTransition.DO_NOTHING;
import static org.jahdoo.ascension.StructureManager.generateStructure;
import static org.jahdoo.ascension.utils.Helpers.res;
import static org.jahdoo.ascension.utils.Helpers.withStyleComponent;
import static org.jahdoo.common.registers.AttachmentReg.CHALLENGE_ALTAR;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public class LevelGenerator {

    public static void removeLevel(CustomLevel customLevel) {
        ArcadeDimensions.delete(customLevel.getServer(), customLevel);
    }

    private static Unit setDifficulty(LevelProperties.DifficultyProperties difficulty) {
        difficulty.setValue(Difficulty.HARD);
        return null;
    }

    private static Unit wetWeather(LevelProperties.WeatherProperties weather) {
        weather.setThundering(false);
        return null;
    }

    public static void debugLevels(ServerLevel serverLevel) {
        for (ServerLevel allLevel : serverLevel.getServer().getAllLevels()) {
            if (allLevel instanceof CustomLevel cLevel) System.out.println(cLevel.getDescription());
        }
    }

    public static void removeCustomLevels(ServerLevel serverLevel) {
        var levelsToRemove = new ArrayList<CustomLevel>();
        for (ServerLevel allLevel : serverLevel.getServer().getAllLevels()) {
            if (allLevel instanceof CustomLevel cLevel) levelsToRemove.add(cLevel);
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

    private static Unit setGameRules(GameRules gameRules) {
        gameRules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, null);
        gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
        gameRules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, null);
        gameRules.getRule(GameRules.RULE_NATURAL_REGENERATION).set(false, null);
        gameRules.getRule(GameRules.RULE_MOBGRIEFING).set(false, null);
        return null;
    }

    public static void playerSetup(Player player, int stage) {
        //Remove all effects as should only have ones allowed, which should only be ones on trinkets items etc/
        player.removeAllEffects();

        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(40, 50, 30));
            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(withStyleComponent("Trial Of Strength", ColourStore.PERK_GREEN)));
            serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(withStyleComponent("Stage " + stage, ColourStore.PERK_GREEN)));
            serverPlayer.playNotifySound(SoundReg.START_TRIAL.get(), SoundSource.BLOCKS, 1, 1);
        }
    }

    private static void generateNewLevel(ServerLevel serverLevel, String key) {
        var builder = new CustomLevelBuilder()
            .timeOfDay(18000)
            .dimensionType(BuiltinDimensionTypes.OVERWORLD)
            .chunkGenerator(new VoidChunkGenerator(serverLevel.getServer(), THE_VOID))
            .dimensionKey(res(key == null ? UUID.randomUUID().toString() : key))
            .difficulty(LevelGenerator::setDifficulty)
            .weather(LevelGenerator::wetWeather)
            .gameRules(LevelGenerator::setGameRules);

        ArcadeDimensions.add(serverLevel.getServer(), builder);
    }

    public static DimensionTransition createNewWorld(Player player, ServerLevel serverLevel, ChallengeLevelData altarData, DimHandler handler) {
        var testKey = handler.id() + "-" + UUID.randomUUID();
        var getLevel = new AtomicReference<ServerLevel>();

        generateNewLevel(serverLevel, testKey);

        findLevel(testKey, serverLevel).ifPresent(
            level -> {
                generateStructure(level, altarData, handler.id());
                level.setData(INSTANCE_DATA, new InstanceData());
                level.setData(CHALLENGE_ALTAR, altarData);
                getLevel.set(level);
            }
        );

        return new DimensionTransition(getLevel.get(), handler.spawn(), player.getDeltaMovement(), 0, 0, DO_NOTHING);
    }

}
