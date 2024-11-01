package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.all_abilities.abilities.Utility.BoneMealAbility;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import static org.jahdoo.all_magic.all_abilities.abilities.Utility.BoneMealAbility.BONE_MEAL_RANGE;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;

public class BoneMeal extends AbstractUtilityProjectile {
    ResourceLocation abilityId = ModHelpers.modResourceLocation("bone_meal_property");
    boolean hasHitBlock;
    double counter = 0.05;
    double bonemalRange;
    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.bonemalRange = this.getTag(BONE_MEAL_RANGE);
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new BoneMeal();
    }

    @Override
    public double getTag(String name) {
        var wandAbilityHolder = this.genericProjectile.wandAbilityHolder();
        return ModHelpers.getModifierValue(wandAbilityHolder, BoneMealAbility.abilityId.getPath().intern()).get(name).setValue();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        this.hasHitBlock = true;
        this.genericProjectile.setInvisible(true);
        this.genericProjectile.setDeltaMovement(0,0,0);
        super.onBlockBlockHit(blockHitResult);
    }

    public void applyBoneMeal(Level level, BlockPos pPos) {
        BlockState blockstate = level.getBlockState(pPos);
        if (!(blockstate.getBlock() instanceof BonemealableBlock bonemealableblock)) return;
        if (!(bonemealableblock.isValidBonemealTarget(level, pPos, blockstate))) return;
        if (level instanceof ServerLevel serverLevel) {
            bonemealableblock.performBonemeal(serverLevel, level.random, pPos, blockstate);
        }
    }

    void nova(Projectile projectile, double novaMaxSize){
        if(counter < novaMaxSize){
            if(counter < 2) counter *= 1.8; else counter += 0.5;
            double particle1 = counter == novaMaxSize ? 0.4 : 0.1  ;

            PositionGetters.getOuterSquareOfRadius(projectile.position(), counter + 1, counter * 10,
                positions -> {
                    double vx1 = (ModHelpers.Random.nextDouble() - 0.5) * 0.5;
                    ParticleHandlers.sendParticles(
                        projectile.level(),
                        genericParticleOptions(ParticleStore.SOFT_PARTICLE_SELECTION, this.getElementType(), 6, 2f),
                        positions.add(0, 0.3, 0), 1, vx1, vx1, vx1, particle1
                    );
                }
            );

            PositionGetters.getOuterSquareOfRadius(projectile.position(), counter, counter,
                positions -> {
                    this.applyBoneMeal(projectile.level(), BlockPos.containing(positions));
                    this.applyBoneMeal(projectile.level(), BlockPos.containing(positions).below());
                    ModHelpers.getSoundWithPosition(projectile.level(), BlockPos.containing(positions), SoundEvents.BONE_MEAL_USE);
                }
            );
        } else {
            projectile.discard();
        }
    }

    @Override
    public void onTickMethod() {
        super.onTickMethod();
        if (!(genericProjectile.level() instanceof ServerLevel)) return;
        if(this.hasHitBlock) this.nova(genericProjectile, this.bonemalRange);
    }


}
