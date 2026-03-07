package org.jahdoo.common.block.enchanted_block;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.BlockReg;
import org.shaydee.shaydeeapi.block.SyncedBlockEntity;

import java.util.List;

import static org.jahdoo.trial_nexus.utils.Icons.*;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;


public class EnchantedBlockEntity extends SyncedBlockEntity {

    public static final int MAX_STAGE = 6;
    public int stage;
    public Block block;
    public int growthChance;
    public int spreadChance;
    private int counter;

    public EnchantedBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.ENCHANTED_BE.get(), pos, state);
    }

    private void onStageProgression() {
        this.updateBlock();
        if(growthChance == 0) return;
        if (Random.nextInt(0, growthChance) == 0) stage++;
    }

    private void updateState(BlockPos pos, ServerLevel serverLevel){
        serverLevel.destroyBlock(pos, false);
        serverLevel.setBlock(pos, BlockReg.RAW_NEXITE_BLOCK.get().defaultBlockState(), 2);
    }

    public void setBlockType(Block block, int spreadChance){
        this.block = block;
        this.growthChance = ConverterValues.setBlockType(block);
        this.spreadChance = spreadChance + ConverterValues.setSpreadChance(block);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("counter", this.counter);
        tag.putInt("stage", this.stage);
        tag.putInt("chance", this.growthChance);
        tag.putInt("spread", this.spreadChance);
        if(this.block != null) tag.putInt("block", Block.getId(block.defaultBlockState()));
        super.saveAdditional(tag, registries);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
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
        var newState = BlockReg.ENCHANTED_BLOCK.get().defaultBlockState();
        if(this.block != null){
            var state = serverLevel.getBlockState(pos).getBlock();
            serverLevel.setBlock(pos, newState, 2);

            if (serverLevel.getBlockEntity(pos) instanceof EnchantedBlockEntity entity) {
                if (this.block != null) entity.setBlockType(state, this.spreadChance);
            }
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        counter++;
        if(level instanceof ServerLevel serverLevel && this.block != null){
            if(stage < MAX_STAGE){
                onStageProgression();
            } else {
                updateState(pos, serverLevel);
                onNeighbourSpread(level, serverLevel);
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
}

