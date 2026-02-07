package org.jahdoo.common.entities.safe;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jahdoo.common.registers.EntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.jetbrains.annotations.Nullable;
import org.shaydee.shaydeeapi.Helpers;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

import java.util.ArrayList;

import static net.minecraft.network.syncher.EntityDataSerializers.*;
import static net.minecraft.network.syncher.SynchedEntityData.defineId;
import static net.minecraft.world.entity.ai.attributes.Attributes.SCALE;
import static org.jahdoo.common.entities.EntityAnimations.*;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.level_manager.InstanceDifficulty.NOVICE;
import static org.jahdoo.trial_nexus.level_manager.InstanceDifficulty.getFromName;
import static org.jahdoo.trial_nexus.loot.RewardLootTables.attachItemData;
import static org.jahdoo.trial_nexus.loot.RewardLootTables.getCompletionLoot;
import static org.jahdoo.trial_nexus.mobs.MobSpawnManager.addBaseAttribute;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;
import static software.bernie.geckolib.util.GeckoLibUtil.createInstanceCache;

public class Safe extends LivingEntity implements GeoEntity {

    private final AnimatableInstanceCache geoCache = createInstanceCache(this);
    private static final EntityDataAccessor<Integer> CURRENT_STATE = defineId(Safe.class, INT);
    private static final EntityDataAccessor<Integer> TIME_SINCE_DAMAGED = defineId(Safe.class, INT);
    private static final EntityDataAccessor<Float> DAMAGE_COUNTER = defineId(Safe.class, FLOAT);
    private static final EntityDataAccessor<Integer> DAMAGE_REQUIRED = defineId(Safe.class, INT);
    private static final EntityDataAccessor<Long> TIME_SINCE_SPAWNED = defineId(Safe.class, LONG);

    int requiredTimeBetweenDamage = 15;

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
        setTimeSinceSpawned(level.getGameTime());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        var animation = new AnimationController<>(this, "Attack", this::flyAnimController);
        controllers.add(animation);
    }

    protected PlayState flyAnimController(final AnimationState<Safe> animTest) {
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

    public int getTimer(){
        var currentTimePassed = level().getGameTime() - getTimeSinceSpawned();
        return Math.max(0, (int) (600 - currentTimePassed));
    }

    public long getTimeSinceSpawned() {
        return this.entityData.get(TIME_SINCE_SPAWNED);
    }

    public void setTimeSinceSpawned(long value) {
        this.entityData.set(TIME_SINCE_SPAWNED, value);
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
        builder.define(TIME_SINCE_SPAWNED, 0L);
    }

    @Override
    public Component getName() {
        return Component.empty();
    }

    public void sharedSound(SoundEvent sEvent, Float volume, Float pitch){
        Helpers.getSoundWithPosition(level(), this.position(), sEvent, SoundSource.NEUTRAL, volume, pitch);
    }

    @Override
    public void kill() {

        sharedSound(SoundReg.REJECT.get(), 1F, 1F);
        sharedSound(SoundReg.ORB_CREATE.get(), 1F, 2F);

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

        if(getTimer() == 0 && getCurrentState() < 2){
           kill();
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
        super.setDeltaMovement(0, -1, 0);
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
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return false;
    }

    @Override
    protected void onEffectAdded(MobEffectInstance effectInstance, @Nullable Entity entity) {
        super.onEffectAdded(effectInstance, entity);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.AMETHYST_BLOCK_BREAK;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (getCurrentState() < 2 && source.getEntity() instanceof Player player) {

            sharedSound(SoundReg.BLOCK.get(), 1F, Random.nextFloat(0.8F, Math.max(0.85F, getDamageCounter() / 10)));
            sharedSound(SoundEvents.CHAIN_BREAK, 1F, Random.nextFloat(1F, 1.2F));

            setCurrentState(1);
            setTimeSinceDamaged(requiredTimeBetweenDamage);
            setDamageCounter(getDamageCounter() + amount);

            if (getDamageCounter() >= getDamageRequired()) {
                setDamageCounter(0);
                sharedSound(SoundReg.IMPACT.get(), 1F, 0.6F);
                sharedSound(SoundReg.CRATE_OPEN.get(), 2F, 2F);
                setCurrentState(2);
                if (getCurrentState() == 2) {
                    if (level() instanceof ServerLevel serverLevel) {
                        var hasInstanceData = serverLevel.hasData(INSTANCE_DATA.get());
                        var getInstanceDifficulty = serverLevel.getData(INSTANCE_DATA.get());
                        var difficulty = hasInstanceData ? getFromName(getInstanceDifficulty.getDifficulty()) : NOVICE;
                        var instanceLootMultiplier = Math.max(1, getInstanceDifficulty.getSafeMultiplier());
                        if(source.getEntity() instanceof ServerPlayer serverPlayer){
                            RunData.addExperienceToTotal((difficulty.getId() * 10) * difficulty.expMultiplier(), serverPlayer);
                            RunData.incrementSafeOpened(player);
                        }
                        for (int i = 0; i < instanceLootMultiplier; i++) {
                            var rewards = getCompletionLoot(serverLevel, this.position(), difficulty.getSerializedName(), difficulty.getId());
                            for (var reward : rewards) {
                                attachItemData(serverLevel, reward, null, difficulty.getId());
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
