package org.jahdoo.networking.packet.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

import static org.jahdoo.registers.AttachmentRegister.BOUNCY_FOOT;
import static org.jahdoo.utils.Maths.singleFormattedDouble;

public class BouncyFootDataSyncS2CPacket implements CustomPacketPayload {
    public static final Type<BouncyFootDataSyncS2CPacket> TYPE = new Type<>(ModHelpers.res("sync_bouncy_foot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BouncyFootDataSyncS2CPacket> STREAM_CODEC = CustomPacketPayload.codec(BouncyFootDataSyncS2CPacket::toBytes, BouncyFootDataSyncS2CPacket::new);

    private final int effectTimer;
    private final double previousDelta;
    private final double currentDelta;
    private final float maxFall;

    public BouncyFootDataSyncS2CPacket(
        int effectTimer,
        double previousDelta,
        double currentDelta,
        float maxFall
    ) {
        this.effectTimer = effectTimer;
        this.previousDelta = previousDelta;
        this.currentDelta = currentDelta;
        this.maxFall = maxFall;
    }

    public BouncyFootDataSyncS2CPacket(FriendlyByteBuf buf) {
        this.effectTimer = buf.readInt();
        this.previousDelta = buf.readDouble();
        this.currentDelta = buf.readDouble();
        this.maxFall = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeInt(this.effectTimer);
        bug.writeDouble(this.previousDelta);
        bug.writeDouble(this.currentDelta);
        bug.writeFloat(this.maxFall);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            new Runnable() {
                @Override
                public void run() {
                    if(ctx.player() instanceof LocalPlayer localPlayer) {
                        localPlayer.resetFallDistance();
                        if (localPlayer.verticalCollisionBelow && previousDelta != currentDelta) {
                            if(maxFall > 0.5D){
                                var reducedDelta = Math.abs(previousDelta / 2.5);
                                localPlayer.playSound(SoundEvents.FROG_TONGUE, (float) reducedDelta + 0.2f, 1.8f);
                                localPlayer.setDeltaMovement(localPlayer.getDeltaMovement().add(0, singleFormattedDouble(Math.min(reducedDelta, 1)), 0));
                            }
                        }
                    }
                }
            }
        );
        return true;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
