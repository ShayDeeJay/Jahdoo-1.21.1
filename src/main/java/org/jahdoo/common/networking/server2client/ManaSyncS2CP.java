package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.utils.Helpers;

import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;

public class ManaSyncS2CP implements CustomPacketPayload {
    public static final Type<ManaSyncS2CP> TYPE = new Type<>(Helpers.res("sync_client_mana"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ManaSyncS2CP> STREAM_CODEC = CustomPacketPayload.codec(ManaSyncS2CP::toBytes, ManaSyncS2CP::new);

    private final double mana;

    public ManaSyncS2CP(double mana) {
        this.mana = mana;
    }

    public ManaSyncS2CP(FriendlyByteBuf buf) {
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
