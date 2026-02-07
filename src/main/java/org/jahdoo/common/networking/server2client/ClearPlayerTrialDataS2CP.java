package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

public class ClearPlayerTrialDataS2CP implements CustomPacketPayload {

    public static final Type<ClearPlayerTrialDataS2CP> TYPE = new Type<>(JahdooHelpers.res("clear_player_trial_data_client"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClearPlayerTrialDataS2CP> STREAM_CODEC =
        CustomPacketPayload.codec(ClearPlayerTrialDataS2CP::toBytes, ClearPlayerTrialDataS2CP::new);

    public ClearPlayerTrialDataS2CP() {}

    public ClearPlayerTrialDataS2CP(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf bug) {}

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof LocalPlayer localPlayer) {
                    localPlayer.getData(AttachmentReg.PLAYER_TRIAL_DATA.get()).clearAllData();
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

}
