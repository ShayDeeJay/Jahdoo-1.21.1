package org.jahdoo.block.enchanted_block;

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
import org.jahdoo.client.IconLocations;
import org.jahdoo.networking.packet.server2client.EnchantedBlockS2C;
import org.jahdoo.registers.BlockEntitiesRegister;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.ModTags;

import java.util.List;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.utils.ModHelpers.Random;


public class EnchantedBlockEntity extends BlockEntity {
    int counter;
    public int stage;
    public Block block;
    public int growthChance;
    public int spreadChance;
    public static final int MAX_STAGE = 6;

    public EnchantedBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntitiesRegister.ENCHANTED_BE.get(), pPos, pBlockState);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.putInt("counter", this.counter);
        pTag.putInt("stage", this.stage);
        pTag.putInt("chance", this.growthChance);
        pTag.putInt("spread", this.spreadChance);
        if(this.block != null) pTag.putInt("block", Block.getId(block.defaultBlockState()));
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        this.counter = pTag.getInt("counter");
        this.stage = pTag.getInt("stage");
        this.growthChance = pTag.getInt("chance");
        this.spreadChance = pTag.getInt("spread");
        this.block = Block.stateById(pTag.getInt("block")).getBlock();
        super.loadAdditional(pTag, pRegistries);
    }

    public void setBlockType(Block block, int spreadChance){
        this.block = block;
        this.growthChance = ConverterValues.setBlockType(block);
        this.spreadChance = spreadChance + ConverterValues.setSpreadChance(block);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        counter++;
        if(pLevel instanceof ServerLevel serverLevel && this.block != null){
            updatePacket(serverLevel, pPos);
            if(stage < MAX_STAGE){
                onStageProgression();
            } else {
                updateState(pPos, serverLevel);
                onNeighbourSpread(pLevel, serverLevel);
            }
        }
    }

    private void onNeighbourSpread(Level pLevel, ServerLevel serverLevel) {
        for(Pair<ResourceLocation, BlockPos> block : direction()){
            var nPos = block.getSecond();
            var comparison = pLevel.getBlockState(nPos);
            var current = this.block.defaultBlockState();
            if(spreadChance == 0 || Random.nextInt(0, spreadChance) == 0){
                if(ConverterValues.isMatching(comparison, current)){
                    convertAndInfest(nPos, serverLevel);
                }
            }
        }
    }

    private void onStageProgression() {
        if(growthChance == 0) return;
        if (Random.nextInt(0, growthChance) == 0) stage++;
    }

    private void updatePacket(ServerLevel serverLevel, BlockPos pPos){
        ModHelpers.sendPacketsToPlayerDistance(pPos.getCenter(), 64, serverLevel, new EnchantedBlockS2C(pPos, this.block.defaultBlockState(), stage, growthChance, spreadChance));
    }

    private void updateState(BlockPos pPos, ServerLevel serverLevel){
        serverLevel.destroyBlock(pPos, false);
        serverLevel.setBlock(pPos, BlocksRegister.RAW_NEXITE_BLOCK.get().defaultBlockState(), 2);
    }

    private void convertAndInfest(BlockPos pPos, ServerLevel serverLevel) {
        var newState = BlocksRegister.ENCHANTED_BLOCK.get().defaultBlockState();
        if(this.block != null){
            var state = serverLevel.getBlockState(pPos).getBlock();
            serverLevel.setBlock(pPos, newState, 2);

            if (serverLevel.getBlockEntity(pPos) instanceof EnchantedBlockEntity entity) {
                if (this.block != null) entity.setBlockType(state, this.spreadChance);
            }
        }
    }

    public List<Pair<ResourceLocation, BlockPos>> direction(){
        return List.of(
            Pair.of(IconLocations.NORTH, this.getBlockPos().north()),
            Pair.of(IconLocations.WEST, this.getBlockPos().west()),
            Pair.of(IconLocations.UP, this.getBlockPos().above()),
            Pair.of(IconLocations.EAST, this.getBlockPos().east()),
            Pair.of(IconLocations.SOUTH, this.getBlockPos().south()),
            Pair.of(IconLocations.DOWN, this.getBlockPos().below())
        );
    }

    public enum ConverterValues {
        GARBAGE("garbage", ModTags.Block.GARBAGE_BLOCKS, 300, 3),
        LEAVES("leaves", BlockTags.LEAVES, 200, 3),
        LOGS("logs", BlockTags.LOGS, 140, 2),
        COMMON_ORE("common_ore", ModTags.Block.COMMON_ORE, 80, 2),
        RARE_ORES("rare_ore", ModTags.Block.RARE_ORE, 40, 1),
        OPULENT("opulent", ModTags.Block.RARE_BLOCKS, 10, 0);
        private static final List<ConverterValues> CONVERTER_VALUES =
        List.of(GARBAGE, LEAVES, LOGS, COMMON_ORE, RARE_ORES, OPULENT);

        private final String name;
        private final TagKey<Block> type;
        private final int progressChance;
        private final int fade;

        ConverterValues(String name, TagKey<Block> type, int progressChance, int fade) {
            this.name = name;
            this.type = type;
            this.progressChance = progressChance;
            this.fade = fade;
        }

        public static boolean isConvertibleBlock(Block block){
            for (ConverterValues types : CONVERTER_VALUES){
                if(block.defaultBlockState().is(types.getType())) return true;
            }
            return false;
        }

        public static int setBlockType(Block block){
            for (ConverterValues types : CONVERTER_VALUES){
                if(block.defaultBlockState().is(types.getType())) return types.getProgressChance();
            }
            return 0;
        }

        public static int setSpreadChance(Block block){
            for (ConverterValues types : CONVERTER_VALUES){
                if(block.defaultBlockState().is(types.getType())) return types.getFade();
            }
            return 0;
        }

        public static boolean isMatching(BlockState comparison, BlockState current) {
            for (ConverterValues types : CONVERTER_VALUES){
                if(comparison.is(types.getType()) && current.is(types.getType())) return true;
            }
            return false;
        }

        public int getFade() {
            return fade;
        }

        public int getProgressChance() {
            return progressChance;
        }

        public String getName() {
            return name;
        }
        public TagKey<Block> getType() {
            return type;
        }
    };


}

