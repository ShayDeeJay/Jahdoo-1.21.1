package org.jahdoo.common.networking.server2client;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.trial_nexus.attachments.effects.AbstractEntityEffect;
import org.jahdoo.trial_nexus.attachments.effects.MysticEffect;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

public class SyncEntityEffectValuesS2CP implements CustomPacketPayload {

    public static final Type<SyncEntityEffectValuesS2CP> TYPE =
        new Type<>(JahdooHelpers.res("sync_entity_effect_values"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncEntityEffectValuesS2CP> STREAM_CODEC =
        CustomPacketPayload.codec(SyncEntityEffectValuesS2CP::write, SyncEntityEffectValuesS2CP::new);

    private final int entityId;
    private final int maxTime;
    private final int timer;
    private final int random;
    private final float damage;
    private final boolean isSecondary;

    public SyncEntityEffectValuesS2CP(LivingEntity entity, AbstractEntityEffect effect) {
        this.entityId = entity.getId();
        this.maxTime = effect.getMaxTime();
        this.timer = effect.getTimer();
        this.random = effect.getRandom();
        this.damage = effect.getDamage();
        this.isSecondary = effect.isSecondary();
    }

    public SyncEntityEffectValuesS2CP(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.maxTime = buf.readInt();
        this.timer = buf.readInt();
        this.random = buf.readInt();
        this.damage = buf.readFloat();
        this.isSecondary = buf.readBoolean();
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(maxTime);
        buf.writeInt(timer);
        buf.writeInt(random);
        buf.writeFloat(damage);
        buf.writeBoolean(isSecondary);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if (!(ctx.player().level().getEntity(entityId) instanceof LivingEntity entity)) return;

                var mystic = new MysticEffect();
                mystic.setTimer(timer);
                mystic.setMaxTime(maxTime);
                mystic.setDamage(null, damage);
                mystic.setSecondary(isSecondary);
                mystic.setRandom(random);

                if(mystic.ended()){
                    entity.removeData(mystic.getAttachment());
                } else {
                    entity.setData(AttachmentReg.MYSTIC_EFFECT, mystic);
                }
            }
        );
    }
}
