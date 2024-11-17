package org.jahdoo.networking.packet.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.all_magic.wand_perks.mage_flight.MageFlightClient;
import org.jahdoo.utils.ModHelpers;

public class MageFlightPacketS2CPacket implements CustomPacketPayload {
    public static final Type<MageFlightPacketS2CPacket> TYPE = new Type<>(ModHelpers.res("mage_flight_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MageFlightPacketS2CPacket> STREAM_CODEC = CustomPacketPayload.codec(MageFlightPacketS2CPacket::toBytes, MageFlightPacketS2CPacket::new);

    public MageFlightPacketS2CPacket() {}
    public MageFlightPacketS2CPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf bug) {}

    public boolean handle(IPayloadContext ctx) {
        MageFlightClient.mageFlightClient(ctx.player());
        return true;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
