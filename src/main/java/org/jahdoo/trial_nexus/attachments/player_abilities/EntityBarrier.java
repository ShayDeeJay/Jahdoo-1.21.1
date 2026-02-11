package org.jahdoo.trial_nexus.attachments.player_abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.element.AbstractElement;

import static java.lang.Math.sqrt;
import static org.jahdoo.common.particle.ParticleHandlers.particleBurst;
import static org.jahdoo.trial_nexus.utils.DamageUtils.damageWithJahdoo;
import static org.jahdoo.trial_nexus.utils.PositionFinders.getRandomSphericalPositions;

public class EntityBarrier {

    // NOT AN ATTACHMENT

    private double expandRadius = 5;

    public void entityBarrier(
        Level level,
        Vec3 blockPos,
        AbstractElement getType
    ){

        int radius = 6;
        if(expandRadius < radius) expandRadius += 0.5;
        if (!(level instanceof ServerLevel serverLevel)) return;

        var pPosX = blockPos.x;
        var pPosZ = blockPos.z;
        var entities = level.getEntities(null, new AABB(BlockPos.containing(blockPos)).inflate(expandRadius - 1));

        getRandomSphericalPositions(blockPos, expandRadius, expandRadius * 20,
            positions -> level.addParticle(getType.getParticleGroup().magicSlow(), positions.x, positions.y, positions.z, 0,0,0)
        );

        for (var entity : entities) {
            var filterOutItem = !(entity instanceof ItemEntity);
            var filterOutProjectile = !(entity instanceof GenericProjectile);
            var filterOutPlays = !(entity instanceof Player);

            if (filterOutItem && filterOutProjectile && filterOutPlays) {
                var deltaX = entity.getX() - pPosX;
                var deltaZ = entity.getZ() - pPosZ;
                var distance = sqrt(deltaX * deltaX + deltaZ * deltaZ);

                if (distance > 0.0D) {
                    deltaX /= distance;
                    deltaZ /= distance;
                }

                if (entity instanceof Projectile) {
                    entity.setDeltaMovement(deltaX * 3, 0, deltaZ * 3);
                } else {
                    entity.setDeltaMovement(deltaX * 1, 0, deltaZ * 1);
                }

//                SoundHelpers.getSoundWithPosition(level, entity.blockPosition(), SoundEvents.BEACON_POWER_SELECT, 0.1f, 1.5f);
                damageWithJahdoo(entity, 1, ElementReg.mystic().damageTypeResourceKey());
                particleBurst(serverLevel, entity.position().add(0, entity.getBbHeight() / 2, 0), 5, getType.getParticleGroup().bakedSlow(), 0, 0, 0, 0.2f);
            }
        }
    }

}
