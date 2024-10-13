package org.jahdoo.all_magic.wand_perks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.particle.ParticleHandlers;

public class EntityBarrier {
    int radius = 6;
    double expandRadius = 0;

    public void entityBarrier(
        Level level,
        BlockPos blockPos,
        AbstractElement getType
    ){
        Vec3 blockPosAdjusted = blockPos.getCenter();
        if(expandRadius < radius) expandRadius += 0.5;
        GeneralHelpers.getRandomSphericalPositions(blockPosAdjusted, expandRadius, expandRadius * 20,
            positions -> {
                level.addParticle(getType.getParticleGroup().magicSlow(), positions.x, positions.y, positions.z, 0,0,0);
//                    GeneralHelpers.generalHelpers.sendParticles(serverLevel, getType.getParticleGroup().magicSlow(), positions, 0, 0, 0, 0, 0.4);
            }
        );
        if (level instanceof ServerLevel serverLevel) {
            //projection bubble
            double pPosX = blockPosAdjusted.x;
            double pPosZ = blockPosAdjusted.z;


            level.getEntities(null, new AABB(blockPos).inflate(expandRadius - 1)).forEach(
                entity -> {
                    if (!(entity instanceof ItemEntity) && !(entity instanceof GenericProjectile) && !(entity instanceof Player) ) {// Calculate direction vector from entity to projectile
                        double deltaX = entity.getX() - pPosX;
                        double deltaZ = entity.getZ() - pPosZ;

                        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                        if (distance > 0.0D) {
                            deltaX /= distance;
                            deltaZ /= distance;
                        }
                        if (entity instanceof Projectile) {
                            entity.setDeltaMovement(deltaX * 3, 0, deltaZ * 3);
                        } else {
                            entity.setDeltaMovement(deltaX * 1, 0, deltaZ * 1);
                        }

                        // add force field push back sound
                        GeneralHelpers.getSoundWithPosition(level, entity.blockPosition(), SoundEvents.BEACON_POWER_SELECT, 0.1f, 1.5f);
                        entity.hurt(level.damageSources().magic(), 1);
                        ParticleHandlers.spawnPoof(serverLevel, entity.position().add(0, entity.getBbHeight() / 2, 0), 5, getType.getParticleGroup().bakedSlow(), 0, 0, 0, 0.2f);
                    }
                }
            );
        }
    }

}
