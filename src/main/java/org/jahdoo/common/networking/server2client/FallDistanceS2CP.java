package org.jahdoo.common.networking.server2client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.utils.Helpers;

public class FallDistanceS2CP implements CustomPacketPayload {
        public static final Type<FallDistanceS2CP> TYPE = new Type<>(Helpers.res("reset_fall"));
        public static final StreamCodec<RegistryFriendlyByteBuf, FallDistanceS2CP> STREAM_CODEC = CustomPacketPayload.codec(FallDistanceS2CP::toBytes, FallDistanceS2CP::new);

        public FallDistanceS2CP() {}

        public FallDistanceS2CP(FriendlyByteBuf buf) {}

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
