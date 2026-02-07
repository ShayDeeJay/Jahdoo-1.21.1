package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.AttachmentReg;

import java.util.List;

public class AbilityHolderS2CP implements CustomPacketPayload {

    public static final Type<AbilityHolderS2CP> TYPE = new Type<>(JahdooHelpers.res("sync_all_ability_holder"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityHolderS2CP> STREAM_CODEC =
        CustomPacketPayload.codec(AbilityHolderS2CP::toBytes, AbilityHolderS2CP::new);

    private final List<AbilityHolder> abilityHolder;

    public AbilityHolderS2CP(List<AbilityHolder> abilityHolder) {
        this.abilityHolder = abilityHolder;
    }

    public AbilityHolderS2CP(FriendlyByteBuf buf) {
        this.abilityHolder = buf.readList(AbilityHolder.STREAM_CODEC);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeCollection(this.abilityHolder, AbilityHolder.STREAM_CODEC);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof LocalPlayer localPlayer){
                    var casterData = localPlayer.getData(AttachmentReg.CASTER_DATA);
                    casterData.syncHolders(this.abilityHolder);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
