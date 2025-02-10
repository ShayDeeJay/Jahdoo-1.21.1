package org.jahdoo.challenge;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.casual.arcade.dimensions.ArcadeDimensions;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.casual.arcade.dimensions.level.builder.CustomLevelBuilder;
import net.casual.arcade.dimensions.utils.impl.VoidChunkGenerator;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.*;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.Level;
import org.jahdoo.JahdooMod;
import org.jahdoo.attachments.player_abilities.ChallengeLevelData;
import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.block.shopping_table.ShoppingTableEntity;
import org.jahdoo.challenge.trading_post.ItemCosts;
import org.jahdoo.challenge.trading_post.ShoppingItems;
import org.jahdoo.items.runes.rune_data.RuneData;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ColourStore;
import org.jahdoo.utils.ModHelpers;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.world.level.portal.DimensionTransition.DO_NOTHING;
import static org.jahdoo.block.TrialPortalBlock.*;
import static org.jahdoo.block.loot_chest.LootChestBlock.FACING;
import static org.jahdoo.block.shopping_table.ShoppingTableBlock.TEXTURE;
import static org.jahdoo.challenge.LevelGenerator.DimHandler.TRADING_POST;
import static org.jahdoo.challenge.LevelGenerator.DimHandler.TRIAL;
import static org.jahdoo.challenge.trading_post.ShoppingItems.getEliteShoppingItem;
import static org.jahdoo.registers.AttachmentRegister.CHALLENGE_ALTAR;
import static org.jahdoo.registers.ItemsRegister.*;
import static org.jahdoo.utils.ModHelpers.Random;

public class LevelGenerator {

    public static void removeCustomLevels(ServerLevel serverLevel) {
        var levelsToRemove = new ArrayList<CustomLevel>();
        for (ServerLevel allLevel : serverLevel.getServer().getAllLevels()) {
            if (allLevel instanceof CustomLevel cLevel) levelsToRemove.add(cLevel);
        }
        for (CustomLevel cLevel : levelsToRemove) removeLevel(cLevel);
    }

    public static void debugLevels(ServerLevel serverLevel) {
        for (ServerLevel allLevel : serverLevel.getServer().getAllLevels()) {
            if (allLevel instanceof CustomLevel cLevel) System.out.println(cLevel.getDescription());
        }
    }

    public static void removeLevel(CustomLevel customLevel) {
        ArcadeDimensions.delete(customLevel.getServer(), customLevel);
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
            return new DimHandler(new Vec3(-22.5, 51, -42.5), TRADING_POST);
        }
    }

    public static DimensionTransition createNewWorld(Player player, ServerLevel serverLevel, ChallengeLevelData altarData, DimHandler handler) {
        var testKey = handler.id + "-" + UUID.randomUUID();
        generateNewLevel(serverLevel, testKey);
        var getLevel = new AtomicReference<ServerLevel>();
        findLevel(testKey, serverLevel).ifPresent(
            level -> {
                generateStructure(player, level, altarData, Math.max(altarData.maxRound/5, 1), handler.id());
                level.setData(CHALLENGE_ALTAR, altarData);
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

    private static void generateStructure(Player player, ServerLevel level, ChallengeLevelData data, int stage, String id){
        var pos = new BlockPos(0, 40, 0);
        var getAllChunks = ChunkPos.rangeClosed(new ChunkPos(pos), 3).toList();
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
        if(isTrial) setTrialDim(level, data);
        if(isTrading) setTradingPost(level);
    }

    private static void setTrialDim(ServerLevel level, ChallengeLevelData data) {
        var pos = new BlockPos(-25, 67, -72);
        level.setBlockAndUpdate(pos, BlocksRegister.CHALLENGE_ALTAR.get().defaultBlockState());
        if(level.getBlockEntity(pos) instanceof ChallengeAltarBlockEntity altar){
            altar.setData(CHALLENGE_ALTAR, data);
            altar.setChanged();
        }
    }

    private static void setTradingPost(ServerLevel level) {
        var chestPositions = new BlockPos(-21, 55, -25);
        var eliteItemPosition = new BlockPos(-16, 54, -15);
        var normalItemPosition = new BlockPos(-14, 53, -28);
        var keyItemPosition = new BlockPos(-32, 53, -28);

        var chestState = BlocksRegister.LOOT_CHEST.get().defaultBlockState().setValue(FACING, Direction.SOUTH);
        for(int i = 0; i <= 4; i += 2) level.setBlockAndUpdate(chestPositions.west(i), chestState);

        var shoppingTableState = BlocksRegister.SHOPPING_TABLE.get().defaultBlockState();

        var eliteState = shoppingTableState.setValue(FACING, Direction.NORTH).setValue(TEXTURE, 2);
        for(int i = 0; i <= 14; i += 7){
            var pos = eliteItemPosition.west(i);
            level.setBlockAndUpdate(pos.above(), Blocks.BARRIER.defaultBlockState());
            level.setBlockAndUpdate(pos, eliteState);
            var blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof ShoppingTableEntity entity){
                var shoppingItem = getEliteShoppingItem(level);
                entity.setItem(shoppingItem.ShoppingItem());
                entity.setCost(shoppingItem.itemCosts());
            }
        }

        var normalState = shoppingTableState.setValue(FACING, Direction.WEST);
        for(int i = 0; i <= 6; i += 3) {
            var isRandomTable = i == 0;
            var pos = normalItemPosition.south(i);
            level.setBlockAndUpdate(pos, normalState.setValue(TEXTURE, isRandomTable ? 3 : 0));
            var blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof ShoppingTableEntity entity){
                switch (i){
                    case 0 -> entity.setCost(ItemCosts.getGoldCost(1));
                    case 3 -> {
                        entity.setItem(new ItemStack(AUGMENT_ITEM));
                        entity.setCost(ItemCosts.getGoldCost(20));
                    }
                    case 6 -> {
                        var randomLootItem = new ItemStack(RUNE);
                        RuneData.RuneHelpers.generateRandomTypAttribute(randomLootItem, null);
                        entity.setItem(randomLootItem);
                        entity.setCost(ItemCosts.getGoldCost(10));
                    }
                }
            }
        }

        var keyState = shoppingTableState.setValue(FACING, Direction.EAST).setValue(TEXTURE, 1);
        for(int i = 6; i >= 0; i -= 3) {
            var pos = keyItemPosition.south(i);
            level.setBlockAndUpdate(pos, keyState);
            var blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof ShoppingTableEntity entity){
                var itemStack = new ItemStack(ItemsRegister.LOOT_KEY);
                var value = Random.nextInt(4);
                itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(value));
                entity.setItem(itemStack);

                var cost = switch (value){
                    case 1 -> ItemCosts.getBronzeCost(40);
                    case 2 -> ItemCosts.getSilverCost(40);
                    case 3 -> ItemCosts.getPlatinumCost(3);
                    default -> ItemCosts.getBronzeCost(20);
                };

                entity.setCost(cost);
            }
        }

        //Entrance and exit portals
        var portalBlock = BlocksRegister.TRAIL_PORTAL.get().defaultBlockState();
        var portalExit = new BlockPos(-4, 51, -17);
        var portalNextLevel = new BlockPos(-40, 51, -17);
        for(int i = 0; i < 3; i++) {
            var pos = portalExit.west(i);
            var pos1 = portalNextLevel.west(i);
            for(int x = 0; x < 5; x++){
                level.setBlockAndUpdate(pos.above(x), portalBlock.setValue(DIMENSION_KEY, KEY_HOME));
                level.setBlockAndUpdate(pos1.above(x), portalBlock.setValue(DIMENSION_KEY, KEY_TRIAL));
            }
        }
    }

    public static void playerSetup(Player player, int stage) {
        //Remove all effects as should only have ones allowed, which should only be ones on trinkets items etc/
        player.removeAllEffects();

        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(40, 50, 30));
            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(ModHelpers.withStyleComponent("Trial Of Strength", ColourStore.PERK_GREEN)));
            serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(ModHelpers.withStyleComponent("Stage " + stage, ColourStore.PERK_GREEN)));
            serverPlayer.playNotifySound(SoundRegister.START_TRIAL.get(), SoundSource.BLOCKS, 1, 1);
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
        var registry = sLevel.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        var holder = registry.getHolderOrThrow(ResourceKey.create(Registries.TEMPLATE_POOL, location));
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
