package org.jahdoo.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jahdoo.ability.abilities.ability_data.BurningSkullsAbility;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

public abstract class ProjectileProperties extends Projectile {
    private static final EntityDataAccessor<Integer> ANIMATION_TYPE = SynchedEntityData.defineId(ProjectileProperties.class, EntityDataSerializers.INT);

    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYRot;
    protected double lerpXRot;

    public int animationType() {
        return this.entityData.get(ANIMATION_TYPE);
    }

    public void setAnimation(int getSelectedAbility) {
        this.entityData.set(ANIMATION_TYPE, getSelectedAbility);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        pBuilder.define(ANIMATION_TYPE, 0);
    }

    protected ProjectileProperties(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public void lerpTo(double pX, double pY, double pZ, float pYRot, float pXRot, int pSteps) {
        this.lerpX = pX;
        this.lerpY = pY;
        this.lerpZ = pZ;
        this.lerpYRot = pYRot;
        this.lerpXRot = pXRot;
        this.lerpSteps = 2;
    }

    @Override
    public void tick() {
        super.tick();
        Entity entity = this.getOwner();
        if (this.level().isClientSide || (entity == null || !entity.isRemoved()) && this.level().hasChunk(this.chunkPosition().x, this.chunkPosition().z)) {
            HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitresult)) {
                this.onHit(hitresult);
            }

            this.checkInsideBlocks();
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() + vec3.x;
            double d1 = this.getY() + vec3.y;
            double d2 = this.getZ() + vec3.z;
            this.setPos(d0, d1, d2);

        }

        if(this.level().isClientSide){
            if (this.lerpSteps > 0) {
                double d = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
                double e = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
                double y = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
                double g = Mth.wrapDegrees(this.lerpYRot - (double) this.getYRot());
                this.setYRot(this.getYRot() + (float) g / (float) this.lerpSteps);
                this.setXRot(this.getXRot() + (float) (this.lerpXRot - (double) this.getXRot()) / (float) this.lerpSteps);
                --this.lerpSteps;
                this.setPos(d, e, y);
                this.setRot(this.getYRot(), this.getXRot());
            }
        }
    }

    public void setProjectileWithOffsets(Projectile projectile, LivingEntity owner, double spacing, double distance){
        double forwardHorizontalOffset = -Math.sin(Math.toRadians(owner.yRotO)) * Math.cos(Math.toRadians(owner.xRotO)) * distance;
        double forwardVerticalOffset = Math.sin(-Math.toRadians(owner.xRotO)) * distance;
        double forwardOffsetX = owner.getX() + forwardHorizontalOffset;
        double forwardOffsetY = owner.getY() + owner.getEyeHeight() + forwardVerticalOffset - 0.05;
        double forwardOffsetZ = owner.getZ() + Math.cos(Math.toRadians(owner.yRotO)) * Math.cos(Math.toRadians(owner.xRotO)) * distance;
        double rightOffsetX = Math.cos(Math.toRadians(owner.yRotO)) * spacing;
        double rightOffsetZ = Math.sin(Math.toRadians(owner.yRotO)) * spacing;
        double spawnX = forwardOffsetX + rightOffsetX;
        double spawnZ = forwardOffsetZ + rightOffsetZ;
        projectile.moveTo(spawnX, forwardOffsetY, spawnZ, projectile.getYRot(), projectile.getXRot());
    }

    public static double getTag(String name, WandAbilityHolder wandAbilityHolder) {
        var abName = BurningSkullsAbility.abilityId.getPath().intern();
        if(ModHelpers.getModifierValue(wandAbilityHolder, abName).get(name) != null){
            return ModHelpers.getModifierValue(wandAbilityHolder,abName).get(name).setValue();
        }
        return 0;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public float distanceTo(@NotNull Entity pEntity) {
        return super.distanceTo(pEntity);
    }

    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && !entity.noPhysics;
    }

    public boolean isPickable() {
        return true;
    }

    public float getPickRadius() {
        return 1.0F;
    }

    public abstract AbstractElement getElementType();
}
