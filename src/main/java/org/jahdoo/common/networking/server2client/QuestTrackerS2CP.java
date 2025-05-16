package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.trial_nexus.attachments.QuestTracker;
import org.jahdoo.trial_nexus.utils.Helpers;

public class QuestTrackerS2CP implements CustomPacketPayload{
    public static final Type<QuestTrackerS2CP> TYPE = new Type<>(Helpers.res("sync_quest_tracker_data_client"));
    public static final StreamCodec<RegistryFriendlyByteBuf, QuestTrackerS2CP> STREAM_CODEC = CustomPacketPayload.codec(QuestTrackerS2CP::toBytes, QuestTrackerS2CP::new);

    private final QuestTracker questTracker;

    public QuestTrackerS2CP(QuestTracker questTracker) {
        this.questTracker = questTracker;
    }

    public QuestTrackerS2CP(FriendlyByteBuf buf) {
        this.questTracker = buf.readJsonWithCodec(QuestTracker.CODEC);
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeJsonWithCodec(QuestTracker.CODEC, questTracker);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof LocalPlayer localPlayer){
                    localPlayer.setData(AttachmentReg.QUEST_TRACKER_DATA, questTracker);
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
