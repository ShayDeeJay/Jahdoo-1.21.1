package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities.utility;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.UtilityHelpers;
import org.jahdoo.all_magic.all_abilities.abilities.Utility.BlockBombAbility;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.utils.PositionGetters;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.all_magic.all_abilities.abilities.Utility.BlockBombAbility.EXPLOSION_RANGE;

public class BlockExplosion extends AbstractUtilityProjectile {
    ResourceLocation abilityId = ModHelpers.res("block_bomb_property");
    boolean hasHitBlock;
    int totalRadius;
    int explosionTimer;
    int explosionTimerMax = 50;
    int totalRadiusMax;

    double projectileSphere;
    boolean keepItems = false;
    int itemsDroppedIndex = 0;  // New variable to track the index of the next item to drop
    List<ItemStack> destroyedItems = new ArrayList<>();

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.totalRadiusMax = (int) this.getTag(EXPLOSION_RANGE);
    }

    @Override
    public double getTag(String name) {
        var wandAbilityHolder = this.genericProjectile.wandAbilityHolder();
        return ModHelpers.getModifierValue(wandAbilityHolder, BlockBombAbility.abilityId.getPath().intern()).get(name).setValue();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new BlockExplosion();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        if(this.genericProjectile.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ModularChaosCubeEntity) return;
        this.hasHitBlock = true;
        ModHelpers.getSoundWithPosition(genericProjectile.level(), genericProjectile.blockPosition(), SoundEvents.SLIME_BLOCK_PLACE, 1.5f);
        genericProjectile.setDeltaMovement(0, 0, 0);
        super.onBlockBlockHit(blockHitResult);
    }

    private AbstractElement element() {
        return ElementRegistry.UTILITY.get();
    }

    @Override
    public void discardCondition() {
//        if (genericProjectile.tickCount > 400 && !hasHitBlock) genericProjectile.discard();
    }

    @Override
    public void onTickMethod() {
        super.onTickMethod();
        if (projectileSphere < (double) totalRadiusMax / 10) projectileSphere += 0.05;
        if (explosionTimer < explosionTimerMax) this.coreParticles(level()); else this.outerExplosion(level());

        if (hasHitBlock) {
            explosionTimer++;
            if (totalRadius <= totalRadiusMax) {
                timerTick(level());
                explodingTick(level());
            }
        } else {
            this.isMoving();
        }

        this.dropItemsOrDiscard(level());
    }

    private Level level(){
        return this.genericProjectile.level();
    }

    private void coreParticles(Level level) {
        var bakedParticleOptions = new BakedParticleOptions(element().getTypeId(), 2, 3f, false);
        PositionGetters.getRandomSphericalPositions(genericProjectile, projectileSphere, projectileSphere * 10,
            radiusPosition -> {
                explosionParticle(level, radiusPosition, bakedParticleOptions);
            }
        );
    }

    private void outerExplosion(Level level) {
        var genericParticleSelection = ParticleStore.GENERIC_PARTICLE_SELECTION;
        var colour = element().particleColourPrimary();
        var fade = element().particleColourFaded();
        var bakedParticleOptions = new BakedParticleOptions(element().getTypeId(), 10, 3f, false);
        var genericParticleOptions = new GenericParticleOptions(genericParticleSelection, colour, fade, 10, 3, false, 1);
        if (totalRadius <= totalRadiusMax + 1) {
            PositionGetters.getRandomSphericalPositions(genericProjectile, totalRadius - 1, totalRadius * 4,
                radiusPosition -> {
                    explosionParticle(level, radiusPosition, bakedParticleOptions);
                    explosionParticle(level, radiusPosition, genericParticleOptions);
                }
            );
        }
    }

    private static void explosionParticle(Level level, Vec3 radiusPosition, ParticleOptions genericParticleOptions) {
        ParticleHandlers.sendParticles(level, genericParticleOptions, radiusPosition.add(0, 0.1, 0), 1,
            ModHelpers.Random.nextDouble(0.1, 0.2),
            ModHelpers.Random.nextDouble(0.1, 0.2),
            ModHelpers.Random.nextDouble(0.1, 0.2),
            ModHelpers.Random.nextDouble(0.05, 0.1)
        );
    }

    private void timerTick(Level level) {
        if (explosionTimer % 10 == 0 && !(explosionTimer >= explosionTimerMax)) {
            var colour = element().particleColourPrimary();
            var fade = element().particleColourFaded();
            var genericParticleSelection = ParticleStore.GENERIC_PARTICLE_SELECTION;
            var genericParticleOptions = new GenericParticleOptions(genericParticleSelection, colour, fade, 20, 3, false, 1);
            var add = genericProjectile.position().add(0, 0.2, 0);
            ModHelpers.getSoundWithPosition(genericProjectile.level(), genericProjectile.blockPosition(), SoundRegister.TIMER.get(), 1f);
            ParticleHandlers.particleBurst(level, add, totalRadiusMax / 3, genericParticleOptions, 0, -0.1, 0, 0.08f);
        }
    }

    private void explodingTick(Level level) {
        var colour = element().particleColourPrimary();
        var fade = element().particleColourFaded();
        var genericParticleSelection = ParticleStore.GENERIC_PARTICLE_SELECTION;
        var bakedParticleOptions = new BakedParticleOptions(element().getTypeId(), 4, 4f, false);
        GenericParticleOptions genericParticleOptions = new GenericParticleOptions(genericParticleSelection, colour, fade, 10, 4, false, 1);
        if (explosionTimer >= explosionTimerMax) {

            ParticleHandlers.sendParticles(
                level, bakedParticleOptions, genericProjectile.position().add(0, 0.2, 0),
                totalRadiusMax, 0.05, 0.05, 0.05, (double) totalRadiusMax / 15
            );

            ParticleHandlers.sendParticles(
                level, genericParticleOptions, genericProjectile.position().add(0, 0.2, 0),
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
        if (genericProjectile.tickCount % 12 == 0) {
            ModHelpers.getSoundWithPosition(genericProjectile.level(), genericProjectile.blockPosition(), SoundRegister.TIMER.get(), 1f);
        }
    }

    private void dropItemsOrDiscard(Level level) {
        if (totalRadius >= totalRadiusMax) {
            if (!destroyedItems.isEmpty()) {
                for (int i = 0; i < 60 && itemsDroppedIndex < destroyedItems.size(); i++) {
                    genericProjectile.spawnAtLocation(destroyedItems.get(itemsDroppedIndex));
                    itemsDroppedIndex++;
                }
                var add = genericProjectile.position().add(0, 0.2, 0);
                var particleOptions = element().getParticleGroup().genericSlow();

                ParticleHandlers.particleBurst(level, add, totalRadiusMax, particleOptions, 0, 0, 0, 0.1f);
            } else {
                genericProjectile.discard();
            }
            if (itemsDroppedIndex >= destroyedItems.size()) genericProjectile.discard();
        }
    }

    private void handleItemsAndExplosion(Level level) {
        PositionGetters.getSphericalBlockPositions(genericProjectile, totalRadius,
            radiusPosition -> {
                BlockState blockstate = genericProjectile.level().getBlockState(radiusPosition);
                if (blockstate.isAir()) return;
                var range = UtilityHelpers.destroySpeed(radiusPosition, genericProjectile.level());

                if (UtilityHelpers.range.contains(range)) {
                    if (keepItems) {
                        if(level instanceof ServerLevel serverLevel){
                            var lootparams$builder = new LootParams.Builder(serverLevel)
                                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(radiusPosition))
                                .withParameter(LootContextParams.TOOL, new ItemStack(Items.DIAMOND_PICKAXE));
                            this.destroyedItems.addAll(genericProjectile.level().getBlockState(radiusPosition).getDrops(lootparams$builder));
                        }
                    }

                    ParticleHandlers.sendParticles(
                        level,
                        new BlockParticleOption(ParticleTypes.BLOCK, blockstate),
                        radiusPosition.getCenter(),
                        1, 0, 0, 0, 0.1
                    );

                    genericProjectile.level().removeBlock(radiusPosition, false);
                }
            }
        );
    }
}
