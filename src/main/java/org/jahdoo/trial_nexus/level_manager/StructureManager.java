package org.jahdoo.trial_nexus.level_manager;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.common.block.lock.LockBlock;
import org.jahdoo.common.block.lock.LockBlockEntity;
import org.jahdoo.common.block.loot_pot.LootPotBlockEntity;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.loot.LootHelpers;
import org.jahdoo.trial_nexus.loot.RewardLootTables;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.ModTags;

import java.util.*;

import static com.mojang.datafixers.util.Pair.of;
import static net.minecraft.core.BlockPos.betweenClosed;
import static net.minecraft.core.BlockPos.withinManhattan;
import static net.minecraft.core.Direction.*;
import static net.minecraft.world.level.block.Blocks.NETHERITE_BLOCK;
import static net.minecraft.world.level.block.Blocks.TINTED_GLASS;
import static org.jahdoo.common.block.perk_table.PerkTable.TEXTURE;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.BlockReg.*;
import static org.jahdoo.trial_nexus.level_manager.BlockSetupManager.setBlockGenerator;
import static org.jahdoo.trial_nexus.level_manager.BlockSetupManager.setLocks;
import static org.jahdoo.trial_nexus.level_manager.InstanceDifficulty.*;
import static org.jahdoo.trial_nexus.rarity.JahdooRarity.*;
import static org.jahdoo.trial_nexus.utils.ColourStore.*;
import static org.jahdoo.trial_nexus.utils.Helpers.*;
import static org.jahdoo.trial_nexus.utils.Maths.percentageChance;
import static org.jahdoo.trial_nexus.utils.PositionFinders.innerRadiusRandom;

public class StructureManager {

    public static final String BAZAAR = "bazaar";
    public static final Component BAZAAR_COMPONENT = withStyleComponent(stringIdToName(BAZAAR), AETHER_BLUE);

    public static final String SANCTUARY = "sanctuary";
    public static final Component SANCTUARY_COMPONENT = withStyleComponent(stringIdToName(SANCTUARY), COSMIC_PURPLE);

    public static final String LOOT_CRYPT = "loot_crypt";
    public static final Component LOOT_CRYPT_COMPONENT = withStyleComponent(stringIdToName(LOOT_CRYPT), COOLDOWN_GREEN);

    public static final String BOSS_CRUCIBLE = "boss_crucible";
    public static final Component BOSS_COMPONENT = withStyleComponent(stringIdToName(BOSS_CRUCIBLE), NEGATIVE_RED);

    public static final String EASY_EXIT = "exit";
    public static final Component EXIT_ROOM_COMPONENT = withStyleComponent(stringIdToName(EASY_EXIT), MAGNET_RANGE_GREEN);

    public static final List<String> EASY_ROOMS = List.of("oakvale", "logyard", "rotgrove", "pasture");
    public static final List<String> MEDIUM_ROOMS = List.of("sundown", "deadwood", "oasis", "dustcamp");
    public static final List<String> HARD_ROOMS = List.of("blackstone", "frosthold", "mines", "ruins");
    public static final List<String> EXTREME_ROOMS = List.of("hellgate", "pyrewalk", "ashhaven", "sporefire");
    public static final Map<Integer, Component> getRestRooms = Map.of(0, BAZAAR_COMPONENT, 1, LOOT_CRYPT_COMPONENT, 2, SANCTUARY_COMPONENT, 3, EXIT_ROOM_COMPONENT);

    public static final String STARTING_ROOM = "starting_room";
    public static final String BRIDGE = "bridge";
    public static final int GLOBAL_Y = 60;
    public static final Vec3 SPAWN_POSITION = new Vec3(33.5, GLOBAL_Y + 2, 27.5);
    public static final BlockState BLOCKER_BLOCK = TINTED_GLASS.defaultBlockState();

    public static final List<String> REST_ROOMS = List.of(BAZAAR, SANCTUARY, LOOT_CRYPT, EASY_EXIT);
    public static final List<String> VALID_ROOMS;

    public static void placeStructure(ServerLevel level, BlockPos pos, StructurePlaceSettings settings, String roomId) {
        var templates = level.getStructureManager().get(Helpers.res(roomId));
        var flag = Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS;
        var pos1 = new BlockPos(-22, 0, -22);

        settings.setKnownShape(true).addProcessor(BlockIgnoreProcessor.AIR);
        templates.ifPresent(template -> template.placeInWorld(level, pos, pos1, settings, level.random, flag));
    }

    public static Component getBattleRoom(){
        var randomValidRooms = listRandom(VALID_ROOMS);
        var namedRoom = stringIdToName(randomValidRooms);

        return withStyleComponent(namedRoom, SYMPATHISER_ORANGE);
    }

    public static Iterable<BlockPos> roomBoundingFromCenter(BlockPos pos) {
        return betweenClosed(
            pos.getX() - 25, pos.getY() - 4, pos.getZ() - 25,
            pos.getX() + 25, pos.getY() + 10, pos.getZ() + 25
        );
    }

    public static void generateStartingRoom(ServerLevel level){
        var pos = new BlockPos(0, GLOBAL_Y, 0);
        var getAllChunks = ChunkPos.rangeClosed(new ChunkPos(pos), 1).toList();
        var settings = new StructurePlaceSettings();

        settings.setRotationPivot(new BlockPos(pos.getX() + 23, pos.getY(), pos.getZ() + 25));
        settings.setRotation(Rotation.CLOCKWISE_90);

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, true);

        placeStructure(level, pos, settings, STARTING_ROOM);
        placeLocksWithData(level, BlockPos.containing(SPAWN_POSITION.subtract(10,0,0)), true, false);

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, false);
        //Here we can pass the data from the previous altar to set up the next challenge stack.
    }

    public static void generateCooldownRoom(ServerLevel level){
        var pos = new BlockPos(0, GLOBAL_Y + 30, 0);
        var getAllChunks = ChunkPos.rangeClosed(new ChunkPos(pos), 1).toList();
        var settings = new StructurePlaceSettings();

        settings.setRotationPivot(new BlockPos(pos.getX() + 23, pos.getY(), pos.getZ() + 25));
        settings.setRotation(Rotation.CLOCKWISE_90);

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, true);

        for (var allEntity : level.getAllEntities()) if(allEntity instanceof Villager villager) villager.kill();

        placeStructure(level, pos, settings, BRIDGE);
        placeLocksWithData(level, new BlockPos(23, 105, 27), false, true);

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, false);
        //Here we can pass the data from the previous altar to set up the next challenge stack.
    }

    public static Map<Integer, Component> getRandomRoomId(InstanceData data, boolean isRestRoom){
        var roomGen = new HashMap<Integer, Component>();

        if(isRestRoom) return getRestRooms;

        var difficulty = data.getDifficulty();
        var isNovice = NOVICE.getSerializedName().equals(difficulty);
        var isExpert = EXPERT.getSerializedName().equals(difficulty);
        var forBoss = isNovice ? 10 : isExpert ? 20 : 30 ;
        roomGen.put(0, getBattleRoom());

        if(!data.getDifficulty().isEmpty()){
            JahdooMod.LOGGER.log(org.apache.logging.log4j.Level.INFO, "Error, boss room generated as starting room");
            if(percentageChance(forBoss)) roomGen.put(roomGen.size(), BOSS_COMPONENT);
        }

        while (roomGen.size() < 4) roomGen.put(roomGen.size(), getBattleRoom());
        return roomGen;
    }

    public static void placeLocksWithData(ServerLevel level, BlockPos pos, boolean isStarter, boolean isRestRoom) {
        var range = roomBoundingFromCenter(pos);
        var counter = 0;
        var getRooms = getRandomRoomId(level.getData(INSTANCE_DATA), isRestRoom);

        var pos1 = new BlockPos(23, 61, 27);
        if(level.getBlockState(pos1).is(Blocks.LIME_CONCRETE)){
            BlockSetupManager.setPerkTable(level, pos1, 2);
        }

        for (var blockPos : range) {

            BlockSetupManager.generateExit(level, blockPos, EAST);
            setLocks(level, blockPos, false);

            if(level.getBlockState(blockPos).equals(BLOCKER_BLOCK)){
                level.destroyBlock(blockPos, false);
            }

            var getLock = level.getBlockEntity(blockPos);

            if(getLock instanceof LockBlockEntity lock) {
                lock.isStarting = isStarter;

                if(lock.isStartingRoom()) {
                    lock.getDifficulty = getDifficulties().reversed().get(counter).getSerializedName();
                }

                lock.setRoomData(getRooms.get(counter));
                counter++;
                var getPositions = innerRadiusRandom(blockPos.getCenter().subtract(0, 1, 0), 2.3, 350);

                for (var vec3 : getPositions) {
                    var colour = lock.roomId.getStyle().getColor().getValue();
                    var particle = ParticleHandlers.getNonBakedParticles(colour, colour, 16, Random.nextInt(2, 4));
                    sendParticles(level, particle, vec3, 0, 0, 0.5, 0, Random.nextDouble(0.3, 1.2));
                }

                if(isRestRoom) {
                    LockBlock.getItemInteractionResult(getLock.getBlockState(), blockPos, lock, level);
                }
            }

            if(level.getBlockState(blockPos).is(NETHERITE_BLOCK)){
                level.setBlockAndUpdate(blockPos, LOCK_SUPPORT.get().defaultBlockState());
            }
        }
    }

    public static final List<Pair<JahdooRarity, Integer>> potRarityGetter = List.of(of(COMMON, 1), of(RARE, 1000), of(EPIC, 5800),  of(LEGENDARY, 6000));
    public static int FLAGS = Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS;
    public static List<Direction> NO_Y = List.of(NORTH, SOUTH, EAST, WEST);

    public static void placeNewSide(Level level, Direction direction, BlockPos pos, String roomId, boolean usedKey) {
        if (level instanceof ServerLevel serverLevel) {

            var settings = new StructurePlaceSettings();
            var newPos = new BlockPos(0, 0, 0);

            var globalY1 = GLOBAL_Y + (REST_ROOMS.contains(roomId) && !usedKey ? 44 : 0);
            final var globalY = roomId.equals(LOOT_CRYPT) ? globalY1 - 6 : globalY1 + (roomId.equals(BRIDGE) ? 104 : 0);
            var distance = 25;
            switch (direction) {
                case SOUTH -> newPos = new BlockPos(pos.getX() - distance, globalY, pos.getZ());
                case NORTH -> {
                    settings.setRotation(Rotation.CLOCKWISE_180);
                    newPos = new BlockPos(pos.getX() + distance, globalY, pos.getZ());
                }
                case EAST -> {
                    settings.setRotation(Rotation.COUNTERCLOCKWISE_90);
                    newPos = new BlockPos(pos.getX(), globalY, pos.getZ() + distance);
                }
                case WEST -> {
                    settings.setRotation(Rotation.CLOCKWISE_90);
                    newPos = new BlockPos(pos.getX(), globalY, pos.getZ() - distance);
                }
            }

            placeStructure(serverLevel, newPos, settings, roomId);

            var relative = newPos.relative(direction, distance).above(19);
            var findBlock = switch (direction){
                case NORTH -> withinManhattan(relative.west(distance), distance, 19, distance);
                case SOUTH -> withinManhattan(relative.east(distance), distance, 19, distance);
                case EAST -> withinManhattan(relative.north(distance), distance, 19, distance);
                default -> withinManhattan(relative.south(distance), distance, 19, distance);
            };

            setBlockGenerator(serverLevel, findBlock, direction, roomId);
            var alreadyPlaced = false;
            var instanceData = serverLevel.getData(INSTANCE_DATA);
            for (var blockPos : findBlock) {
                var placerState = level.getBlockState(blockPos);
                if(VALID_ROOMS.contains(roomId)){
                    placeAltar(level, direction, roomId, blockPos, placerState);
                    alreadyPlaced = placePowerUpStation(level, blockPos, placerState, alreadyPlaced);
                    placeLootPots(level, blockPos, placerState, instanceData);
                    placeOres(level, blockPos, placerState, instanceData);
                }

                if(placerState.is(Blocks.OBSERVER)) setLocks(serverLevel, blockPos, true);
                if(placerState.is(NETHERITE_BLOCK)) level.setBlockAndUpdate(blockPos, LOCK_SUPPORT.get().defaultBlockState());
            }
        }
    }


    private static void placeOres(Level level, BlockPos blockPos, BlockState placerState, InstanceData instanceData) {
        if (placerState.is(Blocks.PINK_STAINED_GLASS)) {
            if(level instanceof ServerLevel sLevel){
                var aStates = RewardLootTables.oreDistribution(sLevel, blockPos.getCenter());
                var aItems = Helpers.listRandom(aStates);
                var aBase = Block.byItem(aItems.getItem()).defaultBlockState();
                placer(blockPos, sLevel, percentageChance(10), aBase);

                var bPos = blockPos;
                var multiplier = instanceData.getOreMultiplier();
                for (int i = 0; i < multiplier; i++) {
                    if(percentageChance(20)){
                        var bStates = RewardLootTables.oreDistribution(sLevel, blockPos.getCenter());
                        var bItems = Helpers.listRandom(bStates);
                        var bBase = Block.byItem(bItems.getItem()).defaultBlockState();
                        var poss = new ArrayList<BlockPos>();
                        for (var direction1 : stream().toList()) {
                            var relativeA = bPos.relative(direction1);
                            if (level.getBlockState(relativeA).is(ModTags.Block.CAN_REPLACE_BLOCK)) {
                                placer(relativeA, sLevel, true, bBase);
                                poss.add(relativeA);
                            }
                        }
                        if (!poss.isEmpty()) bPos = Helpers.listRandom(poss);
                    }
                }
            }
        }
    }

    private static void placeLootPots(Level level, BlockPos blockPos, BlockState placerState, InstanceData instanceData) {
        if (placerState.is(Blocks.ORANGE_STAINED_GLASS)) {
            if(level instanceof ServerLevel sLevel){
                var bPos = blockPos;
                var spawnChance = percentageChance(20);
                placePot(level, sLevel, blockPos, spawnChance);

                var multiplier = instanceData.getLootPotMultiplier();
                for (int i = 0; i < multiplier; i++) {
                    if(percentageChance(20)){
                        var poss = new ArrayList<BlockPos>();
                        for (var direction1 : NO_Y) {
                            var relativeA = bPos.relative(direction1);
                            if (level.getBlockState(relativeA).is(ModTags.Block.CAN_REPLACE_BLOCK) && !level.getBlockState(relativeA.below()).is(LOOT_POT)) {
                                placePot(level, sLevel, relativeA, spawnChance);
                                poss.add(relativeA);
                            }
                        }
                        if (!poss.isEmpty()) bPos = Helpers.listRandom(poss);
                    }
                }
            }
        }
    }

    private static boolean placePowerUpStation(Level level, BlockPos blockPos, BlockState placerState, boolean alreadyPlaced) {
        if (placerState.is(Blocks.MAGENTA_STAINED_GLASS)) {
            var spawnChance = percentageChance(20) && !alreadyPlaced;
            var station = POWER_UP_STATION.get().defaultBlockState();
            var air = Blocks.AIR.defaultBlockState();
            if (spawnChance) alreadyPlaced = true;
            level.setBlockAndUpdate(blockPos, spawnChance ? station : air);
        }
        return alreadyPlaced;
    }

    private static void placeAltar(Level level, Direction direction, String roomId, BlockPos blockPos, BlockState placerState) {
        if (placerState.is(Blocks.DIAMOND_BLOCK)) {
            level.setBlockAndUpdate(blockPos, CHALLENGE_ALTAR.get().defaultBlockState());
            if (level.getBlockEntity(blockPos) instanceof AltarBlockEntity e) {
                e.roomId = roomId;
                e.direction = direction;
            }
        }
    }

    private static void placePot(Level level, ServerLevel sLevel, BlockPos relativeA, boolean spawnChance) {
        var value1 = JahdooRarity.getRarity(potRarityGetter).getId();
        var state1 = LOOT_POT.get().defaultBlockState().setValue(TEXTURE, value1);
        var getLoot1 = LootHelpers.potLoot(sLevel, relativeA.getCenter(), NOVICE.getSerializedName(), value1);
        placer(relativeA, sLevel, spawnChance, state1);
        if(level.getBlockEntity(relativeA) instanceof LootPotBlockEntity potBlockEntity){
            potBlockEntity.setTheItem(getLoot1);
        }
    }

    public static void placer(BlockPos blockPos, ServerLevel sLevel, boolean spawnChance, BlockState state) {
        var blockState = sLevel.getBlockState(blockPos.below());
        var air = Blocks.AIR.defaultBlockState();
        if(spawnChance){
            if (blockState.isSolidRender(EmptyBlockGetter.INSTANCE, blockPos)) {
                sLevel.setBlock(blockPos, state, FLAGS);
            } else if (!sLevel.getBlockState(blockPos).isAir()) {
                sLevel.setBlock(blockPos, air, FLAGS);
            }
        } else if (!sLevel.getBlockState(blockPos).isAir()) {
            sLevel.setBlock(blockPos, air, FLAGS);
        }
    }

    static {
        List<String> list = new ArrayList<>();
        list.addAll(EASY_ROOMS);
        list.addAll(HARD_ROOMS);
        list.addAll(MEDIUM_ROOMS);
        list.addAll(EXTREME_ROOMS);
        list.add(BOSS_CRUCIBLE);
        VALID_ROOMS = Collections.unmodifiableList(list);
    }

}
