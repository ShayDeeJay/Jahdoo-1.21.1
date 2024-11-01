package org.jahdoo.networking.packet.server2client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.utils.ModHelpers;

public class FallDistanceSyncC2SPacket implements CustomPacketPayload {
    public static final Type<FallDistanceSyncC2SPacket> TYPE = new Type<>(ModHelpers.modResourceLocation("reset_fall"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FallDistanceSyncC2SPacket> STREAM_CODEC = CustomPacketPayload.codec(FallDistanceSyncC2SPacket::toBytes, FallDistanceSyncC2SPacket::new);

    public FallDistanceSyncC2SPacket() {}

    public FallDistanceSyncC2SPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    if(ctx.player() instanceof ServerPlayer serverPlayer) {
                        serverPlayer.resetFallDistance();
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
