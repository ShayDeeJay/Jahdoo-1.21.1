package org.jahdoo.common.entities.safe;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.ascension.level_manager.InstanceDifficulty;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.registers.EntityReg;
import org.jahdoo.common.registers.SoundReg;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

import java.util.ArrayList;

import static net.minecraft.network.syncher.EntityDataSerializers.FLOAT;
import static net.minecraft.network.syncher.EntityDataSerializers.INT;
import static net.minecraft.network.syncher.SynchedEntityData.defineId;
import static net.minecraft.world.entity.ai.attributes.Attributes.SCALE;
import static org.jahdoo.ascension.loot.RewardLootTables.attachItemData;
import static org.jahdoo.ascension.loot.RewardLootTables.getCompletionLoot;
import static org.jahdoo.ascension.mobs.MobManager.addBaseAttribute;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.common.entities.EntityAnimations.*;
import static software.bernie.geckolib.util.GeckoLibUtil.createInstanceCache;

public class Safe extends LivingEntity implements GeoEntity {

    private final AnimatableInstanceCache geoCache = createInstanceCache(this);
    private static final EntityDataAccessor<Integer> CURRENT_STATE = defineId(Safe.class, INT);
    private static final EntityDataAccessor<Integer> TIME_SINCE_DAMAGED = defineId(Safe.class, INT);
    private static final EntityDataAccessor<Float> DAMAGE_COUNTER = defineId(Safe.class, FLOAT);
    private static final EntityDataAccessor<Integer> DAMAGE_REQUIRED = defineId(Safe.class, INT);

    int requiredTimeBetweenDamage;

    public Safe(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
        int damageRequired1 = 200;
        this.requiredTimeBetweenDamage = 20;
        setDamageRequired(damageRequired1);
        addBaseAttribute(SCALE, this, damageRequired1);
    }

    public Safe(Level level, int setKillMultiplier, int timeBetweenDamage) {
        super(EntityReg.SAFE.get(), level);
        this.requiredTimeBetweenDamage = timeBetweenDamage;
        setDamageRequired(setKillMultiplier);
        addBaseAttribute(SCALE, this, setKillMultiplier);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var animation = new AnimationController<>(this, "Attack", this::flyAnimController);
        controllers.add(animation);
    }

    protected <E extends Safe> PlayState flyAnimController(final AnimationState<Safe> animTest) {
        if (getCurrentState() == 1) {
            return animTest.setAndContinue(Random.nextInt(2) == 0 ? KNOCK_LEFT : KNOCK_RIGHT);
        }

        if (getCurrentState() == 2) {
            return animTest.setAndContinue(OPEN);
        }

        if (getCurrentState() == 3) {
            return animTest.setAndContinue(CLOSE);
        }

        animTest.getController().forceAnimationReset();
        return PlayState.CONTINUE;
    }

    public int getCurrentState() {
        return this.entityData.get(CURRENT_STATE);
    }

    public void setCurrentState(int lifetimes) {
        this.entityData.set(CURRENT_STATE, lifetimes);
    }

    public int getTimeSinceDamaged() {
        return this.entityData.get(TIME_SINCE_DAMAGED);
    }

    public void setTimeSinceDamaged(int time) {
        this.entityData.set(TIME_SINCE_DAMAGED, time);
    }

    public float getDamageCounter() {
        return this.entityData.get(DAMAGE_COUNTER);
    }

    public void setDamageCounter(float value) {
        this.entityData.set(DAMAGE_COUNTER, value);
    }

    public int getDamageRequired() {
        return this.entityData.get(DAMAGE_REQUIRED);
    }

    public void setDamageRequired(int value) {
        this.entityData.set(DAMAGE_REQUIRED, value);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CURRENT_STATE, 0);
        builder.define(TIME_SINCE_DAMAGED, 20);
        builder.define(DAMAGE_COUNTER, 0f);
        builder.define(DAMAGE_REQUIRED, 200);
    }

    @Override
    public Component getName() {
        return Component.empty();
    }

    @Override
    public void kill() {
        this.remove(RemovalReason.KILLED);
    }

    @Override
    public void tick() {
        super.tick();

        if (getCurrentState() == 1) setCurrentState(0);

        if (getCurrentState() >= 2) {
            if (getDamageCounter() == 25) {
                setCurrentState(3);
            }
            if (getDamageCounter() >= 40) {
                this.remove(RemovalReason.KILLED);
            }
            setDamageCounter(getDamageCounter() + 1);
        }

        if (getTimeSinceDamaged() > 0) setTimeSinceDamaged(getTimeSinceDamaged() - 1);

        if (getTimeSinceDamaged() == 0 && getCurrentState() < 2) {
            if (getDamageCounter() > 0) {
                this.playSound(SoundReg.BLOCK.get(), 0.5F, 0.6F);
                this.playSound(SoundReg.REJECT.get(), 1, 1.8F);
            }
            setDamageCounter(0);
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {}

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EMPTY;
    }

    @Override
    public void setDeltaMovement(double x, double y, double z) {
        super.setDeltaMovement(0, 0, 0);
    }

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.AMETHYST_BLOCK_BREAK;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (getCurrentState() < 2) {
            Helpers.getSoundWithPositionV(level(), this.position(), SoundReg.BLOCK.get(), 1, Random.nextFloat(1.4F, (float) Math.max(1.45, getDamageCounter() / 10)));
            Helpers.getSoundWithPositionV(level(), this.position(), SoundEvents.CHAIN_BREAK, 1, Random.nextFloat(1F, 1.2F));
            setCurrentState(1);
            setTimeSinceDamaged(requiredTimeBetweenDamage);
            setDamageCounter(getDamageCounter() + amount);

            if (getDamageCounter() >= getDamageRequired()) {
                setDamageCounter(0);
                Helpers.getSoundWithPositionV(level(), this.position(), SoundReg.IMPACT.get(), 1, 0.6F);
                Helpers.getSoundWithPositionV(level(), this.position(), SoundReg.CRATE_OPEN.get(), 2, 2F);
                setCurrentState(2);
                if (getCurrentState() == 2) {
                    for (int i = 0; i < 10; i++) {
                        if (level() instanceof ServerLevel serverLevel) {
                            var expert = InstanceDifficulty.EXPERT;
                            var rewards = getCompletionLoot(serverLevel, this.position(), expert.getSerializedName(), expert.getId());
                            for (var reward : rewards) {
                                attachItemData(serverLevel, reward, null, expert.getId());
                                Helpers.throwItem(this, reward);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
