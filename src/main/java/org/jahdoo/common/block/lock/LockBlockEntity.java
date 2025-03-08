package org.jahdoo.common.block.lock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.block.AbstractBEInventory;
import org.jahdoo.common.block.AbstractTankUser;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.BlockEntityReg;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.ItemReg;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.common.block.AbstractTankUser.findInRange;
import static org.jahdoo.common.block.lock.LockBlock.FACING;
import static org.jahdoo.common.block.tank.TankBlock.LIT;


public class LockBlockEntity extends SyncedBlockEntity {

    public Component roomId;

    public LockBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityReg.LOCK_BE.get(), pPos, pBlockState);
        this.roomId = getRandomRoomId();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("component", Component.Serializer.toJson(this.roomId, registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.roomId = Component.Serializer.fromJson(tag.getString("component"), registries);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {

    }

    public boolean canPlace(){
        var level = getLevel();
        if(level == null) return false;

        return level.getBlockState(getBlockPos().relative(getBlockState().getValue(FACING),1).below(2)).isAir();
    }

    public static Component getRandomRoomId(){
        var roomGen = new ArrayList<Component>();
        roomGen.add(withStyleComponent("Room", PERK_GREEN));

        if(Random.nextInt(4) == 0){
            roomGen.add(
                listRandom(
                    List.of(
                        withStyleComponent("Trading Post", AETHER_BLUE),
                        withStyleComponent("Power Up", COSMIC_PURPLE)
                    )
                )
            );
        }

        return listRandom(roomGen);
    }

}

