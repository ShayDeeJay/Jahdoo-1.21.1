package org.jahdoo.ascension;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import org.apache.logging.log4j.Level;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.attachments.player_abilities.ChallengeLevelData;
import org.jahdoo.ascension.utils.Helpers;

import java.util.Objects;

import static net.minecraft.core.SectionPos.blockToSectionCoord;
import static net.minecraft.resources.ResourceLocation.withDefaultNamespace;
import static org.jahdoo.ascension.BlockSetupManager.generateTradingPost;
import static org.jahdoo.ascension.BlockSetupManager.setTrialDim;
import static org.jahdoo.ascension.DimHandler.TRADING_POST;
import static org.jahdoo.ascension.DimHandler.TRIAL;

public class StructureManager {

    private static void checkLoaded(ServerLevel level, ChunkPos start, ChunkPos end) throws CommandSyntaxException {
        if (ChunkPos.rangeClosed(start, end).anyMatch((chunkPos) -> !level.isLoaded(chunkPos.getWorldPosition()))) {
            throw BlockPosArgument.ERROR_NOT_LOADED.create();
        }
    }

    public static void placeStructureJigsaw(ServerLevel level, BlockPos pos) throws CommandSyntaxException {
        var location = Helpers.res("challenge_arena/challenge_pool_1");
        var registry = level.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        var holder = registry.getHolderOrThrow(ResourceKey.create(Registries.TEMPLATE_POOL, location));

        JigsawPlacement.generateJigsaw(level, holder,  withDefaultNamespace("empty"), 10, pos, true);
    }

    static void generateStructure(Player player, ServerLevel level, ChallengeLevelData data, int stage, String id){
        var pos = new BlockPos(0, 40, 0);
        var getAllChunks = ChunkPos.rangeClosed(new ChunkPos(pos), 3).toList();

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, true);
        var isTrial = Objects.equals(id, TRIAL);
        var isTrading = Objects.equals(id, TRADING_POST);

        try {
            if(isTrial) placeStructureJigsaw(level, pos);
            if(isTrading) placeStructure(level, pos);
        } catch (CommandSyntaxException e) {
            JahdooMod.LOGGER.log(Level.ALL, e);
            throw new RuntimeException(e);
        }

        for (var chunkPos : getAllChunks) level.setChunkForced(chunkPos.x, chunkPos.z, false);
        //Here we can pass the data from the previous altar to set up the next challenge stack.
        if(isTrial) setTrialDim(level, data);
        if(isTrading) generateTradingPost(level);
    }

    public static void placeStructure(ServerLevel level, BlockPos pos) throws CommandSyntaxException {
        var registry = level.registryAccess().registry(Registries.STRUCTURE);
        if(registry.isEmpty()) return;

        var structure = registry.get().get(Helpers.res("trading_post"));
        if(structure == null) return;

        var chunkgenerator = level.getChunkSource().getGenerator();
        var structurestart = structure.generate(
            level.registryAccess(),
            chunkgenerator,
            chunkgenerator.getBiomeSource(),
            level.getChunkSource().randomState(),
            level.getStructureManager(),
            level.getSeed(),
            new ChunkPos(pos),
            0,
            level,
            (biomeHolder) -> true
        );

        if (structurestart.isValid()) {
            var boundingbox = structurestart.getBoundingBox();
            var chunkPosPrimary = new ChunkPos(blockToSectionCoord(boundingbox.minX()), blockToSectionCoord(boundingbox.minZ()));
            var chunkPosSecondary = new ChunkPos(blockToSectionCoord(boundingbox.maxX()), blockToSectionCoord(boundingbox.maxZ()));

            checkLoaded(level, chunkPosPrimary, chunkPosSecondary);
            for (var cPos : ChunkPos.rangeClosed(chunkPosPrimary, chunkPosSecondary).toList()) {
                var box = new BoundingBox(cPos.getMinBlockX(), level.getMinBuildHeight(), cPos.getMinBlockZ(), cPos.getMaxBlockX(), level.getMaxBuildHeight(), cPos.getMaxBlockZ());
                structurestart.placeInChunk(level, level.structureManager(), chunkgenerator, level.getRandom(), box, cPos);
            }
        }
    }

}
