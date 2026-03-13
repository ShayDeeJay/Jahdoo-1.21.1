package org.jahdoo.trial_nexus.attachments.effects;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jahdoo.trial_nexus.attachments.IAttachment;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.DamageUtils;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jetbrains.annotations.Nullable;
import org.shaydee.shaydeeapi.helpers.MathHelpers;

import java.util.UUID;
import java.util.function.Supplier;

import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public abstract class AbstractEntityEffect implements IAttachment {

    private int maxTime;
    private int timer;
    private int random;
    private float damage;
    private boolean isSecondary;
    private String applierUUID = "";

    public abstract String id();

    public abstract AbstractElement getElement();

    public abstract void greaterEffect(LivingEntity entity);

    public abstract AttachmentType<AbstractEntityEffect> getAttachment();

    public abstract ResourceLocation icon();

    public void onStarted(LivingEntity livingEntity){};

    public void onActive(LivingEntity livingEntity){};

    public void onEnd(LivingEntity livingEntity){};

    public void ownerTick(LivingEntity livingEntity){};

    public void createEffect(@Nullable LivingEntity applier, boolean greater, int maxTime, float damage) {
        setTimer(maxTime);
        setMaxTime(maxTime);
        setDamage(applier, damage);
        if(applier != null) setApplierUUID(applier.getStringUUID());
        setSecondary(greater);
        setRandom(Random.nextInt(0, 2000));
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getTimer() {
        return timer;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public boolean isActive() {
        return timer > 0;
    }

    public int getRandom() {
        return random;
    }

    public boolean started() {
        return timer == maxTime;
    }

    public boolean isEnding() {
        return timer == 1;
    }

    public void setSecondary(boolean secondary) {
        isSecondary = secondary;
    }

    public boolean isSecondary() {
        return isSecondary;
    }

    public float getDamage() {
        return this.damage;
    }

    public void setRandom(int random) {
        this.random = random;
    }

    public void setApplierUUID(String applierUUID) {
        this.applierUUID = applierUUID;
    }

    public String getApplierUUID() {
        return applierUUID;
    }

    public boolean avoidOwner(LivingEntity livingEntity) {
        return !livingEntity.getUUID().toString().equals(applierUUID);
    }

    public @Nullable LivingEntity getOwner(ServerLevel level) {
        var entity = level.getEntity(UUID.fromString(applierUUID));
        if(entity instanceof ServerPlayer serverPlayer)
            return serverPlayer;

        return null;
    }

    public void doDamage(LivingEntity target, ServerLevel serverLevel) {
        DamageUtils.damageWithJahdoo(target, getOwner(serverLevel), getDamage(), getElement().damageTypeResourceKey());
    }

    public void doDamage(LivingEntity target, ServerLevel serverLevel, double damage) {
        DamageUtils.damageWithJahdoo(target, getOwner(serverLevel), damage, getElement().damageTypeResourceKey());
    }

    public void setDamage(LivingEntity livingEntity, float damage) {
        if(livingEntity instanceof Player player) {
            this.damage = JahdooHelpers.attributeModifierCalculator(
                player,
                damage,
                true,
                getElement().damageAmplifier(),
                MAGIC_DAMAGE_MULTIPLIER
            );
        } else {
            this.damage = damage;
        }
    }

    public static void setTypeEffect(Supplier<AbstractEntityEffect> getEffect, @Nullable LivingEntity applier, LivingEntity target, boolean isSecondary, int time, float damage) {
        if(target.level().isClientSide) return;

        var effect = getEffect.get();
        if(!target.hasData(effect.getAttachment())) {
            effect.createEffect(applier, isSecondary, time, damage);
            target.setData(effect.getAttachment(), effect);
        }
    }

    public static void setTypeEffect(Supplier<AbstractEntityEffect> getEffect, @Nullable LivingEntity applier, LivingEntity target, boolean isSecondary, int time, float damage, double applyChance) {
        if(!MathHelpers.percentageChance(applyChance) || target.level().isClientSide) return;

        var effect = getEffect.get();
        if(!target.hasData(effect.getAttachment())) {
            effect.createEffect(applier, isSecondary, time, damage);
            target.setData(effect.getAttachment(), effect);
        }
    }

    public static void serverOnTick(LivingEntity livingEntity, AttachmentType<AbstractEntityEffect> mysticEffect) {
        if(livingEntity.hasData(mysticEffect)) {
            var mystic = livingEntity.getData(mysticEffect);
            mystic.onTick(livingEntity);
        }
    }

    public void onTick(LivingEntity entity) {

        if(!this.isActive() || !entity.isAlive()) {
            entity.removeData(getAttachment());
            onEnd(entity);
        }

        if (isSecondary()) this.greaterEffect(entity);
        if(this.started()) onStarted(entity);
        if(this.isActive()) onActive(entity);

        if(entity.level() instanceof ServerLevel serverLevel) {
            var owner = this.getOwner(serverLevel);
            if(owner != null) ownerTick(owner);
        }

        if (timer > 0) timer--;
    }

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putInt("maxTime", maxTime);
        nbt.putInt("timer", timer);
        nbt.putInt("random", random);
        nbt.putFloat("damage", damage);
        nbt.putBoolean("isSecondary", isSecondary);
        nbt.putString("applierUUID", applierUUID);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        this.maxTime = nbt.getInt("maxTime");
        this.timer = nbt.getInt("timer");
        this.random = nbt.getInt("random");
        this.damage = nbt.getFloat("damage");
        this.isSecondary = nbt.getBoolean("isSecondary");
        this.applierUUID = nbt.getString("applierUUID");
    }

    public static <T extends AbstractEntityEffect> StreamCodec<RegistryFriendlyByteBuf, T> streamCodec(java.util.function.Supplier<T> factory) {
        return StreamCodec.of(
            (buf, effect) -> {
                buf.writeInt(effect.getMaxTime());
                buf.writeInt(effect.getTimer());
                buf.writeInt(effect.getRandom());
                buf.writeFloat(effect.getDamage());
                buf.writeBoolean(effect.isSecondary());
                buf.writeUtf(effect.getApplierUUID());
            },
            buf -> {
                T effect = factory.get();
                effect.setMaxTime(buf.readInt());
                effect.setTimer(buf.readInt());
                effect.setRandom(buf.readInt());
                effect.setDamage(null, buf.readFloat());
                effect.setSecondary(buf.readBoolean());
                String uuid = buf.readUtf();
                effect.setApplierUUID(uuid);
                return effect;
            }
        );
    }

}
