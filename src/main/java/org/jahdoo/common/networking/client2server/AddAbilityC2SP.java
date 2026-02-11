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

public class AddAbilityC2SP implements CustomPacketPayload {

    public static final Type<AddAbilityC2SP> TYPE = new Type<>(JahdooHelpers.res("add_ability"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AddAbilityC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(AddAbilityC2SP::toBytes, AddAbilityC2SP::new);

    String currentAbility;

    public AddAbilityC2SP(String selectedAbility) {
        this.currentAbility = selectedAbility;
    }

    public AddAbilityC2SP(FriendlyByteBuf buf) {
        this.currentAbility = buf.readUtf() ;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.currentAbility);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    var castingData = serverPlayer.getData(AttachmentReg.CASTER_DATA);
                    castingData.addAbilitySlot(currentAbility);
                    CasterData.sharedPackets(serverPlayer, castingData);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
