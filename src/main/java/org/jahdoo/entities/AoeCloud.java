package org.jahdoo.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.EntitiesRegister;
import org.jahdoo.registers.EntityPropertyRegister;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AoeCloud extends Entity implements TraceableEntity {
    public static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(AoeCloud.class, EntityDataSerializers.FLOAT);
    LivingEntity owner;
    UUID ownerUUID;
    String selectedAbility;
    String abilityId;
    WandAbilityHolder wandAbilityHolder;
    DefaultEntityBehaviour getAoe;
    double getRandomCloudRadius;

    public AoeCloud(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.reapplyPosition();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        pBuilder.define(DATA_RADIUS, 3.0F);
    }

    public AoeCloud(Level pLevel, LivingEntity livingEntity, float setWidth, String selectedAbility, String abilityId)  {
        super(EntitiesRegister.CUSTOM_AOE_CLOUD.get(), pLevel);
        this.reapplyPosition();
        this.setRadius(setWidth);
        this.owner = livingEntity;
        this.wandAbilityHolder = livingEntity.getMainHandItem().get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        this.selectedAbility = selectedAbility;
        this.abilityId = abilityId;
        this.getAoe = EntityPropertyRegister.REGISTRY.get(ModHelpers.res(selectedAbility)).getEntityProperty();
        this.getAoe.getAoeCloud(this);
        this.getRandomCloudRadius = ModHelpers.Random.nextDouble(setWidth + 1, setWidth + 1.5);
    }

    public AoeCloud(Level pLevel, LivingEntity livingEntity, float setWidth, String selectedAbility, WandAbilityHolder wandAbilityHolder, String abilityId)  {
        super(EntitiesRegister.CUSTOM_AOE_CLOUD.get(), pLevel);
        this.reapplyPosition();
        this.setRadius(setWidth);
        this.owner = livingEntity;
        this.wandAbilityHolder = wandAbilityHolder;
        this.selectedAbility = selectedAbility;
        this.abilityId = abilityId;
        this.getAoe = EntityPropertyRegister.REGISTRY.get(ModHelpers.res(selectedAbility)).getEntityProperty();
        this.getAoe.getAoeCloud(this);
        this.getRandomCloudRadius = ModHelpers.Random.nextDouble(setWidth + 1, setWidth + 1.5);
    }

    public double getRandomRadius(){
        return this.getRandomCloudRadius;
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    public void setRadius(float pRadius) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Mth.clamp(pRadius, 0.0F, 32.0F));
        }
    }

    public WandAbilityHolder getwandabilityholder(){
        return this.wandAbilityHolder;
    }

    @Override
    public void tick() {
        super.tick();
        if(this.level() instanceof ServerLevel serverLevel){
            if(owner == null && this.ownerUUID != null) this.owner = serverLevel.getPlayerByUUID(this.ownerUUID);
        }

        if(getAoe != null){
            getAoe.onTickMethod();
            getAoe.discardCondition();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putString("get_selection", selectedAbility);
        pCompound.putString("ability_id", this.abilityId);
        DefaultEntityBehaviour.writeTag(wandAbilityHolder, abilityId, pCompound);
        pCompound.putDouble("random_radius", this.getRandomCloudRadius);
        pCompound.putFloat("radius", this.getRadius());
        if(owner != null) pCompound.putUUID("uuid", owner.getUUID());
        getAoe.addAdditionalDetails(pCompound);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.selectedAbility = pCompound.getString("get_selection");
        this.abilityId = pCompound.getString("ability_id");
        this.wandAbilityHolder = DefaultEntityBehaviour.readTag(pCompound, abilityId);
        this.getRandomCloudRadius = pCompound.getDouble("random_radius");
        if(this.ownerUUID == null) this.ownerUUID = pCompound.getUUID("uuid");
        this.setRadius(pCompound.getFloat("radius"));
        if(getAoe == null){
            this.getAoe = EntityPropertyRegister.REGISTRY
                .get(ModHelpers.res(pCompound.getString("get_selection")))
                .getEntityProperty();
            getAoe.getAoeCloud(this);
            getAoe.readCompoundTag(pCompound);
        }
    }

    public void setOwner(LivingEntity owner){
        this.owner = owner;
    }


    @Nullable
    @Override
    public LivingEntity getOwner() {
        return this.owner;
    }
}
