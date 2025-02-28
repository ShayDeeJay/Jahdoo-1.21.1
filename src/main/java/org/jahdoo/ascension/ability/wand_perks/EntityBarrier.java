package org.jahdoo.ascension.ability.wand_perks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;

import static java.lang.Math.*;
import static org.jahdoo.ascension.utils.DamageUtils.damageWithJahdoo;
import static org.jahdoo.ascension.utils.Helpers.getSoundWithPosition;
import static org.jahdoo.ascension.utils.PositionFinders.getRandomSphericalPositions;
import static org.jahdoo.common.particle.ParticleHandlers.particleBurst;

public class EntityBarrier {

    private double expandRadius = 0;

    public void entityBarrier(
        Level level,
        BlockPos blockPos,
        AbstractElement getType
    ){

        int radius = 6;
        if(expandRadius < radius) expandRadius += 0.5;
        if (!(level instanceof ServerLevel serverLevel)) return;

        var blockPosAdjusted = blockPos.getCenter();
        var pPosX = blockPosAdjusted.x;
        var pPosZ = blockPosAdjusted.z;
        var entities = level.getEntities(null, new AABB(blockPos).inflate(expandRadius - 1));

        getRandomSphericalPositions(blockPosAdjusted, expandRadius, expandRadius * 20,
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

                getSoundWithPosition(level, entity.blockPosition(), SoundEvents.BEACON_POWER_SELECT, 0.1f, 1.5f);
                damageWithJahdoo(entity, 1);
                particleBurst(serverLevel, entity.position().add(0, entity.getBbHeight() / 2, 0), 5, getType.getParticleGroup().bakedSlow(), 0, 0, 0, 0.2f);
            }
        }
    }

}
