package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.networking.server2client.QuestTrackerS2CP;
import org.jahdoo.trial_nexus.attachments.QuestTracker;
import org.jahdoo.trial_nexus.utils.Helpers;

public class QuestTrackerC2SP implements CustomPacketPayload{
    public static final Type<QuestTrackerC2SP> TYPE = new Type<>(Helpers.res("sync_quest_tracker_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, QuestTrackerC2SP> STREAM_CODEC = CustomPacketPayload.codec(QuestTrackerC2SP::toBytes, QuestTrackerC2SP::new);

    private final String tag;

    public QuestTrackerC2SP(String tag) {
        this.tag = tag;
    }

    public QuestTrackerC2SP(FriendlyByteBuf buf) {
        this.tag = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeUtf(this.tag);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    QuestTracker.addClaimedQuest(serverPlayer, tag);
                    PacketDistributor.sendToPlayer(serverPlayer, new QuestTrackerS2CP(QuestTracker.getQuestTracker(serverPlayer)));
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
