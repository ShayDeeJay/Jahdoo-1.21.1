package org.jahdoo.networking.packet.client2server;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ability.AbilityRegistrar;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.components.DataComponentHelper;

public class UseAbilityC2SPacket implements CustomPacketPayload {
    public static final Type<UseAbilityC2SPacket> TYPE = new Type<>(ModHelpers.res("use_ability"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UseAbilityC2SPacket> STREAM_CODEC = CustomPacketPayload.codec(UseAbilityC2SPacket::toBytes, UseAbilityC2SPacket::new);

    public UseAbilityC2SPacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf bug) {}

    private void invokeSelectedAbility(Player player){
        AbilityRegistrar ability = AbilityRegister.REGISTRY.get(DataComponentHelper.getAbilityTypeWand(player));
        if(ability != null) ability.invokeAbility(player);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    if( ctx.player().getItemInHand(ctx.player().getUsedItemHand()).getItem() instanceof WandItem){
                        this.invokeSelectedAbility(serverPlayer);
                    }
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}
