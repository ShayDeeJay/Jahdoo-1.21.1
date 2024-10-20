package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities.utility;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.UtilityHelpers;
import org.jahdoo.all_magic.all_abilities.abilities.ArcaneShiftAbility;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.particle.ParticleHandlers;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.level.block.Blocks.AIR;

public class BlockExplosion extends AbstractUtilityProjectile {
    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("block_bomb_property");
    boolean hasHitBlock;
    int totalRadius;
    int explosionTimer;
    int explosionTimerMax = 50;
    int totalRadiusMax = 50;
    double projectileSphere;
    boolean keepItems = false;
    int itemsDroppedIndex = 0;  // New variable to track the index of the next item to drop
    List<ItemStack> destroyedItems = new ArrayList<>();

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new BlockExplosion();
    }

    @Override
    public double getTag(String name) {
        var wandAbilityHolder = this.genericProjectile.wandAbilityHolder();
        return GeneralHelpers.getModifierValue(wandAbilityHolder, ArcaneShiftAbility.abilityId.getPath().intern()).get(name).actualValue();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.hasHitBlock = true;
        GeneralHelpers.getSoundWithPosition(genericProjectile.level(), genericProjectile.blockPosition(), SoundEvents.SLIME_BLOCK_PLACE, 1.5f);
        genericProjectile.setDeltaMovement(0, 0, 0);
    }

    private AbstractElement element(){
        return ElementRegistry.UTILITY.get();
    }

    @Override
    public void discardCondition() {
        if (genericProjectile.tickCount > 400 && !hasHitBlock) genericProjectile.discard();
    }

    @Override
    public void onTickMethod() {
        super.onTickMethod();
        if (genericProjectile.level() instanceof ServerLevel serverLevel) {
            if (projectileSphere < (double) totalRadiusMax / 10) projectileSphere += 0.05;
            if (explosionTimer < explosionTimerMax) {
                this.coreParticles(serverLevel);
            } else {
                this.outerExplosion(serverLevel);
            }
            if (hasHitBlock) {
                explosionTimer++;
                if (explosionTimer <= explosionTimerMax + totalRadiusMax) {
                    timerTick(serverLevel);
                    explodingTick(serverLevel);
                }
            } else {
                this.isMoving();
            }
            this.dropItemsOrDiscard(serverLevel);
        }
    }

    private void coreParticles(ServerLevel serverLevel){
        BakedParticleOptions bakedParticleOptions = new BakedParticleOptions(element().getTypeId(),2,3f, false);
        GeneralHelpers.getRandomSphericalPositions(genericProjectile, projectileSphere, projectileSphere * 10,
            radiusPosition -> {
                GeneralHelpers.generalHelpers.sendParticles(serverLevel, bakedParticleOptions, radiusPosition.add(0,0.1,0), 1,
                    GeneralHelpers.Random.nextDouble(0.1, 0.2),
                    GeneralHelpers.Random.nextDouble(0.1, 0.2),
                    GeneralHelpers.Random.nextDouble(0.1, 0.2),
                    GeneralHelpers.Random.nextDouble(0.05, 0.1)
                );
            }
        );
    }

    private void outerExplosion(ServerLevel serverLevel){
        BakedParticleOptions bakedParticleOptions = new BakedParticleOptions(element().getTypeId(),10,3f, false);
        GenericParticleOptions genericParticleOptions = new GenericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, element().particleColourPrimary(), element().particleColourFaded(), 10, 3,false, 1);
        if (totalRadius <= totalRadiusMax  + 1){
            GeneralHelpers.getRandomSphericalPositions(genericProjectile, totalRadius - 1, totalRadius * 4,
                radiusPosition -> {
                    GeneralHelpers.generalHelpers.sendParticles(serverLevel, bakedParticleOptions, radiusPosition.add(0,0.1,0), 1,
                        GeneralHelpers.Random.nextDouble(0.1, 0.2),
                        GeneralHelpers.Random.nextDouble(0.1, 0.2),
                        GeneralHelpers.Random.nextDouble(0.1, 0.2),
                        GeneralHelpers.Random.nextDouble(0.05, 0.1)
                    );
                    GeneralHelpers.generalHelpers.sendParticles(serverLevel, genericParticleOptions, radiusPosition.add(0,0.1,0), 1,
                        GeneralHelpers.Random.nextDouble(0.1, 0.2),
                        GeneralHelpers.Random.nextDouble(0.1, 0.2),
                        GeneralHelpers.Random.nextDouble(0.1, 0.2),
                        GeneralHelpers.Random.nextDouble(0.05, 0.1)
                    );
                }
            );
        }
    }

    private void timerTick(ServerLevel serverLevel){
        if (explosionTimer % 10 == 0 && !(explosionTimer >= explosionTimerMax)) {
            GenericParticleOptions genericParticleOptions = new GenericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, element().particleColourPrimary(), element().particleColourFaded(), 20, 3,false, 1);
            GeneralHelpers.getSoundWithPosition(genericProjectile.level(), genericProjectile.blockPosition(), SoundRegister.TIMER.get(), 1f);
            ParticleHandlers.spawnPoof(serverLevel, genericProjectile.position().add(0, 0.2, 0), totalRadiusMax/3, genericParticleOptions, 0, -0.1, 0, 0.08f);
        }
    }

    private void explodingTick(ServerLevel serverLevel){
        BakedParticleOptions bakedParticleOptions = new BakedParticleOptions(element().getTypeId(),4,4f, false);
        GenericParticleOptions genericParticleOptions = new GenericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, element().particleColourPrimary(), element().particleColourFaded(), 10, 4,false, 1);
        if (explosionTimer >= explosionTimerMax) {

            GeneralHelpers.generalHelpers.sendParticles(
                serverLevel, bakedParticleOptions, genericProjectile.position().add(0,0.2,0),
                totalRadiusMax, 0.05, 0.05, 0.05, (double) totalRadiusMax / 15
            );

            GeneralHelpers.generalHelpers.sendParticles(
                serverLevel, genericParticleOptions, genericProjectile.position().add(0,0.2,0),
                totalRadiusMax, 0.05, 0.05, 0.05, (double) totalRadiusMax / 15
            );

            GeneralHelpers.getSoundWithPosition(genericProjectile.level(), genericProjectile.blockPosition(), SoundRegister.EXPLOSION.get(), 2f);
            handleItemsAndExplosion(serverLevel);
            if (totalRadius <= totalRadiusMax) totalRadius++;
        }
    }

    private void isMoving(){
        genericProjectile.setDeltaMovement(genericProjectile.getDeltaMovement().x, genericProjectile.getDeltaMovement().y - projectileSphere / 50, genericProjectile.getDeltaMovement().z);
        if (genericProjectile.tickCount % 12 == 0) {
            GeneralHelpers.getSoundWithPosition(genericProjectile.level(), genericProjectile.blockPosition(), SoundRegister.TIMER.get(), 1f);
        }
    }

    private void dropItemsOrDiscard(ServerLevel serverLevel){
        if (explosionTimer >= explosionTimerMax + 20) {
            if (!destroyedItems.isEmpty()) {
                for (int i = 0; i < 60 && itemsDroppedIndex < destroyedItems.size(); i++) {
                    genericProjectile.spawnAtLocation(destroyedItems.get(itemsDroppedIndex));
                    itemsDroppedIndex++;
                }
                ParticleHandlers.spawnPoof(serverLevel, genericProjectile.position().add(0, 0.2, 0), totalRadiusMax, element().getParticleGroup().genericSlow(), 0, 0, 0, 0.1f);
            } else {
                genericProjectile.discard();
            }
            if (itemsDroppedIndex >= destroyedItems.size()) genericProjectile.discard();
        }
    }

    private void handleItemsAndExplosion(ServerLevel serverLevel){
        GeneralHelpers.getSphericalBlockPositions(genericProjectile, totalRadius,
            radiusPosition -> {
                var fluidState = genericProjectile.level().getFluidState(radiusPosition);
                if (UtilityHelpers.range.contains(UtilityHelpers.destroySpeed(radiusPosition, genericProjectile.level()))) {
                    BlockState blockstate = genericProjectile.level().getBlockState(radiusPosition);
                    if(!blockstate.isAir()){
                        if (keepItems) {
                            LootParams.Builder lootparams$builder = (new LootParams.Builder((ServerLevel) genericProjectile.level())).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(radiusPosition)).withParameter(LootContextParams.TOOL, new ItemStack(Items.DIAMOND_PICKAXE));
                            this.destroyedItems.addAll(genericProjectile.level().getBlockState(radiusPosition).getDrops(lootparams$builder));
                        }
                        GeneralHelpers.generalHelpers.sendParticles(serverLevel, new BlockParticleOption(ParticleTypes.BLOCK, blockstate), radiusPosition.getCenter(), 1, 0, 0, 0, 0.1);
                        genericProjectile.level().removeBlock(radiusPosition, false);

                    }
                }
                if(!fluidState.isEmpty()){
                    genericProjectile.level().setBlock(radiusPosition, AIR.defaultBlockState(), 3);
                }
            }
        );
    }
}
