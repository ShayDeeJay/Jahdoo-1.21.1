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
import org.shaydee.shaydeeapi.helpers.ItemHelpers;

public class DurabilityC2SP implements CustomPacketPayload {
    public static final Type<DurabilityC2SP> TYPE = new Type<>(JahdooHelpers.res("durability_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DurabilityC2SP> STREAM_CODEC =
            CustomPacketPayload.codec(DurabilityC2SP::toBytes, DurabilityC2SP::new);

    BlockPos blockPos;
    int duraIncrease;

    public DurabilityC2SP(BlockPos blockPos, int duraIncrease) {
        this.blockPos = blockPos;
        this.duraIncrease = duraIncrease;
    }

    public DurabilityC2SP(FriendlyByteBuf buf) {
        this.blockPos = buf.readBlockPos();
        this.duraIncrease = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(blockPos);
        buf.writeInt(duraIncrease);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player().level() instanceof ServerLevel serverLevel){
                    var bEntity = serverLevel.getBlockEntity(blockPos);
                    if(bEntity instanceof AbstractBEInventory abstractBEntity){
                        var inputItemHandler = abstractBEntity.getInputItemHandler();
                        var item = inputItemHandler.getStackInSlot(0);
                        ItemHelpers.setDurability(item, item.getMaxDamage() + duraIncrease);
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
