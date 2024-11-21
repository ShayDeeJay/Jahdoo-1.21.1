package org.jahdoo.networking.packet.server2client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.ability.effects.CustomMobEffect;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.utils.ModHelpers;

public class EffectSyncS2CPacket implements CustomPacketPayload {
    public static final Type<EffectSyncS2CPacket> TYPE = new Type<>(ModHelpers.res("get_entity"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EffectSyncS2CPacket> STREAM_CODEC = CustomPacketPayload.codec(EffectSyncS2CPacket::toBytes, EffectSyncS2CPacket::new);

    int id;
    int duration;
    int amp;

    public EffectSyncS2CPacket(int id, int duration, int amp) {
        this.id = id;
        this.duration = duration;
        this.amp = amp;
    }

    public EffectSyncS2CPacket(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        this.duration = buf.readInt();
        this.amp = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf byteBuf) {
        byteBuf.writeInt(id);
        byteBuf.writeInt(duration);
        byteBuf.writeInt(amp);
    }

    public boolean handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    var level = Minecraft.getInstance().level;
                    if(level != null) {
                        var entity = level.getEntity(id);
                        if(entity instanceof LivingEntity livingEntity) {
                            livingEntity.addEffect(new CustomMobEffect(EffectsRegister.ARCANE_EFFECT, duration, amp));
                        }
                    }
                }
            }
        );
        return true;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
