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

public class RemoveAbilityC2SP implements CustomPacketPayload {

    public static final Type<RemoveAbilityC2SP> TYPE = new Type<>(JahdooHelpers.res("remove_ability"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveAbilityC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(RemoveAbilityC2SP::toBytes, RemoveAbilityC2SP::new);

    String currentAbility;

    public RemoveAbilityC2SP(String selectedAbility) {
        this.currentAbility = selectedAbility;
    }

    public RemoveAbilityC2SP(FriendlyByteBuf buf) {
        this.currentAbility = buf.readUtf() ;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.currentAbility);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    if(!this.currentAbility.isEmpty()){
                        var castingData = serverPlayer.getData(AttachmentReg.CASTER_DATA);
                        castingData.removeAbilitySlot(currentAbility);
                        CasterData.sharedPackets(serverPlayer, castingData);
                    }
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
