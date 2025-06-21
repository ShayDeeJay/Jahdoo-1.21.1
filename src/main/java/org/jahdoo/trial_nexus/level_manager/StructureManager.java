package org.jahdoo.trial_nexus.level_manager;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.common.block.lock.LockBlockEntity;
import org.jahdoo.common.block.loot_pot.LootPotBlockEntity;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.loot.LootHelpers;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.ModTags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mojang.datafixers.util.Pair.of;
import static net.minecraft.core.BlockPos.betweenClosed;
import static net.minecraft.core.BlockPos.withinManhattan;
import static net.minecraft.core.Direction.*;
import static net.minecraft.world.level.block.Blocks.NETHERITE_BLOCK;
import static net.minecraft.world.level.block.Blocks.TINTED_GLASS;
import static org.jahdoo.common.block.perk_table.PerkTable.TEXTURE;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.common.registers.BlockReg.LOCK_SUPPORT;
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

    public static final String BOSS_CRUCIBLE = "boss_crucible";
    public static final Component BOSS_COMPONENT = withStyleComponent(stringIdToName(BOSS_CRUCIBLE), NEGATIVE_RED);

    public static final String EASY_EXIT = "exit";
    public static final Component EXIT_ROOM_COMPONENT = withStyleComponent(stringIdToName(EASY_EXIT), MAGNET_RANGE_GREEN);

    public static final String LOOT_CRYPT = "loot_crypt";
    public static final Component LOOT_CRYPT_COMPONENT = withStyleComponent(stringIdToName(LOOT_CRYPT), COOLDOWN_GREEN);

    public static final List<String> THE_HALL = List.of("serene_1", "serene_2", "serene_3", "serene_4");
    public static final List<String>  THE_CHAMBERS = List.of("camp_1", "camp_2", "camp_3", "camp_4");
    public static final List<String>  THE_OASIS = List.of("wastland_1", "wastland_2", "wastland_3", "wastland_4");
    public static final List<String>  THE_BASTION = List.of("hellscape_1", "hellscape_2", "hellscape_3", "hellscape_4");
    public static final String STARTING_ROOM = "starting_room";

    public static final int GLOBAL_Y = 60;
    public static final Vec3 SPAWN_POSITION = new Vec3(33.5, GLOBAL_Y + 2, 27.5);
//    public static final long SEED = /*Random.nextLong()*/ 874095743;
    public static BlockState blocker = TINTED_GLASS.defaultBlockState();

    public static void placeStructure(ServerLevel level, BlockPos pos, StructurePlaceSettings settings, String roomId) {
        var templates = level.getStructureManager().get(Helpers.res(roomId));
        settings.setKnownShape(true);
        templates.ifPresent(template -> template.placeInWorld(level, pos, new BlockPos(-22, 0, -22), settings, level.random, Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS));
    }

    public static List<String> getValidRooms(){
        var newList = new ArrayList<String>();
        newList.addAll(THE_HALL);
        newList.addAll(THE_CHAMBERS);
        newList.addAll(THE_OASIS);
        newList.addAll(THE_BASTION);
        return newList;
    }

    public static Component getBattleRoom(){
        return withStyleComponent(stringIdToName(Helpers.listRandom(getValidRooms())), SYMPATHISER_ORANGE);
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
        placeLocksWithData(level, BlockPos.containing(SPAWN_POSITION.subtract(10,0,0)), true);

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, false);
        //Here we can pass the data from the previous altar to set up the next challenge stack.
    }

    public static List<Component> getRandomRoomId(boolean isStarter, InstanceData data){
        var roomGen = new ArrayList<Component>();
        roomGen.add(getBattleRoom());

        var difficulty = data.getDifficulty();
        var forSanctuary = NOVICE.getSerializedName().equals(difficulty) ? 80 : EXPERT.getSerializedName().equals(difficulty) ? 50 : 20 ;
        var forBoss = NOVICE.getSerializedName().equals(difficulty) ? 10 : EXPERT.getSerializedName().equals(difficulty) ? 20 : 30 ;

        if(!isStarter){
            if (percentageChance(forSanctuary)) roomGen.add(SANCTUARY_COMPONENT);
            if (percentageChance(50)) roomGen.add(BAZAAR_COMPONENT);

            if(roomGen.size() == 3) return roomGen;
            if (percentageChance(10)) roomGen.add(LOOT_CRYPT_COMPONENT);

            if(roomGen.size() == 3) return roomGen;
            if(percentageChance(20)) roomGen.add(EXIT_ROOM_COMPONENT);

            if(roomGen.size() == 3) return roomGen;
            if (percentageChance(forBoss)) roomGen.add(BOSS_COMPONENT);
        }

        while (roomGen.size() < 4) roomGen.add(getBattleRoom());

        Collections.shuffle(roomGen);
        return roomGen;
    }

    public static void placeLocksWithData(ServerLevel level, BlockPos pos, boolean isStarter) {
        var range = roomBoundingFromCenter(pos);
        var counter = 0;
        var getRooms = getRandomRoomId(isStarter, level.getData(INSTANCE_DATA));

        var pos1 = new BlockPos(23, 61, 27);
        if(level.getBlockState(pos1).is(Blocks.LIME_CONCRETE)){
            BlockSetupManager.setPerkTable(level, pos1, 2);
        }

        for (var blockPos : range) {

            BlockSetupManager.generateExit(level, blockPos, EAST);
            setLocks(level, blockPos, false);
            if(level.getBlockState(blockPos).equals(blocker)){
                level.destroyBlock(blockPos, false);
            }

            var getLock = level.getBlockEntity(blockPos);

            if(getLock instanceof LockBlockEntity lock) {
                lock.isStarting = isStarter;
                if(lock.isStartingRoom()){
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
            }

            if(level.getBlockState(blockPos).is(NETHERITE_BLOCK)){
                level.setBlockAndUpdate(blockPos, LOCK_SUPPORT.get().defaultBlockState());
            }
        }
    }

    public static final List<Pair<JahdooRarity, Integer>> potRarityGetter = List.of(of(COMMON, 1), of(RARE, 1000), of(EPIC, 5800),  of(LEGENDARY, 6000));
    public static int FLAGS = Block.UPDATE_KNOWN_SHAPE | Block.UPDATE_CLIENTS;
    public static List<Direction> NO_Y = List.of(NORTH, SOUTH, EAST, WEST);

    public static void placeNewSide(Level level, Direction direction, BlockPos pos, String roomId) {
        if (level instanceof ServerLevel serverLevel) {

            var settings = new StructurePlaceSettings();
            var newPos = new BlockPos(0, 0, 0);

            final var globalY = roomId.equals(LOOT_CRYPT) ? GLOBAL_Y - 6 : GLOBAL_Y;
            var spaceBy = 25;
            switch (direction) {
                case SOUTH -> newPos = new BlockPos(pos.getX() - spaceBy, globalY, pos.getZ());
                case NORTH -> {
                    settings.setRotation(Rotation.CLOCKWISE_180);
                    newPos = new BlockPos(pos.getX() + spaceBy, globalY, pos.getZ());
                }
                case EAST -> {
                    settings.setRotation(Rotation.COUNTERCLOCKWISE_90);
                    newPos = new BlockPos(pos.getX(), globalY, pos.getZ() + spaceBy);
                }
                case WEST -> {
                    settings.setRotation(Rotation.CLOCKWISE_90);
                    newPos = new BlockPos(pos.getX(), globalY, pos.getZ() - spaceBy);
                }
            }

            placeStructure(serverLevel, newPos, settings, roomId);

            var relative = newPos.relative(direction, 25).above(19);
            var findBlock = switch (direction){
                case NORTH -> withinManhattan(relative.west(25), 25, 19, 25);
                case SOUTH -> withinManhattan(relative.east(25), 25, 19, 25);
                case EAST -> withinManhattan(relative.north(25), 25, 19, 25);
                default -> withinManhattan(relative.south(25), 25, 19, 25);
            };

            setBlockGenerator(serverLevel, findBlock, direction, roomId);
            var alreadyPlaced = false;

            for (var blockPos : findBlock) {
                var placerState = level.getBlockState(blockPos);
                if(getValidRooms().contains(roomId)){
                    if (placerState.is(Blocks.DIAMOND_BLOCK)) {
                        level.setBlockAndUpdate(blockPos, BlockReg.CHALLENGE_ALTAR.get().defaultBlockState());
                        if (level.getBlockEntity(blockPos) instanceof AltarBlockEntity e) {
                            e.roomId = roomId;
                            e.direction = direction;
                        }
                    }

                    if (placerState.is(Blocks.MAGENTA_STAINED_GLASS)) {
                        var spawnChance = percentageChance(20) && !alreadyPlaced;
                        var station = BlockReg.POWER_UP_STATION.get().defaultBlockState();
                        var air = Blocks.AIR.defaultBlockState();
                        if (spawnChance) alreadyPlaced = true;
                        level.setBlockAndUpdate(blockPos, spawnChance ? station : air);
                    }

                    if (placerState.is(Blocks.ORANGE_STAINED_GLASS)) {
                        if(level instanceof ServerLevel sLevel){
                            var bPos = blockPos;
                            var spawnChance = percentageChance(20);
                            placePot(level, sLevel, blockPos, percentageChance(20));

                            if(percentageChance(20)){
                                for (int i = 0; i < 0; i++) {
                                    var poss = new ArrayList<BlockPos>();
                                    for (var direction1 : NO_Y) {
                                        var relativeA = bPos.relative(direction1);
                                        if (level.getBlockState(relativeA).is(ModTags.Block.CAN_REPLACE_BLOCK) && !level.getBlockState(relativeA.below()).is(BlockReg.LOOT_POT)) {
                                            placePot(level, sLevel, relativeA, spawnChance);
                                            poss.add(relativeA);
                                        }
                                    }
                                    if (!poss.isEmpty()) bPos = Helpers.listRandom(poss);
                                }
                            }
                        }
                    }

                    if (placerState.is(Blocks.PINK_STAINED_GLASS)) {
                        if(level instanceof ServerLevel sLevel){
                            var state = Helpers.listRandom(List.of(BlockReg.ROSE_QUARTZ_ORE.get(), BlockReg.ENCHANTED_DIAMOND_ORE.get())).defaultBlockState();
                            var spawnChance = percentageChance(10);
                            placeOre(level, blockPos, sLevel, spawnChance, state);
                            var bPos = blockPos;

                            if(percentageChance(20)){
                                for (int i = 0; i < 0; i++) {
                                    var poss = new ArrayList<BlockPos>();
                                    for (var direction1 : stream().toList()) {
                                        var relativeA = bPos.relative(direction1);
                                        if (level.getBlockState(relativeA).is(ModTags.Block.CAN_REPLACE_BLOCK)) {
                                            placeOre(level, relativeA, sLevel, spawnChance, state);
                                            poss.add(relativeA);
                                        }
                                    }
                                    if (!poss.isEmpty()) bPos = Helpers.listRandom(poss);
                                }
                            }
                        }
                    }
                }

                if(placerState.is(Blocks.OBSERVER)) setLocks(serverLevel, blockPos, true);

                if(placerState.is(NETHERITE_BLOCK)) level.setBlockAndUpdate(blockPos, LOCK_SUPPORT.get().defaultBlockState());
            }
        }
    }

    private static void placePot(Level level, ServerLevel sLevel, BlockPos relativeA, boolean spawnChance) {
        var value1 = JahdooRarity.getRarity(potRarityGetter).getId();
        var state1 = BlockReg.LOOT_POT.get().defaultBlockState().setValue(TEXTURE, value1);
        var getLoot1 = LootHelpers.potLoot(sLevel, relativeA.getCenter(), NOVICE.getSerializedName(), value1);
        placeOre(level, relativeA, sLevel, spawnChance, state1);
        if(level.getBlockEntity(relativeA) instanceof LootPotBlockEntity potBlockEntity){
            potBlockEntity.setTheItem(getLoot1);
        }
    }

    private static void placeOre(Level level, BlockPos blockPos, ServerLevel sLevel, boolean spawnChance, BlockState state) {
        if(spawnChance && sLevel.getBlockState(blockPos.below()).isSolid()){
            level.setBlock(blockPos, state, FLAGS);
        } else {
            var air = Blocks.AIR.defaultBlockState();
            if(level.getBlockState(blockPos.above()).is(state.getBlock())){
                level.setBlock(blockPos.above(), air, FLAGS);
                level.setBlock(blockPos, state, FLAGS);
            } else {
                level.setBlock(blockPos, air, FLAGS);
            }
        }
    }

}
