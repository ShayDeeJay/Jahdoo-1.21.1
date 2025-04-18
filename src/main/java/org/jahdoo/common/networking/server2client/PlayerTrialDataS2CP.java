package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.attachments.PlayerTrialData;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.registers.AttachmentReg;

public class PlayerTrialDataS2CP implements CustomPacketPayload {

    public static final Type<PlayerTrialDataS2CP> TYPE = new Type<>(Helpers.res("sync_player_trial_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerTrialDataS2CP> STREAM_CODEC =
        CustomPacketPayload.codec(PlayerTrialDataS2CP::toBytes, PlayerTrialDataS2CP::new);

    private final PlayerTrialData trialData;

    public PlayerTrialDataS2CP(PlayerTrialData trialData) {
        this.trialData = trialData;
    }

    public PlayerTrialDataS2CP(FriendlyByteBuf buf) {
        this.trialData = buf.readJsonWithCodec(PlayerTrialData.CODEC);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(PlayerTrialData.CODEC, trialData);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof LocalPlayer localPlayer){
                    localPlayer.setData(AttachmentReg.PLAYER_TRIAL_DATA.get(), trialData);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
