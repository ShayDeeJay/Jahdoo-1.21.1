package org.jahdoo.ascension.ability;

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
import org.jahdoo.ascension.ability.abilities_combat.BurningSkullsAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;

public abstract class ProjectileProperties extends Projectile {
    private static final EntityDataAccessor<Integer> ANIMATION_TYPE = SynchedEntityData.defineId(ProjectileProperties.class, EntityDataSerializers.INT);

    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYRot;
    protected double lerpXRot;

    protected ProjectileProperties(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public abstract AbstractElement getElementType();

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

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public float distanceTo(Entity pEntity) {
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

    public static double getTag(String name, AbilityHolder abilityHolder) {
        var abName = BurningSkullsAbility.abilityId.getPath().intern();
        var modifier = Helpers.getModifierValue(abilityHolder, abName).get(name);

        if(modifier != null) return modifier.setValue();

        return 0;
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

    public void setProjectileWithOffsets(Projectile projectile, LivingEntity owner, double spacing, double distance){
        var forwardHorizontalOffset = -Math.sin(Math.toRadians(owner.yRotO)) * Math.cos(Math.toRadians(owner.xRotO)) * distance;
        var forwardVerticalOffset = Math.sin(-Math.toRadians(owner.xRotO)) * distance;
        var forwardOffsetX = owner.getX() + forwardHorizontalOffset;
        var forwardOffsetY = owner.getY() + owner.getEyeHeight() + forwardVerticalOffset - 0.05;
        var forwardOffsetZ = owner.getZ() + Math.cos(Math.toRadians(owner.yRotO)) * Math.cos(Math.toRadians(owner.xRotO)) * distance;
        var rightOffsetX = Math.cos(Math.toRadians(owner.yRotO)) * spacing;
        var rightOffsetZ = Math.sin(Math.toRadians(owner.yRotO)) * spacing;
        var spawnX = forwardOffsetX + rightOffsetX;
        var spawnZ = forwardOffsetZ + rightOffsetZ;

        projectile.moveTo(spawnX, forwardOffsetY, spawnZ, projectile.getYRot(), projectile.getXRot());
    }

    @Override
    public void tick() {
        super.tick();
        var entity = this.getOwner();
        if (this.level().isClientSide || (entity == null || !entity.isRemoved()) && this.level().hasChunk(this.chunkPosition().x, this.chunkPosition().z)) {

            var hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitresult.getType() != HitResult.Type.MISS && !EventHooks.onProjectileImpact(this, hitresult)) {
                this.onHit(hitresult);
            }

            this.checkInsideBlocks();
            Vec3 vec3 = this.getDeltaMovement();
            var d0 = this.getX() + vec3.x;
            var d1 = this.getY() + vec3.y;
            var d2 = this.getZ() + vec3.z;
            this.setPos(d0, d1, d2);

        }

        if(this.level().isClientSide){
            if (this.lerpSteps > 0) {
                var d = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
                var e = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
                var y = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;
                var g = Mth.wrapDegrees(this.lerpYRot - (double) this.getYRot());
                this.setYRot(this.getYRot() + (float) g / (float) this.lerpSteps);
                this.setXRot(this.getXRot() + (float) (this.lerpXRot - (double) this.getXRot()) / (float) this.lerpSteps);
                --this.lerpSteps;
                this.setPos(d, e, y);
                this.setRot(this.getYRot(), this.getXRot());
            }
        }
    }
}
