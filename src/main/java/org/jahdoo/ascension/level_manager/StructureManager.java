package org.jahdoo.ascension.level_manager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.attachments.InstanceData;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.Maths;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.common.block.lock.LockBlockEntity;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockReg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.minecraft.core.BlockPos.betweenClosed;
import static net.minecraft.core.BlockPos.withinManhattan;
import static org.jahdoo.ascension.level_manager.BlockSetupManager.setBlockGenerator;
import static org.jahdoo.ascension.level_manager.BlockSetupManager.setLocks;
import static org.jahdoo.ascension.level_manager.InstanceDifficulty.*;
import static org.jahdoo.ascension.level_manager.InstanceDifficulty.EASY;
import static org.jahdoo.ascension.level_manager.InstanceDifficulty.MEDIUM;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.PositionFinders.innerRadiusRandom;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public class StructureManager {

    public static final String BAZAAR = "bazaar";
    public static final Component BAZAAR_COMPONENT = withStyleComponent(stringIdToName(BAZAAR), AETHER_BLUE);

    public static final String SANCTUARY = "sanctuary";
    public static final Component SANCTUARY_COMPONENT = withStyleComponent(stringIdToName(SANCTUARY), COSMIC_PURPLE);

    public static final String BOSS_CRUCIBLE = "boss_crucible";
    public static final Component BOSS_COMPONENT = withStyleComponent(stringIdToName(BOSS_CRUCIBLE), NEGATIVE_RED);

    public static final String EASY_EXIT = "emergency_exit";
    public static final Component EXIT_ROOM_COMPONENT = withStyleComponent(stringIdToName(EASY_EXIT), MAGNET_RANGE_GREEN);

    public static final String THE_HALL = "serene";
    public static final String THE_CHAMBERS = "camp";
    public static final String THE_OASIS = "wasteland";
    public static final String THE_BASTION = "hellscape";
    public static final String STARTING_ROOM = "starting_room";

    public static final int GLOBAL_Y = 60;
    public static final Vec3 SPAWN_POSITION = new Vec3(33.5, GLOBAL_Y + 2, 27.5);
    public static final long SEED = /*Random.nextLong()*/ 874095743;

    public static void placeStructure(ServerLevel level, BlockPos pos, StructurePlaceSettings settings, String roomId) {
        var templates = level.getStructureManager().get(Helpers.res(roomId));
        templates.ifPresent(template -> template.placeInWorld(level, pos, new BlockPos(-22, 0, -22), settings, level.random, 2));
    }

    public static String getValidRooms(){
        return Helpers.listRandom(List.of(THE_HALL, THE_CHAMBERS, THE_OASIS, THE_BASTION));
    }

    public static Component getBattleRoom(){
        return withStyleComponent(stringIdToName(getValidRooms()), SYMPATHISER_ORANGE);
    }

    public static Iterable<BlockPos> roomBoundingFromCenter(BlockPos pos) {
        return betweenClosed(
            pos.getX() - 25, pos.getY(), pos.getZ() - 25,
            pos.getX() + 25, pos.getY() + 10, pos.getZ() + 25
        );
    }

    public static void generateStructure(ServerLevel level){
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
        var forSanctuary = EASY.getSerializedName().equals(difficulty) ? 80 : MEDIUM.getSerializedName().equals(difficulty) ? 50 : 20 ;
        var forBoss = EASY.getSerializedName().equals(difficulty) ? 10 : MEDIUM.getSerializedName().equals(difficulty) ? 40 : 70 ;

        if(!isStarter){
            if (Maths.percentageChance(forSanctuary)) roomGen.add(SANCTUARY_COMPONENT);

            if (Maths.percentageChance(50)) roomGen.add(BAZAAR_COMPONENT);

            if(roomGen.size() == 3) return roomGen;

            if(Maths.percentageChance(20)) roomGen.add(EXIT_ROOM_COMPONENT);

            if(roomGen.size() == 3) return roomGen;

            if (Maths.percentageChance(forBoss)) roomGen.add(BOSS_COMPONENT);
        }

        while (roomGen.size() < 4) roomGen.add(getBattleRoom());

        Collections.shuffle(roomGen);
        return roomGen;
    }

    public static void placeLocksWithData(ServerLevel level, BlockPos pos, boolean isStarter) {
        var range = roomBoundingFromCenter(pos);
        var counter = 0;
        var getRooms = getRandomRoomId(isStarter, level.getData(INSTANCE_DATA));

        for (var blockPos : range) {
            BlockSetupManager.generateExit(level, blockPos, Direction.EAST);
            setLocks(level, blockPos, false);
            if(level.getBlockState(blockPos).is(Blocks.TINTED_GLASS)){
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
        }
    }

    public static void placeNewSide(Level level, Direction direction, BlockPos pos, String roomId) {
        if (level instanceof ServerLevel serverLevel) {

            var settings = new StructurePlaceSettings();
            var newPos = new BlockPos(0, 0, 0);

            switch (direction) {
                case Direction.SOUTH -> newPos = new BlockPos(pos.getX() - 25, GLOBAL_Y, pos.getZ());
                case Direction.NORTH -> {
                    settings.setRotation(Rotation.CLOCKWISE_180);
                    newPos = new BlockPos(pos.getX() + 25, GLOBAL_Y, pos.getZ());
                }
                case Direction.EAST -> {
                    settings.setRotation(Rotation.COUNTERCLOCKWISE_90);
                    newPos = new BlockPos(pos.getX(), GLOBAL_Y, pos.getZ() + 25);
                }
                case Direction.WEST -> {
                    settings.setRotation(Rotation.CLOCKWISE_90);
                    newPos = new BlockPos(pos.getX(), GLOBAL_Y, pos.getZ() - 25);
                }
            }

            placeStructure(serverLevel, newPos, settings, roomId);

            var relative = newPos.relative(direction, 26).above(19);
            var findBlock = switch (direction){
                case Direction.NORTH -> withinManhattan(relative.west(25), 25, 19, 25);
                case Direction.SOUTH -> withinManhattan(relative.east(25), 25, 19, 25);
                case Direction.EAST -> withinManhattan(relative.north(25), 25, 19, 25);
                default -> withinManhattan(relative.south(25), 25, 19, 25);
            };

            setBlockGenerator(serverLevel, findBlock, direction, roomId);

            for (var blockPos : findBlock) {
                if (level.getBlockState(blockPos).is(Blocks.DIAMOND_BLOCK)) {
                    level.setBlockAndUpdate(blockPos, BlockReg.CHALLENGE_ALTAR.get().defaultBlockState());
                    if(level.getBlockEntity(blockPos) instanceof AltarBlockEntity e){
                        e.roomId = roomId;
                        e.direction = direction;
                    }
                }
                if(level.getBlockState(blockPos).is(Blocks.OBSERVER)){
                    setLocks(serverLevel, blockPos, true);
                }
            }
        }
    }

}
