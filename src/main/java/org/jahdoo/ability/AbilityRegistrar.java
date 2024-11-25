package org.jahdoo.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;

import java.util.Objects;

public abstract class AbilityRegistrar {
    public static final int DISTANCE_CAST = 1;
    public static final int PROJECTILE_CAST = 2;
    public static final int AREA_CAST = 3;
    public static final int HOLD_CAST = 4;
    private String abilityId = null;

    public final String setAbilityId() {
        if (abilityId == null) {
            var resourceLocation = Objects.requireNonNull(getAbilityResource());
            abilityId = resourceLocation.getPath().intern();
        }
        return abilityId;
    }

    public abstract ResourceLocation getAbilityResource();

    public String getAbilityName(){
        return ModHelpers.stringIdToName(this.abilityId);
    }

    public abstract void setModifiers(ItemStack itemStack);

    public abstract String getDescription();

    public abstract int getCastType();

    public abstract AbstractElement getElemenType();

    public abstract void invokeAbility(Player player);

    public abstract JahdooRarity rarity();

    public boolean isMultiType(){
        return false;
    }

    public ResourceLocation getAbilityIconLocation(){
        return ModHelpers.res("textures/ability_icons/"+abilityId+".png");
    }

    public abstract int getCastDuration(Player player);

    public boolean internallyChargeManaAndCooldown(){
        return false;
    }

    public void fireProjectile(Projectile projectile, LivingEntity player, float velocity){
        if(player != null){
            if(player.level() instanceof ServerLevel serverLevel){
                Vec3 direction = player.getLookAngle();
                projectile.shoot(direction.x(), direction.y(), direction.z(), velocity, 0);
                projectile.setOwner(player);
                serverLevel.addFreshEntity(projectile);
                ModHelpers.getSoundWithPosition(projectile.level(), projectile.blockPosition(), SoundRegister.ORB_FIRE.get(), 0.4f, 1f);
            }
        }
    }

    public void fireUtilityProjectile(Projectile projectile, LivingEntity player){
        if(player != null){
            if(player.level() instanceof ServerLevel serverLevel){
                Vec3 direction = player.getLookAngle();
                projectile.shoot(direction.x(), direction.y(), direction.z(), 1.2f, 0);
                projectile.setOwner(player);
                serverLevel.addFreshEntity(projectile);
                ModHelpers.getSoundWithPosition(projectile.level(), projectile.blockPosition(), SoundEvents.BREEZE_CHARGE , 0.05f,1.4f);
            }
        }
    }

    public void fireUtilityProjectile(Projectile projectile, BlockPos pos, Vec3i direction){
        if (projectile.level() instanceof ServerLevel serverLevel) {
            Vec3 eastDirection = Vec3.atCenterOf(direction).subtract(pos.getCenter()).normalize(); // Vector pointing east
            projectile.shoot(eastDirection.x, eastDirection.y, eastDirection.z, 0.5f, 0);
            serverLevel.addFreshEntity(projectile);
        }
    }
}
