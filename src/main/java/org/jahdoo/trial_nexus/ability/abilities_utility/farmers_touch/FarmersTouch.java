package org.jahdoo.trial_nexus.ability.abilities_utility.farmers_touch;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import org.jahdoo.common.block.chaos_cube.ChaosCubeEntity;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.trial_nexus.ability.AbstractUtilityProjectile;
import org.jahdoo.trial_nexus.ability.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.ability.UtilityHelpers;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.trial_nexus.utils.PositionFinders;
import org.shaydee.shaydeeapi.Helpers;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.level.block.SugarCaneBlock.AGE;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;
import static org.jahdoo.trial_nexus.ability.AbilityBuilder.RANGE;
import static org.jahdoo.trial_nexus.ability.abilities_utility.farmers_touch.FarmersTouchAbility.GROWTH_CHANCE;
import static org.jahdoo.trial_nexus.ability.abilities_utility.farmers_touch.FarmersTouchAbility.HARVEST_CHANCE;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public class FarmersTouch extends AbstractUtilityProjectile {

    private static final ResourceLocation abilityId = JahdooHelpers.res("farmers_touch_property");
    private final List<BlockPos> effectedPos = new ArrayList<>();
    private double counter = 0.05;
    private double harvestChance;
    private double growthChance;
    private double range;
    private boolean hasHitBlock;

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.range = this.getTag(RANGE);
        this.growthChance = this.getTag(GROWTH_CHANCE);
        this.harvestChance = this.getTag(HARVEST_CHANCE);
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new FarmersTouch();
    }

    @Override
    public String abilityId() {
        return FarmersTouchAbility.abilityId.getPath().intern();
    }

    @Override
    public void onTickMethod() {
        super.onTickMethod();
        if (!(generic.level() instanceof ServerLevel)) return;
        if(this.hasHitBlock) this.nova(generic, this.range);
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        super.onBlockBlockHit(blockHitResult);
        var level = this.generic.level();
        if(level instanceof CustomLevel) return;

        if(level.getBlockEntity(blockHitResult.getBlockPos()) instanceof ChaosCubeEntity) return;
        this.hasHitBlock = true;
        this.generic.setInvisible(true);
        this.generic.setDeltaMovement(0,0,0);
        PositionFinders.getOuterSquareOfRadius(this.generic.position(), counter + 0.5, this.range * 20,
            positions -> this.setParticleNova(positions, level)
        );
    }

    public void setParticleNova(Vec3 worldPosition, Level level){
        int col1 = this.getElementType().partColourA();
        int col2 = this.getElementType().partColourFade();
        var directions = worldPosition.subtract(this.generic.position()).normalize();
        var lifetime = (int) this.range * 1.5;
        var size = 3;

        var genericParticle = ParticleHandlers.genericParticle(SOFT_PARTICLE, Math.min(Math.max((int) lifetime, 3), 10), (float) (size - 0.2), col1, col2, false);
        var speedRange = Random.nextDouble(this.range / 10, this.range / 8);
        ParticleHandlers.sendParticles(
            level, genericParticle, worldPosition, 0, directions.x, directions.y+0.05, directions.z, speedRange
        );
    }

    void nova(Projectile projectile, double novaMaxSize){
        if(counter < novaMaxSize){
            counter = Math.min(counter + 0.5, novaMaxSize);
            PositionFinders.getOuterSquareOfRadius(projectile.position(), counter, counter*10,
                positions -> {
                    var level = projectile.level();
                    var pos = BlockPos.containing(positions);
                    this.applyBoneMeal(level, pos);
                    this.applyBoneMeal(level, pos.below());
                }
            );
        } else {
            projectile.discard();
        }
    }

    private void growAgeRelatedCrops(Level level, BlockPos pos, BlockState state) {
        var above = pos.above();
        if (level.isEmptyBlock(above) && state.hasProperty(AGE)) {
            int i;
            var block = state.getBlock();
            i = 1;
            while (level.getBlockState(pos.below(i)).is(block)) ++i;

            if (i < 3) {
                int j = state.getValue(AGE);
                if (CommonHooks.canCropGrow(level, pos, state, true)) {
                    if (j == 15) {
                        level.setBlockAndUpdate(above, block.defaultBlockState());
                        CommonHooks.fireCropGrowPost(level, above, block.defaultBlockState());
                        level.setBlock(pos, state.setValue(AGE, 0), 4);
                        harvest(level, above, state);
                    } else {
                        level.setBlock(pos, state.setValue(AGE, j + 1), 4);
                    }
                    return;
                }
            }
        }

        var stateAbove = level.getBlockState(above);
        if(!stateAbove.isAir()){
            if (stateAbove.hasProperty(AGE)) harvest(level, above, state);
        }
    }

    public void applyBoneMeal(Level level, BlockPos pos) {
        var blockstate = level.getBlockState(pos);
        if (level instanceof ServerLevel && !this.effectedPos.contains(pos)) {
            if(Random.nextInt(0, (int) harvestChance) == 0) {
                if(blockstate.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(blockstate)){
                    harvest(level, pos, blockstate);
                    level.setBlockAndUpdate(pos, cropBlock.getStateForAge(0));
                } else {
                    growAgeRelatedCrops(level, pos, blockstate);
                }
            } else {
                if (!(blockstate.getBlock() instanceof BonemealableBlock bonemealableblock)) return;
                if (!(bonemealableblock.isValidBonemealTarget(level, pos, blockstate))) return;
                if(growthChance == 0 || Random.nextInt(0, (int) growthChance) == 0){
                    BoneMealItem.applyBonemeal(ItemStack.EMPTY, level, pos, null);
                    Helpers.getSoundWithPosition(level, pos, SoundEvents.BONE_MEAL_USE);
                }
            }
            this.effectedPos.add(pos);
        }
    }

    private void harvest(Level level, BlockPos pos, BlockState blockstate) {
        UtilityHelpers.harvestBreaker(level, pos, false);
        utilityParticleBurst(level, pos.getCenter().add(0, 0.4, 0), 8, 1, 3, 0.1f);
        Helpers.getSoundWithPosition(generic.level(), pos, blockstate.getSoundType(level, pos, null).getBreakSound());
    }

}
