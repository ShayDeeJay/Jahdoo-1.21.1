package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.attachments.RunData;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.registers.AttachmentReg;

public class RunDataS2CP implements CustomPacketPayload {

    public static final Type<RunDataS2CP> TYPE = new Type<>(Helpers.res("sync_run_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RunDataS2CP> STREAM_CODEC =
        CustomPacketPayload.codec(RunDataS2CP::toBytes, RunDataS2CP::new);

    private final RunData runData;

    public RunDataS2CP(RunData runData) {
        this.runData = runData;
    }

    public RunDataS2CP(FriendlyByteBuf buf) {
        this.runData = buf.readJsonWithCodec(RunData.CODEC);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(RunData.CODEC, runData);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof LocalPlayer localPlayer){
                    localPlayer.setData(AttachmentReg.RUN_DATA.get(), runData);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
