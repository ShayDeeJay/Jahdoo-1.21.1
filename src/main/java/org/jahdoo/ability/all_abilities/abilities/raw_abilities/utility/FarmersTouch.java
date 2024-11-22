package org.jahdoo.ability.all_abilities.abilities.raw_abilities.utility;

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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractUtilityProjectile;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.UtilityHelpers;
import org.jahdoo.ability.all_abilities.abilities.Utility.FarmersTouchAbility;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.ability.AbilityBuilder.*;
import static org.jahdoo.ability.all_abilities.abilities.Utility.FarmersTouchAbility.*;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.SOFT_PARTICLE_SELECTION;
import static org.jahdoo.utils.ModHelpers.Random;

public class FarmersTouch extends AbstractUtilityProjectile {
    ResourceLocation abilityId = ModHelpers.res("farmers_touch_property");
    boolean hasHitBlock;
    double counter = 0.05;
    double range;
    double growthChance;
    private double harvestChance;
    private final List<BlockPos> effectedPos = new ArrayList<>();

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.range = this.getTagUtility(RANGE);
        this.growthChance = this.getTagUtility(GROWTH_CHANCE);
        this.harvestChance = this.getTagUtility(HARVEST_CHANCE);
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
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        if(this.genericProjectile.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ModularChaosCubeEntity) return;
        this.hasHitBlock = true;
        this.genericProjectile.setInvisible(true);
        this.genericProjectile.setDeltaMovement(0,0,0);
        PositionGetters.getOuterSquareOfRadius(this.genericProjectile.position(), counter + 0.5, this.range * 20,
            positions -> this.setParticleNova(positions, this.genericProjectile.level())
        );
        super.onBlockBlockHit(blockHitResult);
    }

    public void applyBoneMeal(Level level, BlockPos pPos) {
        var blockstate = level.getBlockState(pPos);
        if (level instanceof ServerLevel && !this.effectedPos.contains(pPos)) {
            if(Random.nextInt(0, (int) harvestChance) == 0) {
                if(blockstate.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(blockstate)){
                    UtilityHelpers.harvestBreaker(genericProjectile, pPos, false);
                    level.setBlockAndUpdate(pPos, cropBlock.getStateForAge(0));
                    utilityParticleBurst(level, pPos.getCenter().add(0, 0.4, 0), 8, 1, 3, 0.1f);
                    ModHelpers.getSoundWithPosition(genericProjectile.level(), pPos, blockstate.getSoundType(level, pPos, null).getBreakSound());
                }
            } else {
                if (!(blockstate.getBlock() instanceof BonemealableBlock bonemealableblock)) return;
                if (!(bonemealableblock.isValidBonemealTarget(level, pPos, blockstate))) return;
                if(growthChance == 0 || Random.nextInt(0, (int) growthChance) == 0){
                    BoneMealItem.applyBonemeal(ItemStack.EMPTY, level, pPos, null);
                    ModHelpers.getSoundWithPosition(level, pPos, SoundEvents.BONE_MEAL_USE);
                }
            }
            this.effectedPos.add(pPos);
        }
    }

    void nova(Projectile projectile, double novaMaxSize){
        if(counter < novaMaxSize){
            counter = Math.min(counter + 0.5, novaMaxSize);
            PositionGetters.getOuterSquareOfRadius(projectile.position(), counter, counter*10,
                positions -> {
                    this.applyBoneMeal(projectile.level(), BlockPos.containing(positions));
                    this.applyBoneMeal(projectile.level(), BlockPos.containing(positions).below());
                }
            );
        } else {
            projectile.discard();
        }
    }

    private void setParticleNova(Vec3 worldPosition, Level level){
        int col1 = this.getElementType().particleColourPrimary();
        int col2 = this.getElementType().particleColourFaded();
        var directions = worldPosition.subtract(this.genericProjectile.position()).normalize();
        var lifetime = (int) this.range * 1.5;
        var size = 3;

        var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, Math.min(Math.max((int) lifetime, 3), 10), (float) (size - 0.2), col1, col2, false);
        var speedRange = Random.nextDouble(this.range / 10, this.range / 8);
        ParticleHandlers.sendParticles(
            level, genericParticle, worldPosition, 0, directions.x, directions.y+0.05, directions.z, speedRange
        );
    }

    @Override
    public void onTickMethod() {
        super.onTickMethod();
        if (!(genericProjectile.level() instanceof ServerLevel)) return;
        if(this.hasHitBlock) this.nova(genericProjectile, this.range);
    }


}
