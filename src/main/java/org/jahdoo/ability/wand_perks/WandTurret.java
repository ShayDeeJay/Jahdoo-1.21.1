package org.jahdoo.ability.wand_perks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.RandomUtils;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.utils.ModHelpers;

import java.util.List;

public class WandTurret {

    public void wandTurret(BlockPos blockPosition, Level level, AbstractElement getType){
        if(getType != null){
            if (ModHelpers.Random.nextInt(0, 10) == 0) {
                //Shooter Code
                List<LivingEntity> entities = level.getNearbyEntities(
                    LivingEntity.class,
                    TargetingConditions.DEFAULT,
                    null,
                    new AABB(blockPosition).inflate(20)
                );

                if (!entities.isEmpty()) {
                    LivingEntity captureEntity = entities.get(RandomUtils.nextInt(0, entities.size()));
                    Vec3 vec3 = new Vec3(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
                    Vec3 vec31 = new Vec3(captureEntity.getX(), captureEntity.getEyeY(), captureEntity.getZ());
                    boolean hasLineOfSight = level.clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, captureEntity)).getType() == HitResult.Type.MISS;
                    if (!(captureEntity instanceof Player) && hasLineOfSight) {
//                        GenericProjectile mysticProjectile = new GenericProjectile(captureEntity, blockPosition.getCenter().x, blockPosition.getCenter().y + 0.5, blockPosition.getCenter().z, getType.getTypeId(), 25, false);
//                        mysticProjectile.setAllowBuddyParticles(true);
                        Vec3 direction = new Vec3(
                            (float) (captureEntity.getX() - blockPosition.getCenter().x),
                            (float) (captureEntity.getY() + captureEntity.getBbHeight() / 2 - blockPosition.getCenter().y),
                            (float) (captureEntity.getZ() - blockPosition.getCenter().z)
                        ).normalize();

//                        mysticProjectile.shoot(direction.x(), direction.y(), direction.z(), 0.6f, 0);
//                        mysticProjectile.setOwner(null);
//                        GeneralHelpers.getSoundWithPosition(level, blockPosition, SoundRegister.ORB_FIRE.get(), 0.5f, 1.5f);
//                        level.addFreshEntity(mysticProjectile);
                    }
                }
            }
        }
    }

}
