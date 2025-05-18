package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.Helpers;

public class GivePlayerItemsC2SP implements CustomPacketPayload {
    public static final Type<GivePlayerItemsC2SP> TYPE = new Type<>(Helpers.res("give_player_items"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GivePlayerItemsC2SP> STREAM_CODEC = CustomPacketPayload.codec(GivePlayerItemsC2SP::toBytes, GivePlayerItemsC2SP::new);

    private final ItemStack reward;

    public GivePlayerItemsC2SP(ItemStack reward) {
        this.reward = reward;
    }

    public GivePlayerItemsC2SP(FriendlyByteBuf buf) {
        this.reward = buf.readJsonWithCodec(ItemStack.CODEC);
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeJsonWithCodec(ItemStack.CODEC, reward);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    if(ctx.player() instanceof ServerPlayer serverPlayer) {
                        Helpers.throwOrAddItem(serverPlayer, reward);
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
