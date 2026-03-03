package org.jahdoo.common.entities.aoe_cloud;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.Level;
import org.jahdoo.trial_nexus.ability.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.IEntityProperties;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.mod.EntityDataReg;
import org.jahdoo.common.registers.EntityReg;

import java.util.UUID;

import static net.minecraft.network.syncher.EntityDataSerializers.FLOAT;
import static net.minecraft.network.syncher.EntityDataSerializers.INT;
import static net.minecraft.network.syncher.EntityDataSerializers.STRING;
import static net.minecraft.network.syncher.SynchedEntityData.defineId;

public class AoeCloud extends Entity implements TraceableEntity, IEntityProperties {

    public static final EntityDataAccessor<Float> DATA_RADIUS =
        defineId(AoeCloud.class, FLOAT);

    public static final EntityDataAccessor<String> ENTITY_TYPE =
        defineId(AoeCloud.class, STRING);

    public static final EntityDataAccessor<Integer> DATA_DISCARD_TIME =
        defineId(AoeCloud.class, INT);

    public static final EntityDataAccessor<Integer> DATA_TICK_COUNT =
        defineId(AoeCloud.class, INT);

    public static final EntityDataAccessor<Integer> ELEMENT_ID =
        defineId(AoeCloud.class, INT);

    private UUID ownerUUID;
    private String abilityId;
    private LivingEntity owner;
    private double randomCloudRadius;
    private DefaultEntityBehaviour aoeBehaviour;
    private AbilityHolder abilityHolder;

    /* ------------------------------------------------------------ */

    public AoeCloud(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.reapplyPosition();
    }

    public AoeCloud(Level level, LivingEntity owner, float radius, int discardTime) {
        super(EntityReg.CUSTOM_AOE_CLOUD.get(), level);
        this.reapplyPosition();
        this.owner = owner;
        this.setRadius(radius);
        this.setDiscardTime(discardTime);
        this.randomCloudRadius = JahdooHelpers.Random.nextDouble(radius + 1, radius + 1.5);
    }

    public AoeCloud( Level level, LivingEntity owner, float radius, String selectedAbility, AbilityHolder holder, String abilityId ) {
        super(EntityReg.CUSTOM_AOE_CLOUD.get(), level);
        this.reapplyPosition();
        this.owner = owner;
        this.abilityHolder = holder;
        this.abilityId = abilityId;
        this.setRadius(radius);
        this.setEntityType(selectedAbility);
        this.aoeBehaviour = EntityDataReg.getProperty(selectedAbility);
        if (this.aoeBehaviour != null) {
            this.aoeBehaviour.getAoeCloud(this);
        }
        this.randomCloudRadius = JahdooHelpers.Random.nextDouble(radius + 1, radius + 1.5);
    }

    public AoeCloud(Level level, LivingEntity owner, float radius, String selectedAbility, String abilityId) {

        super(EntityReg.CUSTOM_AOE_CLOUD.get(), level);
        this.reapplyPosition();

        this.owner = owner;
        this.abilityId = abilityId;
        this.abilityHolder = owner.getData(AttachmentReg.CASTER_DATA).getHolder(abilityId);

        this.setRadius(radius);
        this.setEntityType(selectedAbility);

        this.aoeBehaviour = EntityDataReg.getProperty(selectedAbility);
        if (this.aoeBehaviour != null) {
            this.aoeBehaviour.getAoeCloud(this);
        }

        this.randomCloudRadius = JahdooHelpers.Random.nextDouble(radius + 1, radius + 1.5);
    }

    public AoeCloud(Level level, LivingEntity owner, float radius, String selectedAbility, String abilityId, int elementId) {

        super(EntityReg.CUSTOM_AOE_CLOUD.get(), level);
        this.setElementId(elementId);


        this.reapplyPosition();

        this.owner = owner;
        this.abilityId = abilityId;
        this.abilityHolder = owner.getData(AttachmentReg.CASTER_DATA).getHolder(abilityId);

        this.setRadius(radius);
        this.setEntityType(selectedAbility);

        this.aoeBehaviour = EntityDataReg.getProperty(selectedAbility);
        if (this.aoeBehaviour != null) {
            this.aoeBehaviour.getAoeCloud(this);
        }

        this.randomCloudRadius = JahdooHelpers.Random.nextDouble(radius + 1, radius + 1.5);
    }

    /* ------------------------------------------------------------ */

    @Override
    protected void defineSynchedData(Builder builder) {
        builder.define(DATA_RADIUS, 3.0F);
        builder.define(ENTITY_TYPE, "");
        builder.define(DATA_DISCARD_TIME, -1);
        builder.define(DATA_TICK_COUNT, 0);
        builder.define(ELEMENT_ID, 0); // NEW
    }

    /* ------------------------------------------------------------ */

    public double getRandomRadius() {
        return this.randomCloudRadius;
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    public void setRadius(float radius) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Mth.clamp(radius, 0.0F, 32.0F));
        }
    }

    public String getEntityType() {
        return this.getEntityData().get(ENTITY_TYPE);
    }

    public void setEntityType(String type) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(ENTITY_TYPE, type);
        }
    }

    // NEW GETTER / SETTER
    public int getElementId() {
        return this.getEntityData().get(ELEMENT_ID);
    }

    public void setElementId(int id) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(ELEMENT_ID, id);
        }
    }

    public void setDiscardTime(int discardTime) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(DATA_DISCARD_TIME, discardTime);
        }
    }

    public int getDiscardTime() {
        return this.getEntityData().get(DATA_DISCARD_TIME);
    }

    public int getSyncedTickCount() {
        return this.getEntityData().get(DATA_TICK_COUNT);
    }

    @Override
    public LivingEntity getOwner() {
        return this.owner;
    }

    public void setOwner(LivingEntity owner) {
        this.owner = owner;
    }

    public AbilityHolder getAbilityHolder() {
        return this.abilityHolder;
    }

    /* ------------------------------------------------------------ */

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            this.getEntityData().set(DATA_TICK_COUNT, this.tickCount);
        }

        if (this.level() instanceof ServerLevel serverLevel) {
            if (owner == null && ownerUUID != null) {
                this.owner = serverLevel.getPlayerByUUID(ownerUUID);
            }
        }

        if (aoeBehaviour != null) {
            aoeBehaviour.onTickMethod();
            aoeBehaviour.discardCondition();
        }

        if (!this.level().isClientSide) {
            int discardTime = getDiscardTime();
            if (discardTime >= 0 && getSyncedTickCount() >= discardTime) {
                this.discard();
            }
        }
    }

    /* ------------------------------------------------------------ */

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putString("selection", getEntityType());
        tag.putString("ability_id", abilityId == null ? "" : abilityId);
        tag.putDouble("random_radius", randomCloudRadius);
        tag.putFloat("radius", getRadius());
        tag.putInt("discard_time", getDiscardTime());
        tag.putInt("tick_count", tickCount);
        tag.putInt("element_id", getElementId()); // NEW

        if (owner != null)
            tag.putUUID("uuid", owner.getUUID());

        if (abilityHolder != null)
            AbilityHolder.writeTag(abilityHolder, tag);

        if (aoeBehaviour != null)
            aoeBehaviour.addAdditionalDetails(tag);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {

        this.setEntityType(tag.getString("selection"));
        this.abilityId = tag.getString("ability_id");
        this.randomCloudRadius = tag.getDouble("random_radius");

        this.setRadius(tag.getFloat("radius"));
        this.setDiscardTime(tag.getInt("discard_time"));
        this.setElementId(tag.getInt("element_id")); // NEW

        this.tickCount = tag.getInt("tick_count");

        if (!this.level().isClientSide)
            this.getEntityData().set(DATA_TICK_COUNT, this.tickCount);

        if (tag.hasUUID("uuid"))
            this.ownerUUID = tag.getUUID("uuid");

        this.abilityHolder = AbilityHolder.readTag(tag, abilityId);

        if (aoeBehaviour == null) {
            var entry = EntityDataReg.REGISTRY
                .get(JahdooHelpers.res(tag.getString("selection")));

            if (entry != null) {
                this.aoeBehaviour = entry.getEntityProperty();
                this.aoeBehaviour.getAoeCloud(this);
                this.aoeBehaviour.readCompoundTag(tag);
            }
        }
    }
}