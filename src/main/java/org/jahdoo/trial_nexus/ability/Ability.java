package org.jahdoo.trial_nexus.ability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.SoundReg;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class Ability {

    public static final String NON = "non";
    public static final int DISTANCE_CAST = 1;
    public static final int PROJECTILE_CAST = 2;
    public static final int AREA_CAST = 3;
    public static final int HOLD_CAST = 4;
    private String abilityId = null;

    public abstract ResourceLocation getAbilityResource();

    public abstract AbilityHolder setModifiers();

    public abstract String getDescription();

    public abstract int getCastType();

    public abstract AbstractElement getElemenType();

    public abstract JahdooRarity rarity();

    public abstract int getCastDuration(Player player);

    public abstract void invokeAbility(Player player);

    public abstract int levelRequirement();

    public boolean isMultiType(){
        return false;
    }

    public boolean selfChargeAbility(){
        return false;
    }

    abstract public int getAbilityCost();

    public String getAbilityName(){
        return Helpers.stringIdToName(this.abilityId);
    }

    public ResourceLocation getAbilityIconLocation(){
        return Helpers.res("textures/ability_icons/"+abilityId+".png");
    }

    public static double offsetShoot(LivingEntity livingEntity){
        return livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND ? -0.3 : 0.3;
    }

    public static Vec3 calculateDirectionOffset(LivingEntity player, double offset) {
        var lookDirection = player.getLookAngle();
        var rightVector = new Vec3(-lookDirection.z(), 0, lookDirection.x()).normalize(); // Perpendicular to look direction
        return rightVector.scale(offset);
    }

    public final String setAbilityId() {
        if (abilityId == null) {
            var resourceLocation = Objects.requireNonNull(getAbilityResource());
            abilityId = resourceLocation.getPath().intern();
        }
        return abilityId;
    }

    public static int hexedEffect(LivingEntity player){
        var effect = EffectReg.HEXED;
        var getEffectLevel = player.getEffect(effect);

        if(getEffectLevel != null) return getEffectLevel.getAmplifier();
        return 0;
    }

    public static void fireUtilityProjectile(Projectile projectile, BlockPos pos, Vec3i direction){
        if (projectile.level() instanceof ServerLevel serverLevel) {
            Vec3 eastDirection = Vec3.atCenterOf(direction).subtract(pos.getCenter()).normalize(); // Vector pointing east
            projectile.shoot(eastDirection.x, eastDirection.y, eastDirection.z, 0.5f, 0);
            serverLevel.addFreshEntity(projectile);
        }
    }

    public static void fireProjectileDirection(Projectile projectile, LivingEntity player, float velocity, Vec3 direction){
        if(player != null){
            if(player.level() instanceof ServerLevel serverLevel){
                projectile.shoot(direction.x(), direction.y(), direction.z(), velocity, hexedEffect(player));
                projectile.setOwner(player);
                serverLevel.addFreshEntity(projectile);
            }
        }
    }

    public void fireProjectileNoSound(Projectile projectile, LivingEntity player, float velocity){
        if(player != null){
            if(player.level() instanceof ServerLevel serverLevel){
                Vec3 direction = player.getLookAngle();
                projectile.shoot(direction.x(), direction.y(), direction.z(), velocity, hexedEffect(player));
                projectile.setOwner(player);
                serverLevel.addFreshEntity(projectile);
            }
        }
    }

    public void fireProjectile(Projectile projectile, LivingEntity player, float velocity){
        if(player != null){
            if(player.level() instanceof ServerLevel serverLevel){
                Vec3 direction = player.getLookAngle();
                projectile.shoot(direction.x(), direction.y(), direction.z(), velocity, hexedEffect(player));
                projectile.setOwner(player);
                serverLevel.addFreshEntity(projectile);
                Helpers.getSoundWithPositionV(projectile.level(), player.position(), SoundReg.ORB_FIRE.get(), 0.4f, 1f);
            }
        }
    }

    public void fireUtilityProjectile(Projectile projectile, LivingEntity player){
        if(player != null){
            if(player.level() instanceof ServerLevel serverLevel){
                Vec3 direction = player.getLookAngle();
                projectile.shoot(direction.x(), direction.y(), direction.z(), 1.2f, hexedEffect(player));
                projectile.setOwner(player);
                serverLevel.addFreshEntity(projectile);
                Helpers.getSoundWithPositionV(projectile.level(), player.position(), SoundEvents.BREEZE_CHARGE , 0.05f, 1.4f);
            }
        }
    }

    public static void fireMultiShotProjectile(int numberOfProjectile, float velocities, Player player, double adjustSpread, Supplier<Projectile> projectileSupplier){
        var totalWidth = (numberOfProjectile - 1) * adjustSpread;
        var startOffset = -totalWidth / 2.0;

        for (int i = 0; i < numberOfProjectile; i++) {
            double offset = numberOfProjectile == 1 ? 0 : startOffset + i * (totalWidth / (numberOfProjectile - 1));
            var projectile  = projectileSupplier.get();
            var directionOffset = calculateDirectionOffset(player, offset);
            var direction = player.getLookAngle().add(directionOffset).normalize();
            fireProjectileDirection(projectile, player, velocities, direction);
        }
    }
}
