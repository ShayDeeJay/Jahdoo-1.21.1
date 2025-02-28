package org.jahdoo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SyncedBlockEntity extends BlockEntity {

    public SyncedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, registries);
        return tag;
    }

    public void updateBlock() {
        if(getLevel() != null) {
            var state = getLevel().getBlockState(worldPosition);
            getLevel().sendBlockUpdated(worldPosition, state, state, 3);
            setChanged();
        }
    }

    @Override
    public void onDataPacket(
        Connection net,
        ClientboundBlockEntityDataPacket pkt,
        HolderLookup.Provider lookupProvider
    ) {
        super.onDataPacket(net, pkt, lookupProvider);
        handleUpdateTag(pkt.getTag(), lookupProvider);
    }

}