package org.jahdoo.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.rarity.JahdooRarity;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;

import java.util.Objects;
import java.util.function.Supplier;

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

    public int Laccuracy(LivingEntity player){
        var effect = EffectsRegister.AMPLIFY_BLOCK_REACH;
        var getEffectLevel = player.getEffect(effect);
        if(player.hasEffect(effect)){
            return getEffectLevel.getAmplifier();
        }
        return 0;
    }

    public static Vec3 calculateDirectionOffset(LivingEntity player, double offset) {
        var lookDirection = player.getLookAngle();
        var rightVector = new Vec3(-lookDirection.z(), 0, lookDirection.x()).normalize(); // Perpendicular to look direction
        return rightVector.scale(offset);
    }

    public static double offsetShoot(LivingEntity livingEntity){
        return livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND ? -0.3 : 0.3;
    }

    public void fireProjectile(Projectile projectile, LivingEntity player, float velocity){
        if(player != null){
            if(player.level() instanceof ServerLevel serverLevel){
                Vec3 direction = player.getLookAngle();
                projectile.shoot(direction.x(), direction.y(), direction.z(), velocity, Laccuracy(player));
                projectile.setOwner(player);
                serverLevel.addFreshEntity(projectile);
                ModHelpers.getSoundWithPositionV(projectile.level(), player.position(), SoundRegister.ORB_FIRE.get(), 0.4f, 1f);
            }
        }
    }

    public void fireMultiShotProjectile(int numberOfProjectile, float velocities, Player player, double adjustSpread, Supplier<Projectile> projectileSupplier){
       var totalWidth = (numberOfProjectile - 1) * adjustSpread; // Adjust the total width as needed
        var startOffset = -totalWidth / 2.0;

        for (int i = 0; i < numberOfProjectile; i++) {
            double offset = numberOfProjectile == 1 ? 0 : startOffset + i * (totalWidth / (numberOfProjectile - 1));
            var projectile  = projectileSupplier.get();
            var directionOffset = calculateDirectionOffset(player, offset);
            var direction = player.getLookAngle().add(directionOffset).normalize();
            this.fireProjectileDirection(projectile, player, velocities, direction);
        }
    }

    public void fireProjectileNoSound(Projectile projectile, LivingEntity player, float velocity){
        if(player != null){
            if(player.level() instanceof ServerLevel serverLevel){
                Vec3 direction = player.getLookAngle();
                projectile.shoot(direction.x(), direction.y(), direction.z(), velocity, Laccuracy(player));
                projectile.setOwner(player);
                serverLevel.addFreshEntity(projectile);
            }
        }
    }

    public void fireProjectileDirection(Projectile projectile, LivingEntity player, float velocity, Vec3 direction){
        if(player != null){
            if(player.level() instanceof ServerLevel serverLevel){
                projectile.shoot(direction.x(), direction.y(), direction.z(), velocity, Laccuracy(player));
                projectile.setOwner(player);
                serverLevel.addFreshEntity(projectile);
            }
        }
    }

    public void fireUtilityProjectile(Projectile projectile, LivingEntity player){
        if(player != null){
            if(player.level() instanceof ServerLevel serverLevel){
                Vec3 direction = player.getLookAngle();
                projectile.shoot(direction.x(), direction.y(), direction.z(), 1.2f, Laccuracy(player));
                projectile.setOwner(player);
                serverLevel.addFreshEntity(projectile);
                ModHelpers.getSoundWithPositionV(projectile.level(), player.position(), SoundEvents.BREEZE_CHARGE , 0.05f,1.4f);
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
