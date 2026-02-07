package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import java.util.List;

public class PlayerTrialDataS2CP implements CustomPacketPayload {

    public static final Type<PlayerTrialDataS2CP> TYPE = new Type<>(JahdooHelpers.res("sync_player_trial_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerTrialDataS2CP> STREAM_CODEC =
        CustomPacketPayload.codec(PlayerTrialDataS2CP::toBytes, PlayerTrialDataS2CP::new);

    private final List<RunData> runData;
    private final List<InstanceData> instanceData;

    public PlayerTrialDataS2CP(List<RunData> runData, List<InstanceData> instanceData) {
        this.runData = runData;
        this.instanceData = instanceData;
    }

    public PlayerTrialDataS2CP(FriendlyByteBuf buf) {
        this.runData = buf.readList(RunData.STREAM_CODEC);
        this.instanceData = buf.readList(InstanceData.STREAM_CODEC);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeCollection(runData, RunData.STREAM_CODEC);
        buf.writeCollection(instanceData, InstanceData.STREAM_CODEC);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof LocalPlayer localPlayer){
                    PlayerTrialData.addNewEntry(localPlayer, runData, instanceData);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
