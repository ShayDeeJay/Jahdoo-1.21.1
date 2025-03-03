package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.ability.wand_perks.mage_flight.MageFlightClient;
import org.jahdoo.ascension.utils.Helpers;

public class MageFlightC2SP implements CustomPacketPayload {

    public static final Type<MageFlightC2SP> TYPE = new Type<>(Helpers.res("mage_flight_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MageFlightC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(MageFlightC2SP::toBytes, MageFlightC2SP::new);

    public MageFlightC2SP() {}

    public MageFlightC2SP(FriendlyByteBuf buf) {}

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
