package org.jahdoo.trial_nexus.attachments.effects;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jahdoo.trial_nexus.attachments.IAttachment;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public abstract class AbstractEntityEffect implements IAttachment {

    private int maxTime;
    private int timer;
    private int random;
    private float damage;
    private boolean isSecondary;

    public abstract AbstractElement getElement();

    public abstract void getSecondary(LivingEntity entity);

    public abstract AttachmentType<?> getAttachment();

    public abstract ResourceLocation icon();

    public void createEffect(LivingEntity applier, boolean isPrimary, int maxTime, float damage) {
        setTimer(maxTime);
        setMaxTime(maxTime);
        setDamage(applier, damage);
        setSecondary(!isPrimary);
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

    public boolean ended() {
        return timer <= 1;
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

    public void onTick(LivingEntity entity) {
        if(ended() || !entity.isAlive()) entity.removeData(getAttachment());
        if (timer > 0) timer--;
        if (isSecondary()) this.getSecondary(entity);
    }

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putInt("maxTime", maxTime);
        nbt.putInt("timer", timer);
        nbt.putInt("random", random);
        nbt.putFloat("damage", damage);
        nbt.putBoolean("isSecondary", isSecondary);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        this.maxTime = nbt.getInt("maxTime");
        this.timer = nbt.getInt("timer");
        this.random = nbt.getInt("random");
        this.damage = nbt.getFloat("damage");
        this.isSecondary = nbt.getBoolean("isSecondary");
    }

}
