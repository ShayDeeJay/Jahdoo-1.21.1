package org.jahdoo.common.networking.server2client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public class InstanceSyncS2CP implements CustomPacketPayload {
    public static final Type<InstanceSyncS2CP> TYPE = new Type<>(JahdooHelpers.res("sync_client_level"));
    public static final StreamCodec<RegistryFriendlyByteBuf, InstanceSyncS2CP> STREAM_CODEC = CustomPacketPayload.codec(InstanceSyncS2CP::toBytes, InstanceSyncS2CP::new);

    private final InstanceData data;

    public InstanceSyncS2CP(InstanceData data) {
        this.data = data;
    }

    public InstanceSyncS2CP(FriendlyByteBuf buf) {
        this.data = buf.readJsonWithCodec(InstanceData.CODEC);
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeJsonWithCodec(InstanceData.CODEC, data);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    if(ctx.player().level() instanceof ClientLevel level){
                        level.setData(INSTANCE_DATA, data);
                    }
                }
            }
        );
        return true;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
