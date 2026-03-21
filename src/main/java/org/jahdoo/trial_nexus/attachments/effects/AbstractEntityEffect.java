package org.jahdoo.trial_nexus.attachments.effects;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jahdoo.trial_nexus.attachments.IAttachment;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Supplier;

public abstract class AbstractEntityEffect implements IAttachment {

    private int maxTime;
    private int timer;
    private float damage;
    private double secondaryValue;
    private boolean isSecondary;
    private String applierUUID = "";

    public abstract String id();
    public abstract AttachmentType<AbstractEntityEffect> getAttachment();
    public abstract ResourceLocation icon();

    public void greaterEffect(LivingEntity entity) {}
    public void onStarted(LivingEntity entity) {}
    public void onActive(LivingEntity entity) {}
    public void onEnd(LivingEntity entity) {}
    public void ownerTick(LivingEntity entity) {}

    public boolean isActive() {
        return timer > 0;
    }

    public boolean started() {
        return timer == maxTime;
    }

    public boolean isEnding() {
        return timer == 1;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public int getTimer() {
        return timer;
    }

    public double getSecondaryValue() {
        return secondaryValue;
    }

    public float getDamage() {
        return damage;
    }

    public boolean isSecondary() {
        return isSecondary;
    }

    public String getApplierUUID() {
        return applierUUID;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public void setSecondaryValue(double secondaryValue) {
        this.secondaryValue = secondaryValue;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public void setSecondary(boolean secondary) {
        isSecondary = secondary;
    }

    public void setApplierUUID(String applierUUID) {
        this.applierUUID = applierUUID;
    }

    public static void setTypeEffect(Supplier<AbstractEntityEffect> getEffect, @Nullable LivingEntity applier, LivingEntity target, boolean isSecondary, int time, float damage, double secondaryValue) {
        if(target.level().isClientSide) return;

        var effect = getEffect.get();
        var type = effect.getAttachment();
        if(!target.hasData(type)) {
            effect.createEffect(applier, isSecondary, time, damage, secondaryValue);
            target.setData(type, effect);
        }
    }

    public static void serverOnTick(LivingEntity livingEntity, AttachmentType<AbstractEntityEffect> typeEffect) {
        if (livingEntity.hasData(typeEffect)) {
            livingEntity.getData(typeEffect).onTick(livingEntity);
        }
    }

    public boolean avoidOwner(LivingEntity livingEntity) {
        return !livingEntity.getUUID().toString().equals(applierUUID);
    }

    public void onTick(LivingEntity entity) {
        if(!this.isActive() || !entity.isAlive()) {
            entity.removeData(getAttachment());
            onEnd(entity);
            return;
        }

        if (isSecondary()) greaterEffect(entity);
        if (started()) onStarted(entity);
        onActive(entity);

        if(entity.level() instanceof ServerLevel serverLevel) {
            var owner = this.getOwner(serverLevel);
            if(owner != null) ownerTick(owner);
        }

        timer--;
    }

    public void doDamage(LivingEntity target, DamageSource source, float amount) {
        target.hurt(source, amount);
    }

    public void createEffect(@Nullable LivingEntity applier, boolean secondary, int maxTime, float damage, double secondaryValue) {
        setTimer(maxTime);
        setMaxTime(maxTime);
        setDamage(damage);
        setSecondary(secondary);
        setSecondaryValue(secondaryValue);
        if(applier != null) setApplierUUID(applier.getStringUUID());
    }

    public @Nullable LivingEntity getOwner(ServerLevel level) {
        var entity = level.getEntity(UUID.fromString(applierUUID));
        return entity instanceof LivingEntity living ? living : null;
    }

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putInt("maxTime", maxTime);
        nbt.putInt("timer", timer);
        nbt.putDouble("secondaryValue", secondaryValue);
        nbt.putFloat("damage", damage);
        nbt.putBoolean("isSecondary", isSecondary);
        nbt.putString("applierUUID", applierUUID);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        setMaxTime(nbt.getInt("maxTime"));
        setTimer(nbt.getInt("timer"));
        setSecondaryValue(nbt.getDouble("secondaryValue"));
        setDamage(nbt.getFloat("damage"));
        setSecondary(nbt.getBoolean("isSecondary"));
        setApplierUUID(nbt.getString("applierUUID"));
    }

    public static <T extends AbstractEntityEffect> StreamCodec<RegistryFriendlyByteBuf, T> streamCodec(java.util.function.Supplier<T> factory) {
        return StreamCodec.of(
            (buf, effect) -> {
                buf.writeInt(effect.getMaxTime());
                buf.writeInt(effect.getTimer());
                buf.writeDouble(effect.getSecondaryValue());
                buf.writeFloat(effect.getDamage());
                buf.writeBoolean(effect.isSecondary());
                buf.writeUtf(effect.getApplierUUID());
            },
            buf -> {
                T effect = factory.get();
                effect.setMaxTime(buf.readInt());
                effect.setTimer(buf.readInt());
                effect.setSecondaryValue(buf.readDouble());
                effect.setDamage(buf.readFloat());
                effect.setSecondary(buf.readBoolean());
                effect.setApplierUUID(buf.readUtf());
                return effect;
            }
        );
    }

}