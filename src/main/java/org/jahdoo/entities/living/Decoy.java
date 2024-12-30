package org.jahdoo.entities.living;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.abilities.ability_data.EscapeDecoyAbility;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;

public class Decoy extends Mob {
    private static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(Decoy.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> MAX_LIFETIME = SynchedEntityData.defineId(Decoy.class, EntityDataSerializers.INT);
    private Player player;
    private int range;

    BakedParticleOptions bakedParticlesOptions =
        bakedParticleOptions(ElementRegistry.VITALITY.get().getTypeId(), 6, 2f, false);
    GenericParticleOptions genericParticleOptions =
        genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, this.getElement(), 6, 2f);

    List<ParticleOptions> particleOptionsList = List.of(bakedParticlesOptions, genericParticleOptions);

    private AbstractElement getElement(){
        return ElementRegistry.VITALITY.get();
    }

    public Decoy(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Decoy(Level pLevel, Player player, int range) {
        super(EntitiesRegister.DECOY.get(), pLevel);
        this.player = player;
        this.range = range;
    }

    public float getScale() {
        return this.entityData.get(SCALE);
    }
    public void setScale(float getSelectedAbility) {
        this.entityData.set(SCALE, getSelectedAbility);
    }

    @Override
    public void setHealth(float pHealth) {
        super.setHealth(200);
    }

    public int getMaxLifetime() {
        return this.entityData.get(MAX_LIFETIME);
    }
    public void setMaxLifetime(int getSelectedAbility) {
        this.entityData.set(MAX_LIFETIME, getSelectedAbility);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        ModHelpers.getSoundWithPosition(level(), this.blockPosition(), SoundEvents.ELDER_GUARDIAN_HURT, 1, 1.8f);
        ModHelpers.getSoundWithPosition(level(), this.blockPosition(), SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, 1, 1.8f);
        return false;
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
    protected void pickUpItem(ItemEntity pItemEntity) {}

    @Override
    public boolean shouldDropExperience() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.AMETHYST_BLOCK_BREAK;
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(SCALE, 0f);
        pBuilder.define(MAX_LIFETIME, 0);
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
            ModHelpers.getSoundWithPosition(level(), this.blockPosition(), SoundRegister.ORB_FIRE.get(), 2, 2f);
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

    private void onAttract(Mob mob) {
        if(mob.getTarget() == player) mob.setTarget(this);
    }

    @Override
    protected void onEffectAdded(MobEffectInstance effectInstance, @Nullable Entity entity) {
        super.onEffectAdded(effectInstance, entity);
    }

    public void pullParticlesToCenter(){
        PositionGetters.getInnerRingOfRadiusRandom(
            this.position()
                .add(0,this.getBbHeight()/2,0)
                .offsetRandom(RandomSource.create(), 1.5f), range, (double) range /2,
            positions -> {
                Vec3 directions = this.position().subtract(positions).normalize().add(0,this.getBbHeight()/2,0);
                ParticleHandlers.sendParticles(
                    this.level(),
                    particleOptionsList.get(this.random.nextInt(0,2)),
                    positions,
                    0,
                    directions.x,
                    ModHelpers.Random.nextDouble(-0.3, 0.3),
                    directions.z,
                    0.5
                );
            }
        );
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.tickCount = pCompound.getInt("tickCounter");
        this.setMaxLifetime(pCompound.getInt("maxLife"));
        this.setScale(1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("tickCounter", this.tickCount);
        pCompound.putInt("maxLife", this.getMaxLifetime());
    }
}
