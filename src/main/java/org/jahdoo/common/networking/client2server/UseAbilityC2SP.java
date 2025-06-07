package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.items.caster_item.CastHelper;
import org.jahdoo.trial_nexus.utils.Helpers;

import static net.minecraft.world.InteractionHand.MAIN_HAND;
import static net.minecraft.world.InteractionHand.OFF_HAND;

public class UseAbilityC2SP implements CustomPacketPayload {

    public static final Type<UseAbilityC2SP> TYPE = new Type<>(Helpers.res("use_ability"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UseAbilityC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(UseAbilityC2SP::toBytes, UseAbilityC2SP::new);

    public UseAbilityC2SP() {}

    public UseAbilityC2SP(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf bug) {}

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    var mainHand = serverPlayer.getMainHandItem().getItem();
                    var offHand = serverPlayer.getOffhandItem().getItem();

                    InteractionHand hand = null;

                    if(CastHelper.validCasterType(mainHand)) hand = MAIN_HAND;
                    if(CastHelper.validCasterType(offHand)) hand = OFF_HAND;

                    var item = serverPlayer.getItemInHand(hand);
                    item.use(serverPlayer.level(), serverPlayer, hand);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

}
