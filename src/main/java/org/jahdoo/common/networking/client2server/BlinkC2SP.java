package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.attachments.player_abilities.Blink;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

public class BlinkC2SP implements CustomPacketPayload {

    public static final Type<BlinkC2SP> TYPE = new Type<>(JahdooHelpers.res("blink_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlinkC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(BlinkC2SP::toBytes, BlinkC2SP::new);

    public BlinkC2SP() {}

    public BlinkC2SP(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf bug) {}

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(!(ctx.player() instanceof ServerPlayer player)) return;
                Blink.setMovement(player);

            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
