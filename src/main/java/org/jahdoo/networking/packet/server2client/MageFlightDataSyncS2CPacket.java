package org.jahdoo.networking.packet.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.registers.AttachmentRegister.MAGE_FLIGHT;

public class MageFlightDataSyncS2CPacket implements CustomPacketPayload {
    public static final Type<MageFlightDataSyncS2CPacket> TYPE = new Type<>(ModHelpers.modResourceLocation("sync_mage_flight"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MageFlightDataSyncS2CPacket> STREAM_CODEC = CustomPacketPayload.codec(MageFlightDataSyncS2CPacket::toBytes, MageFlightDataSyncS2CPacket::new);

    private final int jumpTickCounter;
    private final boolean lastJumped;
    private final boolean isFlying;
    private final boolean jumpKeyDown;

    public MageFlightDataSyncS2CPacket(
        int jumpTickCounter,
        boolean lastJumped,
        boolean isFlying,
        boolean jumpKeyDown
    ) {
        this.jumpKeyDown = jumpKeyDown;
        this.jumpTickCounter = jumpTickCounter;
        this.isFlying = isFlying;
        this.lastJumped = lastJumped;
    }

    public MageFlightDataSyncS2CPacket(FriendlyByteBuf buf) {
        this.jumpTickCounter = buf.readInt();
        this.jumpKeyDown = buf.readBoolean();
        this.isFlying = buf.readBoolean();
        this.lastJumped = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeInt(this.jumpTickCounter);
        bug.writeBoolean(this.lastJumped);
        bug.writeBoolean(this.isFlying);
        bug.writeBoolean(this.jumpKeyDown);
    }

    public boolean handle(IPayloadContext ctx) {

        ctx.enqueueWork(
            new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    if(ctx.player() instanceof LocalPlayer localPlayer) {
                        var mageFlight = localPlayer.getData(MAGE_FLIGHT);
                        mageFlight.setIsFlying(isFlying);
                        mageFlight.setJumpTickCounter(jumpTickCounter);
                        mageFlight.setLastJumped(lastJumped);
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
