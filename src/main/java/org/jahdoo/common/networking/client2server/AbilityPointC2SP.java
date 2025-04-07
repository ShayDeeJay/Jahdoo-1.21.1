package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.registers.AttachmentReg;

public class AbilityPointC2SP implements CustomPacketPayload {

    public static final Type<AbilityPointC2SP> TYPE = new Type<>(Helpers.res("ability_points"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityPointC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(AbilityPointC2SP::toBytes, AbilityPointC2SP::new);

    private final int points;

    public AbilityPointC2SP(int points) {
        this.points = points;
    }

    public AbilityPointC2SP(FriendlyByteBuf buf) {
        this.points = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(points);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    var casterData = serverPlayer.getData(AttachmentReg.CASTER_DATA);
                    casterData.decrementAbilityPoints(points);
//                    PacketDistributor.sendToPlayer(serverPlayer, new CastingDataSyncS2CP(casterData));
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
