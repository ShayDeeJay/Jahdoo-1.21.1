package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.registers.AttachmentReg;

public class CastingDataSyncS2CP implements CustomPacketPayload {

    public static final Type<CastingDataSyncS2CP> TYPE = new Type<>(Helpers.res("selected_ability_s2c"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CastingDataSyncS2CP> STREAM_CODEC =
        CustomPacketPayload.codec(CastingDataSyncS2CP::toBytes, CastingDataSyncS2CP::new);

    private final CasterData data;

    public CastingDataSyncS2CP(CasterData data) {
        this.data = data;
    }

    public CastingDataSyncS2CP(FriendlyByteBuf buf) {
        this.data = buf.readJsonWithCodec(CasterData.CODEC);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(CasterData.CODEC, data);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof LocalPlayer localPlayer){
                    localPlayer.setData(AttachmentReg.CASTER_DATA, data);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
