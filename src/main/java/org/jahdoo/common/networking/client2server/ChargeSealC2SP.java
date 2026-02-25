package org.jahdoo.common.networking.client2server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.block.AbstractBEInventory;

public class ChargeSealC2SP implements CustomPacketPayload {
    public static final Type<ChargeSealC2SP> TYPE = new Type<>(JahdooHelpers.res("wand_data_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChargeSealC2SP> STREAM_CODEC =
            CustomPacketPayload.codec(ChargeSealC2SP::toBytes, ChargeSealC2SP::new);

    BlockPos blockPos;
    int index;

    public ChargeSealC2SP(BlockPos blockPos, int index) {
        this.blockPos = blockPos;
        this.index = index;
    }

    public ChargeSealC2SP(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.index = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeInt(index);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player().level() instanceof ServerLevel serverLevel){
                    var bEntity = serverLevel.getBlockEntity(blockPos);
                    if(bEntity instanceof AbstractBEInventory abstractBEntity){
                        var inputItemHandler = abstractBEntity.getInputItemHandler();
                        var item = inputItemHandler.getStackInSlot(index);
                        item.shrink(1);
                    }
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
