package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

public class PlayerExpC2SP implements CustomPacketPayload {
    public static final Type<PlayerExpC2SP> TYPE = new Type<>(JahdooHelpers.res("experience_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerExpC2SP> STREAM_CODEC = CustomPacketPayload.codec(PlayerExpC2SP::toBytes, PlayerExpC2SP::new);
    int level;

    public PlayerExpC2SP(int level) {
        this.level = level;
    }

    public PlayerExpC2SP(FriendlyByteBuf buf) {
        this.level = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(level);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    serverPlayer.setExperienceLevels(level);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
