package org.jahdoo.ability.abilities.ability.utility;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractUtilityProjectile;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.UtilityHelpers;
import org.jahdoo.ability.abilities.ability_data.Utility.BlockBombAbility;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.utils.PositionGetters;

import static org.jahdoo.ability.UtilityHelpers.*;
import static org.jahdoo.ability.abilities.ability_data.Utility.BlockBombAbility.BLOCK_DROP_CHANCE;
import static org.jahdoo.ability.abilities.ability_data.Utility.BlockBombAbility.EXPLOSION_RANGE;
import static org.jahdoo.particle.ParticleHandlers.*;
import static org.jahdoo.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
import static org.jahdoo.utils.ModHelpers.Random;

public class BlockExplosion extends AbstractUtilityProjectile {
    ResourceLocation abilityId = ModHelpers.res("block_bomb_property");
    boolean hasHitBlock;
    int totalRadius;
    int explosionTimer;
    private final int explosionTimerMax = 50;
    int totalRadiusMax;
    int blockDropChance;
    double projectileSphere;

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.totalRadiusMax = (int) this.getTagUtility(EXPLOSION_RANGE);
        this.blockDropChance = (int) this.getTagUtility(BLOCK_DROP_CHANCE);
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        super.onBlockBlockHit(blockHitResult);
        if(this.genericProjectile.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ModularChaosCubeEntity) return;
        this.hasHitBlock = true;
        ModHelpers.getSoundWithPosition(genericProjectile.level(), genericProjectile.blockPosition(), SoundEvents.SLIME_BLOCK_PLACE, 1.5f);
        genericProjectile.setDeltaMovement(0, 0, 0);
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

    private Level level(){
        return this.genericProjectile.level();
    }

    private void coreParticles(Level level) {
        var bakedParticleOption = bakedParticleOptions(getElementType().getTypeId(), 2, 3f, false);
        PositionGetters.getRandomSphericalPositions(genericProjectile, projectileSphere, projectileSphere * 10,
            radiusPosition -> explosionParticle(level, radiusPosition, bakedParticleOption)
        );
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
            var colour = getElementType().particleColourPrimary();
            var fade = getElementType().particleColourFaded();
            var genericParticle = genericParticleOptions(GENERIC_PARTICLE_SELECTION, 6, 3, colour, fade, false);
            var add = genericProjectile.position().add(0, 0.2, 0);
            tickingSound();
            particleBurst(level, add, totalRadiusMax / 3, genericParticle, 0, -0.1, 0, Random.nextFloat(0.1f, 0.3f));
        }
    }

    private void explodingTick(Level level) {
        var bakedParticleOptions = bakedParticleOptions(getElementType().getTypeId(), 4, 4f, false);
        var genericParticle = genericParticleOptions(GENERIC_PARTICLE_SELECTION, getElementType(), 10, 4, 1);
        if (explosionTimer >= explosionTimerMax) {

            ParticleHandlers.sendParticles(
                level, bakedParticleOptions, genericProjectile.position().add(0, 0.2, 0),
                totalRadiusMax, 0.05, 0.05, 0.05, (double) totalRadiusMax / 15
            );

            ParticleHandlers.sendParticles(
                level, genericParticle, genericProjectile.position().add(0, 0.2, 0),
                totalRadiusMax, 0.05, 0.05, 0.05, (double) totalRadiusMax / 15
            );

            ModHelpers.getSoundWithPosition(genericProjectile.level(), genericProjectile.blockPosition(), SoundRegister.EXPLOSION.get(), 2f);
            handleItemsAndExplosion(level);
            if (totalRadius <= totalRadiusMax) totalRadius++;
        }
    }

    private void isMoving() {
        var x = genericProjectile.getDeltaMovement().x;
        var y = genericProjectile.getDeltaMovement().y - projectileSphere / 50;
        var z = genericProjectile.getDeltaMovement().z;
        genericProjectile.setDeltaMovement(x, y, z);
        if (genericProjectile.tickCount % 12 == 0) tickingSound();
    }

    private void handleItemsAndExplosion(Level level) {
        PositionGetters.getSphericalBlockPositions(genericProjectile, totalRadius,
            radiusPosition -> {
                BlockState blockstate = genericProjectile.level().getBlockState(radiusPosition);
                if (blockstate.isAir()) return;
                var range = destroySpeed(radiusPosition, genericProjectile.level());
                if (!UtilityHelpers.range.contains(range)) return;

                if (Random.nextInt(0, this.blockDropChance) == 0) {
                    dropItemsOrBlock(genericProjectile, radiusPosition, false, false);
                }

                var blockPart = new BlockParticleOption(ParticleTypes.BLOCK, blockstate);
                ParticleHandlers.sendParticles(
                    level, blockPart, radiusPosition.getCenter(), 1, 0, 0, 0, 0.1
                );

                genericProjectile.level().removeBlock(radiusPosition, false);
            }
        );
    }

    private void tickingSound() {
        ModHelpers.getSoundWithPosition(genericProjectile.level(), genericProjectile.blockPosition(), SoundRegister.TIMER.get(), 1f, 1.5f);
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
        return new BlockExplosion();
    }
}
