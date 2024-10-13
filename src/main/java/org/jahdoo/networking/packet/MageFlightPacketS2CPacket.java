package org.jahdoo.networking.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.all_magic.wand_perks.mage_flight.MageFlightClient;
import org.jahdoo.utils.GeneralHelpers;

public class MageFlightPacketS2CPacket implements CustomPacketPayload {

    public static final Type<MageFlightPacketS2CPacket> TYPE = new Type<>(GeneralHelpers.modResourceLocation("mage_flight_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MageFlightPacketS2CPacket> STREAM_CODEC = CustomPacketPayload.codec(MageFlightPacketS2CPacket::toBytes, MageFlightPacketS2CPacket::new);

    private final int jumpTickCounter;
    private final boolean setFlying;
    private final boolean setLastJumped;

    public MageFlightPacketS2CPacket(int jumpTickCounter, boolean setFlying, boolean setLastJumped) {
        this.jumpTickCounter = jumpTickCounter;
        this.setFlying = setFlying;
        this.setLastJumped = setLastJumped;
    }


    public MageFlightPacketS2CPacket(FriendlyByteBuf buf) {
        this.jumpTickCounter = buf.readInt();
        this.setFlying = buf.readBoolean();
        this.setLastJumped = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeInt(this.jumpTickCounter);
        bug.writeBoolean(this.setFlying);
        bug.writeBoolean(this.setLastJumped);
    }

    public boolean handle(IPayloadContext ctx) {
        MageFlightClient.mageFlightClient(ctx.player());
        return true;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
