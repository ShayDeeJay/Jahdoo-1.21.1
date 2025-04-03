package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.registers.AttachmentReg;

public class SelectedAbilityS2CP implements CustomPacketPayload {

    public static final Type<SelectedAbilityS2CP> TYPE = new Type<>(Helpers.res("selected_ability_s2c"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SelectedAbilityS2CP> STREAM_CODEC =
        CustomPacketPayload.codec(SelectedAbilityS2CP::toBytes, SelectedAbilityS2CP::new);

    private final String ability;

    public SelectedAbilityS2CP(String ability) {
        this.ability = ability;
    }

    public SelectedAbilityS2CP(FriendlyByteBuf buf) {
        this.ability = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(ability);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof LocalPlayer localPlayer){
                    var casterData = localPlayer.getData(AttachmentReg.CASTER_DATA);
                    casterData.setSelectedAbility(ability);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
