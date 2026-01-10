package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.networking.server2client.CastingDataSyncS2CP;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.utils.Helpers;


public class RegretAbilitiesC2SP implements CustomPacketPayload {

    public static final Type<RegretAbilitiesC2SP> TYPE = new Type<>(Helpers.res("regret_abilities"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RegretAbilitiesC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(RegretAbilitiesC2SP::toBytes, RegretAbilitiesC2SP::new);

    public RegretAbilitiesC2SP() {}

    public RegretAbilitiesC2SP(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    var casterData = serverPlayer.getData(AttachmentReg.CASTER_DATA);
                    CasterData.regretAbilities(serverPlayer, true);
                    PacketDistributor.sendToPlayer(serverPlayer, new CastingDataSyncS2CP(casterData));
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
