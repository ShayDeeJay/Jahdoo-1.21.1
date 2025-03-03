package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.common.items.wand.WandItem;
import org.jahdoo.common.registers.AbilityReg;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.DataComponentHelper;

public class UseAbilityC2SP implements CustomPacketPayload {

    public static final Type<UseAbilityC2SP> TYPE = new Type<>(Helpers.res("use_ability"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UseAbilityC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(UseAbilityC2SP::toBytes, UseAbilityC2SP::new);

    public UseAbilityC2SP(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf bug) {}

    private void invokeSelectedAbility(Player player){
        AbilityRegistrar ability = AbilityReg.REGISTRY.get(DataComponentHelper.getAbilityTypeWand(player));
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
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

}
