package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;


public class RegretAbilitiesC2SP implements CustomPacketPayload {

    public static final Type<RegretAbilitiesC2SP> TYPE = new Type<>(JahdooHelpers.res("regret_abilities"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RegretAbilitiesC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(RegretAbilitiesC2SP::toBytes, RegretAbilitiesC2SP::new);

    boolean playRegretSound;

    public RegretAbilitiesC2SP(boolean playRegretSound) {
        this.playRegretSound = playRegretSound;
    }

    public RegretAbilitiesC2SP(FriendlyByteBuf buf) {
        this.playRegretSound = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(playRegretSound);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    var casterData = serverPlayer.getData(AttachmentReg.CASTER_DATA);
                    CasterData.regretAbilities(serverPlayer, playRegretSound);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
