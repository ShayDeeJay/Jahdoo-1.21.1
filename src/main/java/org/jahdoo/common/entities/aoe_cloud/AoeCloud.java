package org.jahdoo.common.entities.aoe_cloud;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.IEntityProperties;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.mod.EntityDataReg;
import org.jahdoo.common.registers.EntityReg;

import java.util.UUID;

import static net.minecraft.network.syncher.EntityDataSerializers.FLOAT;
import static net.minecraft.network.syncher.EntityDataSerializers.STRING;
import static net.minecraft.network.syncher.SynchedEntityData.Builder;
import static net.minecraft.network.syncher.SynchedEntityData.defineId;

public class AoeCloud extends Entity implements TraceableEntity, IEntityProperties {

    public static final EntityDataAccessor<Float> DATA_RADIUS = defineId(AoeCloud.class, FLOAT);
    public static final EntityDataAccessor<String> ENTITY_TYPE = defineId(AoeCloud.class, STRING);

    private UUID ownerUUID;
    private String abilityId;
    private LivingEntity owner;
    private double getRandomCloudRadius;
    private DefaultEntityBehaviour getAoe;
    private AbilityHolder abilityHolder;

    public AoeCloud(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.reapplyPosition();
    }

    public AoeCloud(
        Level level,
        LivingEntity livingEntity,
        float setWidth,
        String selectedAbility,
        String abilityId
    )  {
        super(EntityReg.CUSTOM_AOE_CLOUD.get(), level);
        this.reapplyPosition();
        this.setRadius(setWidth);
        this.owner = livingEntity;
//        this.abilityHolder = livingEntity.getItemInHand(livingEntity.getUsedItemHand()).get(ABILITY_HOLDER.get());
        this.abilityHolder = livingEntity.getData(AttachmentReg.CASTER_DATA).getHolder(abilityId);
        this.setEntityType(selectedAbility);
        this.abilityId = abilityId;
        this.getAoe = EntityDataReg.getProperty(selectedAbility);
        this.getAoe.getAoeCloud(this);
        this.getRandomCloudRadius = Helpers.Random.nextDouble(setWidth + 1, setWidth + 1.5);
    }

    public AoeCloud(
        Level level,
        LivingEntity livingEntity,
        float setWidth,
        String selectedAbility,
        AbilityHolder wandAbilityHolder,
        String abilityId
    )  {
        super(EntityReg.CUSTOM_AOE_CLOUD.get(), level);
        this.reapplyPosition();
        this.setRadius(setWidth);
        this.owner = livingEntity;
        this.abilityHolder = wandAbilityHolder;
        this.setEntityType(selectedAbility);
        this.abilityId = abilityId;
        this.getAoe = EntityDataReg.getProperty(selectedAbility);
        this.getAoe.getAoeCloud(this);
        this.getRandomCloudRadius = Helpers.Random.nextDouble(setWidth + 1, setWidth + 1.5);
    }

    public double getRandomRadius(){
        return this.getRandomCloudRadius;
    }

    public String getEntityType() {
        return this.getEntityData().get(ENTITY_TYPE);
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    public AbilityHolder getAbilityHolder(){
        return this.abilityHolder;
    }

    public void setOwner(LivingEntity owner){
        this.owner = owner;
    }

    @Override
    public LivingEntity getOwner() {
        return this.owner;
    }

    @Override
    protected void defineSynchedData(Builder builder) {
        builder.define(DATA_RADIUS, 3.0F);
        builder.define(ENTITY_TYPE, "");

    }

    public void setEntityType(String type) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(ENTITY_TYPE, type);
        }
    }

    public void setRadius(float radius) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Mth.clamp(radius, 0.0F, 32.0F));
        }
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
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("get_selection", getEntityType());
        tag.putString("ability_id", this.abilityId);
        AbilityHolder.writeTag(abilityHolder, tag);
        tag.putDouble("random_radius", this.getRandomCloudRadius);
        tag.putFloat("radius", this.getRadius());
        if(owner != null) tag.putUUID("uuid", owner.getUUID());
        getAoe.addAdditionalDetails(tag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setEntityType(tag.getString("get_selection"));
        this.abilityId = tag.getString("ability_id");
        this.abilityHolder = AbilityHolder.readTag(tag, abilityId);
        this.getRandomCloudRadius = tag.getDouble("random_radius");
        if(tag.hasUUID("uuid")){
            if (this.ownerUUID == null) this.ownerUUID = tag.getUUID("uuid");
        }
        this.setRadius(tag.getFloat("radius"));
        if(getAoe == null){
            this.getAoe = EntityDataReg.REGISTRY
                .get(Helpers.res(tag.getString("get_selection")))
                .getEntityProperty();
            getAoe.getAoeCloud(this);
            getAoe.readCompoundTag(tag);
        }
    }

}
