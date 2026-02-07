package org.jahdoo.common.networking.client2server;

import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.ability.effects.JahdooMobEffect;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

public class MobEffectC2SP implements CustomPacketPayload {

    public static final Type<MobEffectC2SP> TYPE = new Type<>(JahdooHelpers.res("sync_mob_effect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MobEffectC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(MobEffectC2SP::toBytes, MobEffectC2SP::new);

    Holder<MobEffect> effectHolder;

    public MobEffectC2SP(Holder<MobEffect> effectHolder) {
        this.effectHolder = effectHolder;
    }

    public MobEffectC2SP(FriendlyByteBuf buf) {
        this.effectHolder = buf.readJsonWithCodec(MobEffect.CODEC) ;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(MobEffect.CODEC, effectHolder);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    if(serverPlayer.hasEffect(effectHolder)){
                        serverPlayer.removeEffect(effectHolder);
                    } else {
                        serverPlayer.addEffect(new JahdooMobEffect(effectHolder, MobEffectInstance.INFINITE_DURATION, 1, true));
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
