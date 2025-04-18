package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.networking.server2client.PlayerTrialDataS2CP;
import org.jahdoo.common.registers.AttachmentReg;

public class PlayerTrialDataC2SP implements CustomPacketPayload {

    public static final Type<PlayerTrialDataC2SP> TYPE = new Type<>(Helpers.res("sync_player_trial_data_client"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerTrialDataC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(PlayerTrialDataC2SP::toBytes, PlayerTrialDataC2SP::new);

    int index;

    public PlayerTrialDataC2SP(int index) {
        this.index = index;
    }

    public PlayerTrialDataC2SP(FriendlyByteBuf buf) {
        index = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeInt(index);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    var type = AttachmentReg.PLAYER_TRIAL_DATA.get();
                    var trialData = serverPlayer.getData(type);

                    if(index >= 0){
                        trialData.getInstanceData().remove(index);
                        trialData.getPastRuns().remove(index);
                    }

                    PacketDistributor.sendToPlayer(serverPlayer, new PlayerTrialDataS2CP(trialData));
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

}
