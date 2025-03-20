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
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.altar.AltarBlockEntity;
import org.jahdoo.common.block.lock.LockBlockEntity;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.BlockReg;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.core.BlockPos.betweenClosed;
import static net.minecraft.core.BlockPos.withinManhattan;
import static org.jahdoo.ascension.level_manager.BlockSetupManager.setBlockGenerator;
import static org.jahdoo.ascension.level_manager.BlockSetupManager.setLocks;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.PositionFinders.innerRadiusRandom;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;

public class StructureManager {

    public static final String BAZAAR = "bazaar";
    public static final String SANCTUARY = "sanctuary";
    public static final String THE_HALL = "the_hall";
    public static final String THE_CHAMBERS = "the_chambers";
    public static final String THE_OASIS = "the_oasis";
    public static final String THE_BASTION = "the_bastion";
    public static final String STARTING_ROOM = "starting_room";
    public static final String BOSS_CRUCIBLE = "boss_crucible";
    public static final int GLOBAL_Y = 60;
    public static final Vec3 SPAWN_POSITION = new Vec3(33.5, GLOBAL_Y + 2, 27.5);

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
        roomGen.add(getBattleRooms());

        if (Random.nextInt(3) == 0){
          roomGen.add(withStyleComponent(stringIdToName(SANCTUARY), COSMIC_PURPLE));
        }

        if(Random.nextInt(5) == 0){
          roomGen.add(withStyleComponent(stringIdToName(BAZAAR), AETHER_BLUE));
        }

        if(Random.nextInt(10) == 0) {
            roomGen.add(withStyleComponent(stringIdToName(BOSS_CRUCIBLE), NEGATIVE_RED));
        }

        return listRandom(roomGen);
    }

    public static @NotNull Component getBattleRooms() {
        return withStyleComponent(stringIdToName(listRandom(List.of(THE_HALL, THE_CHAMBERS, THE_OASIS, THE_BASTION))), EXPERIENCE_GREEN);
    }

    public static void placeLocksWithData(ServerLevel level, BlockPos pos) {
        var range = roomBoundingFromCenter(pos);
        var dif = List.of("Easy", "Medium", "Hard").reversed();
        var counter = 0;

        for (var blockPos : range) {
            BlockSetupManager.generateExit(level, blockPos);
            setLocks(level, blockPos, false);
            if(level.getBlockState(blockPos).is(Blocks.TINTED_GLASS)){
                level.destroyBlock(blockPos, false);
            }
            var getLock = level.getBlockEntity(blockPos);
            if(getLock instanceof LockBlockEntity lock) {
                lock.getDifficulty = dif.get(counter);
                counter++;
                lock.setRoomData();
                var getPositions = innerRadiusRandom(blockPos.getCenter().subtract(0, 1, 0), 2.3, 350);

                for (var vec3 : getPositions) {
                    var colour = lock.roomId.getStyle().getColor().getValue();
                    var particle = ParticleHandlers.getNonBakedParticles(colour, colour, 16, Random.nextInt(2, 4));
                    sendParticles(level, particle, vec3, 0, 0, 0.5, 0, Random.nextDouble(0.3, 1.2));
                }
            }
        }
    }

    public static void generateStructure(ServerLevel level){
        var pos = new BlockPos(0, GLOBAL_Y, 0);
        var getAllChunks = ChunkPos.rangeClosed(new ChunkPos(pos), 1).toList();
        var settings = new StructurePlaceSettings();

        settings.setRotationPivot(new BlockPos(pos.getX() + 23, pos.getY(), pos.getZ() + 25));
        settings.setRotation(Rotation.CLOCKWISE_90);

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, true);

        placeStructure(level, pos, settings, STARTING_ROOM);
        placeLocksWithData(level, BlockPos.containing(SPAWN_POSITION.subtract(10,0,0)));

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, false);
        //Here we can pass the data from the previous altar to set up the next challenge stack.
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
            }
        }
    }
}
