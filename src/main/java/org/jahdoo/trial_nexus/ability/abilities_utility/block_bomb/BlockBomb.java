package org.jahdoo.trial_nexus.ability.abilities_utility.block_bomb;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.block.chaos_cube.ChaosCubeEntity;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.ability.AbstractUtilityProjectile;
import org.jahdoo.trial_nexus.ability.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.ability.UtilityHelpers;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;

import static org.jahdoo.common.particle.ParticleHandlers.*;
import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.trial_nexus.ability.UtilityHelpers.destroySpeed;
import static org.jahdoo.trial_nexus.ability.abilities_utility.block_bomb.BlockBombAbility.EXPLOSION_RANGE;
import static org.jahdoo.trial_nexus.utils.Helpers.Random;

public class BlockBomb extends AbstractUtilityProjectile {

    private static final ResourceLocation abilityId = Helpers.res("block_bomb_property");
    private static final int explosionTimerMax = 50;
    private double projectileSphere;
    private boolean hasHitBlock;
    private int totalRadius;
    private int explosionTimer;
    private int totalRadiusMax;
    private int blockDropChance;

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.totalRadiusMax = (int) this.getTag(EXPLOSION_RANGE);
//        this.blockDropChance = (int) this.getTag(BLOCK_DROP_CHANCE);
    }

    private Level level(){
        return this.generic.level();
    }

    private void tickingSound() {
        Helpers.getSoundWithPosition(generic.level(), generic.blockPosition(), SoundReg.TIMER.get(), 1f, 1.5f);
    }

    @Override
    public String abilityId() {
        return BlockBombAbility.abilityId.getPath().intern();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new BlockBomb();
    }

    private void coreParticles(Level level) {
        var bakedParticleOption = bakedParticle(getElementType().id(), 2, 3f, false);
        PositionFinders.getRandomSphericalPositions(generic, projectileSphere, projectileSphere * 10,
            radiusPosition -> explosionParticle(level, radiusPosition, bakedParticleOption)
        );
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        super.onBlockBlockHit(blockHitResult);
        if(this.generic.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ChaosCubeEntity) return;
        this.hasHitBlock = true;
        Helpers.getSoundWithPosition(generic.level(), generic.blockPosition(), SoundEvents.SLIME_BLOCK_PLACE, 1.5f);
        generic.setDeltaMovement(0, 0, 0);
    }

    private void isMoving() {
        var x = generic.getDeltaMovement().x;
        var y = generic.getDeltaMovement().y - projectileSphere / 50;
        var z = generic.getDeltaMovement().z;
        generic.setDeltaMovement(x, y, z);
        if (generic.tickCount % 12 == 0) tickingSound();
    }

    private static void explosionParticle(Level level, Vec3 radiusPosition, ParticleOptions genericParticleOptions) {
        ParticleHandlers.sendParticles(level, genericParticleOptions, radiusPosition.add(0, 0.1, 0), 1,
            Random.nextDouble(0.1, 0.2),
            Random.nextDouble(0.1, 0.2),
            Random.nextDouble(0.1, 0.2),
            Random.nextDouble(0.05, 0.1)
        );
    }

    private void timerTick(Level level) {
        if (explosionTimer % 10 == 0 && !(explosionTimer >= explosionTimerMax)) {
            var colour = getElementType().partColourA();
            var fade = getElementType().partColourFade();
            var genericParticle = genericParticle(GENERIC_PARTICLE, 6, 3, colour, fade, false);
            var add = generic.position().add(0, 0.2, 0);
            tickingSound();
            particleBurst(level, add, totalRadiusMax / 3, genericParticle, 0, -0.1, 0, Random.nextFloat(0.1f, 0.3f));
        }
    }

    @Override
    public void onTickMethod() {
        super.onTickMethod();
        if (projectileSphere < (double) totalRadiusMax / 10) projectileSphere += 0.05;
        if (explosionTimer < explosionTimerMax) this.coreParticles(level());

        if (hasHitBlock) {
            explosionTimer++;
            if (totalRadius <= totalRadiusMax) {
                timerTick(level());
                explodingTick(level());
            }
        } else this.isMoving();
    }

    private void explodingTick(Level level) {
        var bakedParticleOptions = bakedParticle(getElementType().id(), 4, 4f, false);
        var genericParticle = genericParticle(GENERIC_PARTICLE, getElementType(), 10, 4, 1);
        if (explosionTimer >= explosionTimerMax) {

            ParticleHandlers.sendParticles(
                level, bakedParticleOptions, generic.position().add(0, 0.2, 0),
                totalRadiusMax, 0.05, 0.05, 0.05, (double) totalRadiusMax / 15
            );

            ParticleHandlers.sendParticles(
                level, genericParticle, generic.position().add(0, 0.2, 0),
                totalRadiusMax, 0.05, 0.05, 0.05, (double) totalRadiusMax / 15
            );

            Helpers.getSoundWithPosition(generic.level(), generic.blockPosition(), SoundReg.EXPLOSION.get(), 2f);
            handleItemsAndExplosion(level);
            if (totalRadius <= totalRadiusMax) totalRadius++;
        }
    }

    private void handleItemsAndExplosion(Level level) {
        PositionFinders.getSphericalBlockPositions(generic, totalRadius,
            radiusPosition -> {
                var blockstate = level.getBlockState(radiusPosition);
                if (blockstate.isAir()) return;

                var fluidState = level.getFluidState(radiusPosition);
                if(!fluidState.isEmpty()){
                    level.setBlockAndUpdate(radiusPosition, Blocks.AIR.defaultBlockState());
                    return;
                }

                var range = destroySpeed(radiusPosition, level);
                if (!UtilityHelpers.range.contains(range)) return;

                if(!(blockstate.getBlock() instanceof DropExperienceBlock)){
                    level.removeBlock(radiusPosition, false);
                }
            }
        );
    }

}
