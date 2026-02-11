package org.jahdoo.common.entities.decoy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.jahdoo.common.entities.EntityHelpers;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.EntityReg;
import org.jahdoo.trial_nexus.ability.abilities_combat.EscapeDecoyAbility;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import java.util.UUID;

import static net.minecraft.network.syncher.EntityDataSerializers.FLOAT;
import static net.minecraft.network.syncher.EntityDataSerializers.INT;
import static net.minecraft.network.syncher.SynchedEntityData.Builder;
import static net.minecraft.network.syncher.SynchedEntityData.defineId;
import static net.minecraft.util.RandomSource.create;
import static org.jahdoo.common.registers.mod.ElementReg.vitality;

public class Decoy extends Mob implements ITamableEntity {

    private static final EntityDataAccessor<Float> SCALE = defineId(Decoy.class, FLOAT);
    private static final EntityDataAccessor<Integer> MAX_LIFETIME = defineId(Decoy.class, INT);
    private LivingEntity owner;
    private UUID ownerUUID;
    private int range;

    public Decoy(EntityType<? extends Mob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Decoy(Level pLevel, LivingEntity owner, int range) {
        super(EntityReg.DECOY.get(), pLevel);
        this.owner = owner;
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

    public void sharedSound(SoundEvent sEvent, Float volume, Float pitch){
        SoundHelpers.getSoundWithPosition(level(), this.position(), sEvent, SoundSource.NEUTRAL, volume, pitch);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        sharedSound(SoundEvents.ELDER_GUARDIAN_HURT, 1F, 1.8F);
        sharedSound(SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, 1F, 1.8F);
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

        if(this.owner == null) {
            this.owner = this.reassignOwner(level(), owner, ownerUUID);
        }

        attractPlayersOps(this, range, owner);
        onDiscard();
    }

    private void onDiscard() {
        if(this.tickCount >= this.getMaxLifetime()) {
            sharedSound(getElement().sound(), 1F, 1.4F);
            EscapeDecoyAbility.onExistenceChange(this, getElement());
            this.discard();
        }
    }

    public static void attractPlayersOps(LivingEntity entity, int range, LivingEntity owner){
        entity.level().getNearbyEntities(
            Mob.class, TargetingConditions.DEFAULT, entity,
            entity.getBoundingBox().inflate(range)
        ).forEach(mob -> EntityHelpers.canTarget(mob, owner));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.tickCount = tag.getInt("tickCounter");
        this.setMaxLifetime(tag.getInt("maxLife"));
        this.range = tag.getInt("range");

        var uuid1 = loadTag(tag);
        if(uuid1 != null) this.ownerUUID = uuid1;

        this.setScale(1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("tickCounter", this.tickCount);
        tag.putInt("maxLife", this.getMaxLifetime());
        tag.putInt("range", this.range);

        saveTag(owner, tag);
    }

    public void pullParticlesToCenter(){
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
                    ParticleHandlers.getAllParticleTypes(getElement(), 6, 2),
                    positions,
                    0,
                    directions.x,
                    JahdooHelpers.Random.nextDouble(-0.3, 0.3),
                    directions.z,
                    0.5
                );
            }
        );
    }

    @Override
    public LivingEntity getOwner() {
        return owner;
    }

    @Override
    public void setOwner(LivingEntity livingEntity) {
        this.owner = livingEntity;
    }
}
