package org.jahdoo.networking.packet;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.registers.AttachmentRegister.CASTER_DATA;

public class ManaDataSyncS2CPacket implements CustomPacketPayload {
    public static final Type<ManaDataSyncS2CPacket> TYPE = new Type<>(GeneralHelpers.modResourceLocation("sync_client_mana"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ManaDataSyncS2CPacket> STREAM_CODEC = CustomPacketPayload.codec(ManaDataSyncS2CPacket::toBytes, ManaDataSyncS2CPacket::new);

    private final double mana;

    public ManaDataSyncS2CPacket(double mana) {
        this.mana = mana;
    }

    public ManaDataSyncS2CPacket(FriendlyByteBuf buf) {
        this.mana = buf.readDouble();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeDouble(this.mana);
    }

    public boolean handle(IPayloadContext ctx) {

        ctx.enqueueWork(
            new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    if(ctx.player() instanceof LocalPlayer localPlayer) {
                        localPlayer.getData(CASTER_DATA).setLocalMana(mana);
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
