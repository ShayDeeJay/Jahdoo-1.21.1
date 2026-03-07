package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.items.PocketDimension;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

public class PocketDimensionC2SP implements CustomPacketPayload {

    public static final Type<PocketDimensionC2SP> TYPE = new Type<>(JahdooHelpers.res("pocket_dim_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PocketDimensionC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(PocketDimensionC2SP::toBytes, PocketDimensionC2SP::new);

    public PocketDimensionC2SP() {}

    public PocketDimensionC2SP(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf bug) {}

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                var player1 = ctx.player();
                if(!(player1 instanceof ServerPlayer player)) return;

                var item = JahdooHelpers.getCurioSlotItem(player, "pocket_dimension");
                var isCooldown = !player.getCooldowns().isOnCooldown(item.getItem());

                if(!item.isEmpty()){
                    if(isCooldown) {
                        PocketDimension.onUse(player, item);
                        return;
                    }
                    player.displayClientMessage(TextHelpers.withStyleComponent("Item on cooldown", -1), true);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
