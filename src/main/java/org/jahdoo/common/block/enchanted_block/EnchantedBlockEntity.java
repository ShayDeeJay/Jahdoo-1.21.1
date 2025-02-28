package org.jahdoo.common.block.enchanted_block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.common.client.IconLocations;
import org.jahdoo.common.networking.packet.server2client.EnchantedBlockS2C;
import org.jahdoo.common.registers.BlockEntitiesRegister;
import org.jahdoo.common.registers.BlocksRegister;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.ModTags;

import java.util.List;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.client.IconLocations.*;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.ascension.utils.Helpers.Random;


public class EnchantedBlockEntity extends BlockEntity {

    public static final int MAX_STAGE = 6;
    public int stage;
    public Block block;
    public int growthChance;
    public int spreadChance;
    private int counter;

    public EnchantedBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegister.ENCHANTED_BE.get(), pos, state);
    }

    private void onStageProgression() {
        if(growthChance == 0) return;
        if (Random.nextInt(0, growthChance) == 0) stage++;
    }

    private void updatePacket(ServerLevel serverLevel, BlockPos pos){
        var payload = new EnchantedBlockS2C(pos, this.block.defaultBlockState(), stage, growthChance, spreadChance);
        Helpers.sendPacketsToPlayerDistance(pos.getCenter(), 64, serverLevel, payload);
    }

    private void updateState(BlockPos pos, ServerLevel serverLevel){
        serverLevel.destroyBlock(pos, false);
        serverLevel.setBlock(pos, BlocksRegister.RAW_NEXITE_BLOCK.get().defaultBlockState(), 2);
    }

    public void setBlockType(Block block, int spreadChance){
        this.block = block;
        this.growthChance = ConverterValues.setBlockType(block);
        this.spreadChance = spreadChance + ConverterValues.setSpreadChance(block);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("counter", this.counter);
        tag.putInt("stage", this.stage);
        tag.putInt("chance", this.growthChance);
        tag.putInt("spread", this.spreadChance);
        if(this.block != null) tag.putInt("block", Block.getId(block.defaultBlockState()));
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        this.counter = tag.getInt("counter");
        this.stage = tag.getInt("stage");
        this.growthChance = tag.getInt("chance");
        this.spreadChance = tag.getInt("spread");
        this.block = Block.stateById(tag.getInt("block")).getBlock();
        super.loadAdditional(tag, registries);
    }

    public List<Pair<ResourceLocation, BlockPos>> direction(){
        return List.of(
            Pair.of(NORTH, this.getBlockPos().north()),
            Pair.of(WEST, this.getBlockPos().west()),
            Pair.of(UP, this.getBlockPos().above()),
            Pair.of(EAST, this.getBlockPos().east()),
            Pair.of(SOUTH, this.getBlockPos().south()),
            Pair.of(DOWN, this.getBlockPos().below())
        );
    }

    private void convertAndInfest(BlockPos pos, ServerLevel serverLevel) {
        var newState = BlocksRegister.ENCHANTED_BLOCK.get().defaultBlockState();
        if(this.block != null){
            var state = serverLevel.getBlockState(pos).getBlock();
            serverLevel.setBlock(pos, newState, 2);

            if (serverLevel.getBlockEntity(pos) instanceof EnchantedBlockEntity entity) {
                if (this.block != null) entity.setBlockType(state, this.spreadChance);
            }
        }
    }

    private void onNeighbourSpread(Level level, ServerLevel serverLevel) {
        for(var block : direction()){
            var nPos = block.getSecond();
            var comparison = level.getBlockState(nPos);
            var current = this.block.defaultBlockState();
            if(spreadChance == 0 || Random.nextInt(0, spreadChance) == 0){
                if(ConverterValues.isMatching(comparison, current)){
                    convertAndInfest(nPos, serverLevel);
                }
            }
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        counter++;
        if(level instanceof ServerLevel serverLevel && this.block != null){
            updatePacket(serverLevel, pos);
            if(stage < MAX_STAGE){
                onStageProgression();
            } else {
                updateState(pos, serverLevel);
                onNeighbourSpread(level, serverLevel);
            }
        }
    }
}

