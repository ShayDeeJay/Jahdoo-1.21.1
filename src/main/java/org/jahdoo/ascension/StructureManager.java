package org.jahdoo.ascension;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.common.block.lock.LockBlockEntity;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockReg;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.core.BlockPos.betweenClosed;
import static net.minecraft.core.BlockPos.withinManhattan;
import static org.jahdoo.ascension.BlockSetupManager.setBlockGenerator;
import static org.jahdoo.ascension.BlockSetupManager.setLocks;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.PositionFinders.innerRadiusRandom;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;

public class StructureManager {

    public static final String TRADING_POST = "Trading Post";
    public static final String POWER_UP = "Power Up";
    public static final String ROOM = "Room";
    public static final String ROOM_1 = "Room 1";
    public static final String ROOM_2 = "Room 2";
    public static final String STARTING_ROOM = "Starting Room";


    public static void placeStructure(ServerLevel level, BlockPos pos, StructurePlaceSettings settings, String roomId) {
        var templates = level.getStructureManager().get(Helpers.res(roomId));
        templates.ifPresent(template -> template.placeInWorld(level, pos, new BlockPos(-22, 0, -22), settings, level.random, 2));
    }

    public static Iterable<BlockPos> roomBoundingFromCenter(BlockPos pos) {
        return betweenClosed(
            pos.getX() - 25, pos.getY(), pos.getZ() - 25,
            pos.getX() + 25, pos.getY() + 10, pos.getZ() + 25
        );
    }

    public static Component getRandomRoomId(){
        var roomGen = new ArrayList<Component>();
        roomGen.add(withStyleComponent(listRandom(List.of(ROOM, ROOM_1, ROOM_2)), SYMPATHISER_ORANGE));
        roomGen.add(withStyleComponent(TRADING_POST, AETHER_BLUE));
//        roomGen.add(withStyleComponent(POWER_UP, COSMIC_PURPLE));

        if (Random.nextInt(3) == 0){
          roomGen.add(withStyleComponent(POWER_UP, COSMIC_PURPLE));
        }

        if(Random.nextInt(5) == 0){
//          roomGen.add(withStyleComponent(TRADING_POST, AETHER_BLUE));
        }

        return listRandom(roomGen);
    }

    public static void placeLocksWithData(ServerLevel level, BlockPos pos) {
        var range = roomBoundingFromCenter(pos);

        for (var blockPos : range) {
            BlockSetupManager.generateExit(level, blockPos);
            setLocks(level, blockPos);
            if(level.getBlockState(blockPos).is(Blocks.TINTED_GLASS)){
                level.destroyBlock(blockPos, false);
            }
            var getLock = level.getBlockEntity(blockPos);
            if(getLock instanceof LockBlockEntity lock) {
                lock.setRoomData();
                var getPositions = innerRadiusRandom(blockPos.getCenter().subtract(0,   1, 0), 2.3, 350);

                for (var vec3 : getPositions) {
                    var colour = lock.roomId.getStyle().getColor().getValue();
                    var particle = ParticleHandlers.getNonBakedParticles(colour, colour, 16, Random.nextInt(2, 4));
                    sendParticles(level, particle, vec3, 0, 0, 0.5, 0, Random.nextDouble(0.3, 1.2));
                }
            }
        }
    }

    static void generateStructure(ServerLevel level){
        var pos = new BlockPos(0, 40, 0);
        var getAllChunks = ChunkPos.rangeClosed(new ChunkPos(pos), 1).toList();
        var settings = new StructurePlaceSettings();

        settings.setRotationPivot(new BlockPos(pos.getX() + 23, pos.getY(), pos.getZ() + 25));
        settings.setRotation(Rotation.CLOCKWISE_90);

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, true);

        placeStructure(level, pos, settings, nameToId(STARTING_ROOM));
        placeLocksWithData(level, BlockPos.containing(DimHandler.trial().spawn()));

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, false);
        //Here we can pass the data from the previous altar to set up the next challenge stack.
    }

    public static void placeNewSide(Level level, Direction direction, BlockPos pos, String roomId) {
        if (level instanceof ServerLevel serverLevel) {

            var settings = new StructurePlaceSettings();
            var newPos = new BlockPos(0, 0, 0);

            switch (direction) {
                case Direction.SOUTH -> newPos = new BlockPos(pos.getX() - 25, 40, pos.getZ());
                case Direction.NORTH -> {
                    settings.setRotation(Rotation.CLOCKWISE_180);
                    newPos = new BlockPos(pos.getX() + 25, 40, pos.getZ());
                }
                case Direction.EAST -> {
                    settings.setRotation(Rotation.COUNTERCLOCKWISE_90);
                    newPos = new BlockPos(pos.getX(), 40, pos.getZ() + 25);
                }
                case Direction.WEST -> {
                    settings.setRotation(Rotation.CLOCKWISE_90);
                    newPos = new BlockPos(pos.getX(), 40, pos.getZ() - 25);
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
                    }
                }
            }
        }
    }
}
