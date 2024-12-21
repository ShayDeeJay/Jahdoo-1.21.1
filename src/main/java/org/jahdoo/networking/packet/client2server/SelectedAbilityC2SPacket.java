package org.jahdoo.networking.packet.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.components.DataComponentHelper;

public class SelectedAbilityC2SPacket implements CustomPacketPayload {
    public static final Type<SelectedAbilityC2SPacket> TYPE = new Type<>(ModHelpers.res("selected_ability"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SelectedAbilityC2SPacket> STREAM_CODEC = CustomPacketPayload.codec(SelectedAbilityC2SPacket::toBytes, SelectedAbilityC2SPacket::new);

    String currentAbility;

    public SelectedAbilityC2SPacket(String selectedAbility) {
        this.currentAbility = selectedAbility;
    }

    public SelectedAbilityC2SPacket(FriendlyByteBuf buf) {
        this.currentAbility = buf.readUtf() ;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.currentAbility);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    if(serverPlayer.getMainHandItem().getItem() instanceof WandItem){
                        if(currentAbility != null){
                            DataComponentHelper.setAbilityTypeWand(serverPlayer, this.currentAbility);
                        }
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
