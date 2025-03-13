package org.jahdoo.ascension.ability.abilities.farmers_touch;

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
import org.jahdoo.ascension.ability.AbstractUtilityProjectile;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.UtilityHelpers;
import org.jahdoo.common.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.PositionFinders;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.ability.abilities.farmers_touch.FarmersTouchAbility.*;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;
import static org.jahdoo.ascension.utils.Helpers.Random;

public class FarmersTouch extends AbstractUtilityProjectile {

    private static final ResourceLocation abilityId = Helpers.res("farmers_touch_property");
    private final List<BlockPos> effectedPos = new ArrayList<>();
    private double counter = 0.05;
    private double harvestChance;
    private double growthChance;
    private double range;
    private boolean hasHitBlock;

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
    public void onTickMethod() {
        super.onTickMethod();
        if (!(generic.level() instanceof ServerLevel)) return;
        if(this.hasHitBlock) this.nova(generic, this.range);
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        super.onBlockBlockHit(blockHitResult);
        if(this.generic.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ModularChaosCubeEntity) return;
        this.hasHitBlock = true;
        this.generic.setInvisible(true);
        this.generic.setDeltaMovement(0,0,0);
        PositionFinders.getOuterSquareOfRadius(this.generic.position(), counter + 0.5, this.range * 20,
            positions -> this.setParticleNova(positions, this.generic.level())
        );
    }

    private void setParticleNova(Vec3 worldPosition, Level level){
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
                    this.applyBoneMeal(projectile.level(), BlockPos.containing(positions));
                    this.applyBoneMeal(projectile.level(), BlockPos.containing(positions).below());
                }
            );
        } else {
            projectile.discard();
        }
    }

    public void applyBoneMeal(Level level, BlockPos pos) {
        var blockstate = level.getBlockState(pos);
        if (level instanceof ServerLevel && !this.effectedPos.contains(pos)) {
            if(Random.nextInt(0, (int) harvestChance) == 0) {
                if(blockstate.getBlock() instanceof CropBlock cropBlock && cropBlock.isMaxAge(blockstate)){
                    UtilityHelpers.harvestBreaker(generic, pos, false);
                    level.setBlockAndUpdate(pos, cropBlock.getStateForAge(0));
                    utilityParticleBurst(level, pos.getCenter().add(0, 0.4, 0), 8, 1, 3, 0.1f);
                    Helpers.getSoundWithPosition(generic.level(), pos, blockstate.getSoundType(level, pos, null).getBreakSound());
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

}
