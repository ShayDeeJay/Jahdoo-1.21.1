package org.jahdoo.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.components.ability_holder.AbilityHolder;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.entities.*;
import org.jahdoo.entities.living.EternalWizard;

import java.util.HashMap;

public abstract class DefaultEntityBehaviour extends AbstractEntityProperty {
    protected AoeCloud aoeCloud;
    protected ElementProjectile elementProjectile;
    public GenericProjectile genericProjectile;

    public AbstractElement getElementType(){return null;}
    public void getElementProjectile(ElementProjectile elementProjectile) { this.elementProjectile = elementProjectile; }
    public void getGenericProjectile(GenericProjectile genericProjectile) { this.genericProjectile = genericProjectile; }
    public void getAoeCloud(AoeCloud aoeCloud) { this.aoeCloud = aoeCloud; }
    public void onBlockBlockHit(BlockHitResult blockHitResult) { }
    public void onEntityHit(LivingEntity hitEntity) { }
    public void onTickMethod() { }
    public void discardCondition() { }
    public void addAdditionalDetails(CompoundTag compoundTag) { }
    public void readCompoundTag(CompoundTag compoundTag) { }

    public boolean damageEntity(LivingEntity hitEntity){
        return !(hitEntity instanceof EternalWizard) && !(hitEntity instanceof Player);
    }

    public static boolean canDamageEntity(LivingEntity hitEntity, LivingEntity owner){
        if(owner != null) {
            var isPlayerTamed = !(hitEntity instanceof TamableEntity tamableEntity && tamableEntity.getOwner() == owner);
            var isOwner = hitEntity.getUUID() != owner.getUUID();
            return isOwner && isPlayerTamed;
        }
        return true;
    }

    public static WandAbilityHolder readTag(CompoundTag compoundTag, String abilityId){
        var holder = new HashMap<String, AbilityHolder.AbilityModifiers>();
        compoundTag.getCompound("wandAbilities").getAllKeys().forEach(
            keys -> {
                var actualValue = compoundTag.getCompound("wandAbilities").getDouble(keys);
                var modifier = new AbilityHolder.AbilityModifiers(actualValue, 0,0,0,actualValue,true);
                holder.put(keys, modifier);
            }
        );
        var abilityHolder = new AbilityHolder(holder);
        var newHolder = new HashMap<String, AbilityHolder>();
        newHolder.put(abilityId, abilityHolder);
        return new WandAbilityHolder(newHolder);
    }

    public static void writeTag(
        WandAbilityHolder wandAbilityHolder,
        String abilityId,
        CompoundTag compoundTag
    ){
        var storedAbility = new CompoundTag();
        var abilityHolder = wandAbilityHolder.abilityProperties().get(abilityId);

        if(abilityHolder != null){
            abilityHolder.abilityProperties().forEach((key, value) -> storedAbility.putDouble(key, value.actualValue()));
        }
        compoundTag.put("wandAbilities", storedAbility);
    }

    public static void applyInertia(Projectile projectile, float inertiaFactor) {
        var currentVelocity = projectile.getDeltaMovement();
        var newVelocityX = currentVelocity.x * inertiaFactor;
        var newVelocityY = currentVelocity.y * inertiaFactor;
        var newVelocityZ = currentVelocity.z * inertiaFactor;
        projectile.setDeltaMovement(newVelocityX, newVelocityY, newVelocityZ);
    }
}
