package org.jahdoo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.particle.particle_options.GenericParticleOptions;
import org.jahdoo.common.registers.ElementRegistry;

import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.common.particle.ParticleStore.*;
import static org.jahdoo.common.registers.ElementRegistry.*;

public abstract class AbstractBEInventory extends SyncedBlockEntity {

    protected ContainerData data;

    public final ItemStackHandler inputItemHandler =
        this.setStackHandler(this.setInputSlots(), this.getMaxSlotSize());

    public final ItemStackHandler outputItemHandler =
        this.setStackHandler(this.setOutputSlots(), this.getMaxSlotSize());

    public abstract int setInputSlots();

    public abstract int setOutputSlots();

    public abstract int getMaxSlotSize();

    public ContainerData getData() {
        return data;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
    }

    public static GenericParticleOptions processingParticle(int lifetime, float size, boolean staticSize, double speed){
        return genericParticleOptions(SOFT_PARTICLE_SELECTION, utility(), lifetime, size, staticSize, speed);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        super.onDataPacket(net, pkt, provider);
        if(level == null) return;

        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    public static void sendBlockUpdate(Level level, Runnable setChanged, BlockPos blockPos, BlockState blockState){
        if(level == null) return;

        setChanged.run();
        level.sendBlockUpdated(blockPos, blockState, blockState,3);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("inputInventory", inputItemHandler.serializeNBT(registries));
        tag.put("outputInventory", outputItemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        inputItemHandler.deserializeNBT(registries, tag.getCompound("inputInventory"));
        outputItemHandler.deserializeNBT(registries, tag.getCompound("outputInventory"));
    }

    public ItemStackHandler setStackHandler(int slots, int slotStackLimit){
        return new ItemStackHandler(slots) {
            protected void onContentsChanged(int slot) {
                sendBlockUpdate(level, () -> setChanged(), getBlockPos(), getBlockState());
            }

            @Override
            public int getSlotLimit(int slot) {
                return getMaxSlotSize();
            }
        };
    }

    public AbstractBEInventory(BlockEntityType<?> type, BlockPos pos, BlockState state, int stackSize) {
        super(type, pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return 0;
            }

            @Override
            public void set(int pIndex, int pValue) {}

            @Override
            public int getCount() {
                return 0;
            }
        };
    }

    public void dropsAllInventory(Level level) {
        var inputInventory = new SimpleContainer(setInputSlots());
        var outputInventory = new SimpleContainer(setOutputSlots());

        for (int i = 0; i < this.inputItemHandler.getSlots(); i++) {
            if (i < inputInventory.getContainerSize()) {
                var stackInSlot = inputItemHandler.getStackInSlot(i);
                inputInventory.setItem(i, stackInSlot);
            }
        }

        for (int i = 0; i < this.outputItemHandler.getSlots(); i++) {
            if (i < outputInventory.getContainerSize()) {
                var stackInSlot = outputItemHandler.getStackInSlot(i);
                outputInventory.setItem(i, stackInSlot);
            }
        }

        Containers.dropContents(level, this.worldPosition, inputInventory);
        Containers.dropContents(level, this.worldPosition, outputInventory);
    }
}
