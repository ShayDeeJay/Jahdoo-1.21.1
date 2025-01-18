package org.jahdoo.challenge;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.casual.arcade.dimensions.ArcadeDimensions;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.casual.arcade.dimensions.level.builder.CustomLevelBuilder;
import net.casual.arcade.dimensions.utils.impl.VoidChunkGenerator;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.Level;
import org.jahdoo.JahdooMod;
import org.jahdoo.attachments.player_abilities.ChallengeAltarData;
import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.client.block_renderer.ShoppingTableRenderer;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.world.level.portal.DimensionTransition.DO_NOTHING;
import static org.jahdoo.block.loot_chest.LootChestBlock.FACING;
import static org.jahdoo.block.shopping_table.ShoppingTableBlock.TEXTURE;
import static org.jahdoo.challenge.LevelGenerator.DimHandler.TRADING_POST;
import static org.jahdoo.challenge.LevelGenerator.DimHandler.TRIAL;
import static org.jahdoo.registers.AttachmentRegister.CHALLENGE_ALTAR;
import static org.jahdoo.utils.ModHelpers.Random;

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

    @FunctionalInterface
    public interface ExceptionRunnable {
        void run() throws CommandSyntaxException;
    }

    public record DimHandler(
        Vec3 spawn,
        String id
    ){
        public static final String TRIAL = "trial";
        public static final String TRADING_POST = "trading_post";

        public static DimHandler trial(){
            return new DimHandler(new Vec3(-24.5, 65, -120.5), TRIAL);
        }

        public static DimHandler tradingPost(){
            return new DimHandler(new Vec3(-21.5, 50, -42.5), TRADING_POST);
        }
    }

    public static DimensionTransition createNewWorld(Player player, ServerLevel serverLevel, ChallengeAltarData altarData, DimHandler handler) {
        var testKey = "end_"+UUID.randomUUID();
        generateNewLevel(serverLevel, testKey);
        var getLevel = new AtomicReference<ServerLevel>();
        findLevel(testKey, serverLevel).ifPresent(
            level -> {
                generateStructure(player, level, altarData, Math.max(altarData.maxRound/5, 1), handler.id());
                getLevel.set(level);
            }
        );

        return new DimensionTransition(getLevel.get(), handler.spawn, player.getDeltaMovement(), 0, 0, DO_NOTHING);
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

    private static void generateStructure(Player player, ServerLevel level, ChallengeAltarData data, int stage, String id){
        var pos = new BlockPos(0, 40, 0);
        var getAllChunks = ChunkPos.rangeClosed(new ChunkPos(pos), 4).toList();
        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, true);
        var isTrial = Objects.equals(id, TRIAL);
        var isTrading = Objects.equals(id, TRADING_POST);

        try {

            if(isTrial) placeStructureJigsaw(level, pos, ModHelpers.res("challenge_arena/challenge_pool_1"));

            if(isTrading) placeStructure(level, pos, ModHelpers.res("trading_post"));

        } catch (CommandSyntaxException e) {
            JahdooMod.LOGGER.log(Level.ALL, e);
            throw new RuntimeException(e);
        }

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, false);
        //Here we can pass the data from the previous altar to set up the next challenge stack.
        if(isTrial){
            playerSetup(player, level, stage);
            setAltarAndData(level, data);
        }

        if(isTrading){
            setLootChests(level);
        }
    }

    private static void setAltarAndData(ServerLevel level, ChallengeAltarData data) {
        var pos = new BlockPos(-25, 67, -72);
        level.setBlockAndUpdate(pos, BlocksRegister.CHALLENGE_ALTAR.get().defaultBlockState());
        if(level.getBlockEntity(pos) instanceof ChallengeAltarBlockEntity altar){
            altar.setData(CHALLENGE_ALTAR, data);
            altar.setChanged();
        }
    }

    private static void setLootChests(ServerLevel level) {
        var pos = new BlockPos(-25, 51, -21);
        var posPurchase = new BlockPos(-20, 61, -26);

        var state = BlocksRegister.LOOT_CHEST.get().defaultBlockState().setValue(FACING, Direction.SOUTH);
        var purchase = BlocksRegister.SHOPPING_TABLE.get().defaultBlockState().setValue(FACING, Direction.WEST);
        level.setBlockAndUpdate(pos, state);
        level.setBlockAndUpdate(pos.west(2), state);
        level.setBlockAndUpdate(pos.west(4), state);

        level.setBlockAndUpdate(posPurchase,  purchase.setValue(TEXTURE, Random.nextInt(4)));
        level.setBlockAndUpdate(posPurchase.south(1), purchase.setValue(TEXTURE, Random.nextInt(4)));
        level.setBlockAndUpdate(posPurchase.south(4), purchase.setValue(TEXTURE, Random.nextInt(4)));
        level.setBlockAndUpdate(posPurchase.south(5), purchase.setValue(TEXTURE, Random.nextInt(4)));
    }

    private static void playerSetup(Player player, ServerLevel serverLevel, int stage) {
        //Remove all effects as should only have ones allowed, which should only be ones on trinkets items etc/
        player.removeAllEffects();

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

    public static void placeStructureJigsaw(ServerLevel sLevel, BlockPos pos, ResourceLocation location) throws CommandSyntaxException {
        Registry<StructureTemplatePool> registry = sLevel.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        Holder<StructureTemplatePool> holder = registry.getHolderOrThrow(ResourceKey.create(Registries.TEMPLATE_POOL, location));
        JigsawPlacement.generateJigsaw(sLevel, holder,  ResourceLocation.withDefaultNamespace("empty"), 10, pos, true);
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
                var box = new BoundingBox(cPos.getMinBlockX(), sLevel.getMinBuildHeight(), cPos.getMinBlockZ(), cPos.getMaxBlockX(), sLevel.getMaxBuildHeight(), cPos.getMaxBlockZ());
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
