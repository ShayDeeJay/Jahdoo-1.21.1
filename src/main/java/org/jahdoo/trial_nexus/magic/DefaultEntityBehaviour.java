package org.jahdoo.trial_nexus.magic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.common.entities.ITamableEntity;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.entities.element_projectile.ElementProjectile;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;

public abstract class DefaultEntityBehaviour extends AbstractEntityProperty {

    protected AoeCloud cloud;
    protected ElementProjectile element;
    public GenericProjectile generic;

    public AbstractElement getElementType(){return null;}
    public void getElementProjectile(ElementProjectile elementProjectile) { this.element = elementProjectile; }
    public void getGenericProjectile(GenericProjectile genericProjectile) { this.generic = genericProjectile; }
    public void getAoeCloud(AoeCloud aoeCloud) { this.cloud = aoeCloud; }
    public void onBlockBlockHit(BlockHitResult blockHitResult){}
    public void onEntityHit(LivingEntity hitEntity){}
    public void onTickMethod(){}
    public void discardCondition(){}
    public void addAdditionalDetails(CompoundTag compoundTag){}
    public void readCompoundTag(CompoundTag compoundTag){}

    public static boolean canDamageEntity(LivingEntity hitEntity, LivingEntity owner){
        if(owner != null) {
            var isPlayerTamed = !(hitEntity instanceof ITamableEntity tamableEntity && tamableEntity.getOwner() == owner);
            var isOwner = hitEntity.getUUID() != owner.getUUID();
            return isOwner && isPlayerTamed;
        }
        return true;
    }

    public static void applyInertia(Projectile projectile, float inertiaFactor) {
        var currentVelocity = projectile.getDeltaMovement();
        var newVelocityX = currentVelocity.x * inertiaFactor;
        var newVelocityY = currentVelocity.y * inertiaFactor;
        var newVelocityZ = currentVelocity.z * inertiaFactor;
        projectile.setDeltaMovement(newVelocityX, newVelocityY, newVelocityZ);
    }

}
