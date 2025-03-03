package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.items.wand.WandItem;
import org.jahdoo.ascension.utils.Helpers;

public class StopUsingC2SP implements CustomPacketPayload {
    public static final Type<StopUsingC2SP> TYPE = new Type<>(Helpers.res("stop_using_ability"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StopUsingC2SP> STREAM_CODEC = CustomPacketPayload.codec(StopUsingC2SP::toBytes, StopUsingC2SP::new);

    public StopUsingC2SP() {}

    public StopUsingC2SP(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf bug) {}

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer && serverPlayer.getItemInHand(serverPlayer.getUsedItemHand()).getItem() instanceof WandItem){
                    serverPlayer.releaseUsingItem();
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
