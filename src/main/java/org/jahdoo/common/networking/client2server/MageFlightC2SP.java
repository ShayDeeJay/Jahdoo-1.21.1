package org.jahdoo.common.networking.client2server;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import static org.jahdoo.common.registers.AttachmentReg.MAGE_FLIGHT;

public class MageFlightC2SP implements CustomPacketPayload {

    public static final Type<MageFlightC2SP> TYPE = new Type<>(JahdooHelpers.res("mage_flight_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MageFlightC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(MageFlightC2SP::toBytes, MageFlightC2SP::new);

    public MageFlightC2SP() {}

    public MageFlightC2SP(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf bug) {}

    public boolean handle(IPayloadContext ctx) {
        if(ctx.player() instanceof LocalPlayer localPlayer){
            PacketDistributor.sendToServer(new FlyingC2SP(localPlayer.input.jumping));
            localPlayer.getData(MAGE_FLIGHT).setJumpKeyDown(localPlayer.input.jumping);
        }
        return true;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
