package org.jahdoo.common.entities.decoy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jahdoo.ascension.ability.abilities.EscapeDecoyAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.EntityReg;
import org.jahdoo.common.registers.SoundReg;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.network.syncher.EntityDataSerializers.FLOAT;
import static net.minecraft.network.syncher.EntityDataSerializers.INT;
import static net.minecraft.network.syncher.SynchedEntityData.Builder;
import static net.minecraft.network.syncher.SynchedEntityData.defineId;
import static net.minecraft.util.RandomSource.create;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.common.registers.ElementReg.vitality;

public class Decoy extends Mob {

    private static final EntityDataAccessor<Float> SCALE = defineId(Decoy.class, FLOAT);
    private static final EntityDataAccessor<Integer> MAX_LIFETIME = defineId(Decoy.class, INT);
    private Player player;
    private int range;

    public Decoy(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Decoy(Level pLevel, Player player, int range) {
        super(EntityReg.DECOY.get(), pLevel);
        this.player = player;
        this.range = range;
    }

    @Override
    protected void pickUpItem(ItemEntity pItemEntity) {}

    private AbstractElement getElement(){
        return vitality();
    }

    public float getScale() {
        return this.entityData.get(SCALE);
    }

    public void setScale(float getSelectedAbility) {
        this.entityData.set(SCALE, getSelectedAbility);
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(200);
    }

    public int getMaxLifetime() {
        return this.entityData.get(MAX_LIFETIME);
    }

    public void setMaxLifetime(int getSelectedAbility) {
        this.entityData.set(MAX_LIFETIME, getSelectedAbility);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.EMPTY;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.EMPTY;
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


    private void onAttract(Mob mob) {
        mob.setTarget(this);
    }

    @Override
    protected void onEffectAdded(MobEffectInstance effectInstance, @Nullable Entity entity) {
        super.onEffectAdded(effectInstance, entity);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Helpers.getSoundWithPosition(level(), this.blockPosition(), SoundEvents.ELDER_GUARDIAN_HURT, 1, 1.8f);
        Helpers.getSoundWithPosition(level(), this.blockPosition(), SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, 1, 1.8f);
        return false;
    }

    @Override
    protected void defineSynchedData(Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SCALE, 0f);
        builder.define(MAX_LIFETIME, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.tickCount > 4) this.setScale(1);
        this.pullParticlesToCenter();
        this.attractPlayersOps();
        onDiscard();
    }

    private void onDiscard() {
        if(this.tickCount >= this.getMaxLifetime()) {
            Helpers.getSoundWithPosition(level(), this.blockPosition(), SoundReg.ORB_FIRE.get(), 2, 2f);
            EscapeDecoyAbility.onExistenceChange(this, getElement());
            this.discard();
        }
    }

    public void attractPlayersOps(){
        this.level().getNearbyEntities(
            Mob.class, TargetingConditions.DEFAULT, this,
            this.getBoundingBox().inflate(range)
        ).forEach(this::onAttract);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.tickCount = tag.getInt("tickCounter");
        this.setMaxLifetime(tag.getInt("maxLife"));
        this.setScale(1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("tickCounter", this.tickCount);
        tag.putInt("maxLife", this.getMaxLifetime());
    }

    public void pullParticlesToCenter(){
        var bakedParticlesOptions =
            bakedParticle(vitality().id(), 6, 2f, false);

        var genericParticleOptions =
            ParticleHandlers.genericParticle(GENERIC_PARTICLE, this.getElement(), 6, 2f);

        var particleOptionsList =
            List.of(bakedParticlesOptions, genericParticleOptions);

        PositionFinders.innerRadiusRandom(
            this.position()
                .add(0,this.getBbHeight()/2,0)
                .offsetRandom(create(), 1.5f), range, (double) range /2,
            positions -> {

                var directions = this.position()
                    .subtract(positions)
                    .normalize()
                    .add(0,this.getBbHeight()/2,0);

                ParticleHandlers.sendParticles(
                    this.level(),
                    Helpers.listRandom(particleOptionsList),
                    positions,
                    0,
                    directions.x,
                    Helpers.Random.nextDouble(-0.3, 0.3),
                    directions.z,
                    0.5
                );
            }
        );
    }
}
