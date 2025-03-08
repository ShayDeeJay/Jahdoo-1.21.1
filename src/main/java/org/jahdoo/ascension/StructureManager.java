package org.jahdoo.ascension;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.registers.BlockReg;

import java.util.List;
import java.util.Objects;

import static net.minecraft.core.BlockPos.betweenClosed;
import static net.minecraft.resources.ResourceLocation.withDefaultNamespace;
import static net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement.*;
import static org.jahdoo.ascension.BlockSetupManager.generateTradingPost;
import static org.jahdoo.ascension.BlockSetupManager.setTrialDim;
import static org.jahdoo.ascension.DimHandler.TRADING_POST;
import static org.jahdoo.ascension.DimHandler.TRIAL;

public class StructureManager {

    static void generateStructure(ServerLevel level, ChallengeLevelData data, String id){
        var pos = new BlockPos(0, 40, 0);
        var getAllChunks = ChunkPos.rangeClosed(new ChunkPos(pos), 3).toList();
        var settings = new StructurePlaceSettings();
        var isTrial = Objects.equals(id, TRIAL);
        var isTrading = Objects.equals(id, TRADING_POST);

        settings.setRotationPivot(new BlockPos(pos.getX() + 23, pos.getY(), pos.getZ() + 25));
        settings.setRotation(Rotation.CLOCKWISE_90);

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, true);

        try {
            if(isTrial) placeStructureJigsaw(level, pos);
            if(isTrading) placeStructure(level, pos, settings, "room");
        } catch (CommandSyntaxException e) {
            JahdooMod.LOGGER.log(org.apache.logging.log4j.Level.ALL, e);
            throw new RuntimeException(e);
        }

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, false);
        //Here we can pass the data from the previous altar to set up the next challenge stack.

        if (isTrial) setTrialDim(level, data);
//        if (isTrading) generateTradingPost(level, pos, settings.getRotation());
    }

    public static void placeStructureJigsaw(ServerLevel level, BlockPos pos) throws CommandSyntaxException {
        var location = Helpers.res("challenge_arena/challenge_pool_1");
        var template = Registries.TEMPLATE_POOL;
        var registry = level.registryAccess().registryOrThrow(template);
        var holder = registry.getHolderOrThrow(ResourceKey.create(template, location));

        generateJigsaw(level, holder, withDefaultNamespace("empty"), 10, pos, true);
    }

    public static void placeStructure(ServerLevel level, BlockPos pos, StructurePlaceSettings settings, String roomId) {
        var templates = level.getStructureManager().get(Helpers.res(roomId));

        templates.ifPresent(template -> template.placeInWorld(level, pos, new BlockPos(-22, 0, -22), settings, level.random, 2));
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
            var findBlock = betweenClosed(
                newPos.getX() - 50, newPos.getY() - 1, newPos.getZ() - 50,
                newPos.getX() + 50, newPos.getY() + 30, newPos.getZ() + 50
            );

            generateTradingPost(serverLevel, findBlock, direction);

            for (var blockPos : findBlock) {
                if (level.getBlockState(blockPos).is(Blocks.DIAMOND_BLOCK)) {
                    level.setBlockAndUpdate(blockPos, BlockReg.CHALLENGE_ALTAR.get().defaultBlockState());
                }
            }
        }
    }
}
