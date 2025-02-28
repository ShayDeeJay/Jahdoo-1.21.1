package org.jahdoo.ascension.ability.wand_perks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;

public class WandTurret {

    public void wandTurret(BlockPos blockPosition, Level level, AbstractElement getType){
        if(getType == null || Helpers.Random.nextInt(0, 10) != 0) return;
        var entities = level.getEntities(null, new AABB(blockPosition).inflate(20));

        for (var entity : entities) {
            var vec3 = new Vec3(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
            var vec31 = new Vec3(entity.getX(), entity.getEyeY(), entity.getZ());
            var hasLineOfSight = level.clip(new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS;
            var notPlayer = !(entity instanceof Player);
            if (notPlayer && hasLineOfSight) {
//                GenericProjectile mysticProjectile = new GenericProjectile(entity, blockPosition.getCenter().x, blockPosition.getCenter().y + 0.5, blockPosition.getCenter().z, getType.getTypeId(), 25, false);
//                mysticProjectile.setAllowBuddyParticles(true);

//                Vec3 direction = new Vec3(
//                    (float) (entity.getX() - blockPosition.getCenter().x),
//                    (float) (entity.getY() + entity.getBbHeight() / 2 - blockPosition.getCenter().y),
//                    (float) (entity.getZ() - blockPosition.getCenter().z)
//                ).normalize();

//                mysticProjectile.shoot(direction.x(), direction.y(), direction.z(), 0.6f, 0);
//                mysticProjectile.setOwner(null);
//                GeneralHelpers.getSoundWithPosition(level, blockPosition, SoundRegister.ORB_FIRE.get(), 0.5f, 1.5f);
//                level.addFreshEntity(mysticProjectile);
            }
        }
    }

}
