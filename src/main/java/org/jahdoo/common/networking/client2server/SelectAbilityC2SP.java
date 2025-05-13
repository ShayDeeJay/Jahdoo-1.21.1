package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.networking.server2client.CastingDataSyncS2CP;
import org.jahdoo.common.registers.AttachmentReg;

import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;

public class SelectAbilityC2SP implements CustomPacketPayload {

    public static final Type<SelectAbilityC2SP> TYPE = new Type<>(Helpers.res("selected_ability"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SelectAbilityC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(SelectAbilityC2SP::toBytes, SelectAbilityC2SP::new);

    String currentAbility;

    public SelectAbilityC2SP(String selectedAbility) {
        this.currentAbility = selectedAbility;
    }

    public SelectAbilityC2SP(FriendlyByteBuf buf) {
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
                    if(!currentAbility.isEmpty()) {
                        castingData.setSelectedAbility(currentAbility);
                    } else {
                        sendToPlayer(serverPlayer, new CastingDataSyncS2CP(castingData));
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
