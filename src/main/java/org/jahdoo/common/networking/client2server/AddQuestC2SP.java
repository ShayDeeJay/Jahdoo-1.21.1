package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.attachments.RunData;
import org.jahdoo.ascension.utils.Helpers;

public class AddQuestC2SP implements CustomPacketPayload {

    public static final Type<AddQuestC2SP> TYPE = new Type<>(Helpers.res("sync_run_data_client"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AddQuestC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(AddQuestC2SP::toBytes, AddQuestC2SP::new);

    private final String questId;

    public AddQuestC2SP(String questId) {
        this.questId = questId;
    }

    public AddQuestC2SP(FriendlyByteBuf buf) {
        this.questId = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(questId);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){;
                    RunData.addNewQuest(serverPlayer, questId);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
