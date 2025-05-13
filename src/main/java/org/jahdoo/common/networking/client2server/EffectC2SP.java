package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.trial_nexus.ability.effects.JahdooMobEffect;
import org.jahdoo.trial_nexus.utils.Helpers;

public class EffectC2SP implements CustomPacketPayload {

    public static final Type<EffectC2SP> TYPE = new Type<>(Helpers.res("effect_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EffectC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(EffectC2SP::toBytes, EffectC2SP::new);

    private JahdooMobEffect effect;

    public EffectC2SP(JahdooMobEffect effect) { this.effect = effect; }

    public EffectC2SP(FriendlyByteBuf buf) {
        var instance = buf.readJsonWithCodec(MobEffectInstance.CODEC);
        this.effect = new JahdooMobEffect(instance.getEffect(), instance.getDuration(), instance.getAmplifier());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(MobEffectInstance.CODEC, this.effect);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(!(ctx.player() instanceof ServerPlayer player)) return;
                player.addEffect(effect);
            }

        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

}
