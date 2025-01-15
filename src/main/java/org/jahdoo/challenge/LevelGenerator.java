package org.jahdoo.challenge;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.Pair;
import net.casual.arcade.dimensions.ArcadeDimensions;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.casual.arcade.dimensions.level.builder.CustomLevelBuilder;
import net.casual.arcade.dimensions.utils.impl.VoidChunkGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.biome.BiomeData;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.Level;
import org.jahdoo.JahdooMod;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.world.level.portal.DimensionTransition.DO_NOTHING;
import static org.jahdoo.registers.AttachmentRegister.CHALLENGE_ALTAR;

public class LevelGenerator {

    public static void removeCustomLevels(ServerLevel serverLevel) {
        var levelsToRemove = new ArrayList<CustomLevel>();
        for (ServerLevel allLevel : serverLevel.getServer().getAllLevels()) {
            if (allLevel instanceof CustomLevel cLevel) levelsToRemove.add(cLevel);
        }
        for (CustomLevel cLevel : levelsToRemove) removeLevel(serverLevel, cLevel);
    }

    public static void debugLevels(ServerLevel serverLevel) {
        for (ServerLevel allLevel : serverLevel.getServer().getAllLevels()) {
            if (allLevel instanceof CustomLevel cLevel) System.out.println(cLevel.getDescription());
        }
    }

    public static void removeLevel(ServerLevel serverLevel, CustomLevel customLevel) {
        ArcadeDimensions.delete(serverLevel.getServer(), customLevel);
    }

    public static DimensionTransition createNewWorld(Player player, ServerLevel serverLevel, ChallengeAltarData altarData) {
        var testKey = "end_"+UUID.randomUUID();
        generateNewLevel(serverLevel, testKey);
        var getLevel = new AtomicReference<ServerLevel>();
        findLevel(testKey, serverLevel).ifPresent(
            level -> {
                generateStructure(player, level, altarData, Math.max(altarData.maxRound/5, 1));
                getLevel.set(level);
            }
        );
        var pos = new Vec3(-24.5, 65, -120.5);
        return new DimensionTransition(getLevel.get(), pos, player.getDeltaMovement(), 0, 0, DO_NOTHING);
    }

    private static void generateNewLevel(ServerLevel serverLevel, @Nullable String key) {
        var builder = new CustomLevelBuilder()
            .timeOfDay(18000)
            .dimensionType(BuiltinDimensionTypes.OVERWORLD)
            .weather(
                weather -> {
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
            .chunkGenerator(new VoidChunkGenerator(serverLevel.getServer(), Biomes.THE_VOID))
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

    private static void generateStructure(Player player, ServerLevel level, ChallengeAltarData data, int stage){
        var pos = new BlockPos(0, 40, 0);
        var getAllChunks = ChunkPos.rangeClosed(new ChunkPos(pos), 1).toList();
        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, true);

        try {
            placeStructure(level, pos);
        } catch (CommandSyntaxException e) {
            JahdooMod.LOGGER.log(Level.ALL, e);
            throw new RuntimeException(e);
        }

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, false);
        //Here we can pass the data from the previous altar to set up the next challenge stack.
        teleportToPlatform(player, level, stage);
        setAltarAndData(level, data);
    }

    private static void setAltarAndData(ServerLevel level, ChallengeAltarData data) {
        var pos = new BlockPos(-25, 67, -72);
        level.setBlockAndUpdate(pos, BlocksRegister.CHALLENGE_ALTAR.get().defaultBlockState());
        if(level.getBlockEntity(pos) instanceof ChallengeAltarBlockEntity altar){
            altar.setData(CHALLENGE_ALTAR, data);
            altar.setChanged();
        }
    }

    private static void teleportToPlatform(Player player, ServerLevel serverLevel, int stage) {
        player.changeDimension(new DimensionTransition(serverLevel, new Vec3(-24.5, 65, -120.5), player.getDeltaMovement(), 0, 0, DimensionTransition.DO_NOTHING));
        //Remove all effects as should only have ones allowed, which should only be ones on trinkets items etc/
        player.removeAllEffects();
//        player.playNotifySound(SoundRegister.START_TRIAL.get(), SoundSource.NEUTRAL, 1f,1);
        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(40, 50, 30));
            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(ModHelpers.withStyleComponent("Trial Of Strength", ColourStore.PERK_GREEN)));
            serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(ModHelpers.withStyleComponent("Stage " + stage, ColourStore.PERK_GREEN)));
            serverPlayer.playNotifySound(SoundRegister.START_TRIAL.get(), SoundSource.NEUTRAL, 1,1);
        }
    }

    private static Optional<ServerLevel> findLevel(String id, ServerLevel serverLevel) {
        var levels = serverLevel.getServer().getAllLevels();
        for (var level : levels) {
            var isLevel = level.dimension().location().equals(ModHelpers.res(id));
            if(isLevel) return Optional.of(level);
        }
        return Optional.empty();
    }

    public static void placeStructure(ServerLevel sLevel, BlockPos pos) throws CommandSyntaxException {
        Registry<StructureTemplatePool> registry = sLevel.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> holder = registry.getHolderOrThrow(ResourceKey.create(Registries.TEMPLATE_POOL, ModHelpers.res("challenge_arena/challenge_pool_1")));
        JigsawPlacement.generateJigsaw(sLevel, holder,  ResourceLocation.withDefaultNamespace("empty"), 10, pos, true);
    }



}
