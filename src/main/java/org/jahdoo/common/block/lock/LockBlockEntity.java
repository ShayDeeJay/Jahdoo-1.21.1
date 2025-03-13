package org.jahdoo.common.block.lock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ascension.boon.LevelBoon;
import org.jahdoo.ascension.boon.LevelBoonSelection;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.registers.BlockEntityReg;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.ascension.StructureManager.*;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.common.block.lock.LockBlock.FACING;


public class LockBlockEntity extends SyncedBlockEntity {

    public Component roomId = Component.literal("");
    public LevelBoon getBoon = LevelBoon.EMPTY;

    public LockBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityReg.LOCK_BE.get(), pPos, pBlockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("component", Component.Serializer.toJson(this.roomId, registries));
        LevelBoon.saveData(getBoon, tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.roomId = Component.Serializer.fromJson(tag.getString("component"), registries);
        this.getBoon = LevelBoon.loadData(tag, registries);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {}

    public boolean isInitialized(){
        return !this.roomId.getString().isEmpty() && this.getBoon != LevelBoon.EMPTY;
    }

    public boolean canPlace(){
        var level = getLevel();
        if(level == null) return false;

        var floorBlock = getBlockPos().relative(getBlockState().getValue(FACING), 1).below(2);
        return level.getBlockState(floorBlock).isAir();
    }

    public void setRoomData(){
        this.roomId = getRandomRoomId();
        this.getBoon = LevelBoonSelection.getRandomBoon();
        this.updateBlock();
    }

    public static Component getRandomRoomId(){
        var roomGen = new ArrayList<Component>();
        roomGen.add(withStyleComponent(ROOM, PERK_GREEN));
        roomGen.add(withStyleComponent(TRADING_POST, AETHER_BLUE));
        roomGen.add(withStyleComponent(POWER_UP, COSMIC_PURPLE));

//        if(Random.nextInt(4) == 0){
//            roomGen.add(
//                listRandom(
//                    List.of(
////                        withStyleComponent(TRADING_POST, AETHER_BLUE),
////                        withStyleComponent(POWER_UP, COSMIC_PURPLE)
//                    )
//                )
//            );
//        }

        return listRandom(roomGen);
    }

}

