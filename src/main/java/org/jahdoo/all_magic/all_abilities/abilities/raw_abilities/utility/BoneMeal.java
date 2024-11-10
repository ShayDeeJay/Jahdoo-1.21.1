package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities.utility;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.UtilityHelpers;
import org.jahdoo.all_magic.all_abilities.abilities.Utility.BoneMealAbility;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.PositionGetters;

import java.util.List;
import java.util.UUID;

import static org.jahdoo.all_magic.all_abilities.abilities.Utility.BoneMealAbility.BONE_MEAL_RANGE;
import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
import static org.jahdoo.particle.ParticleStore.SOFT_PARTICLE_SELECTION;
import static org.jahdoo.utils.ModHelpers.Random;

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
        if(this.genericProjectile.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof AutomationBlockEntity) return;
        this.hasHitBlock = true;
        this.genericProjectile.setInvisible(true);
        this.genericProjectile.setDeltaMovement(0,0,0);
        PositionGetters.getOuterSquareOfRadius(this.genericProjectile.position(), counter + 1, this.bonemalRange * 40,
            positions -> this.setParticleNova(positions, this.genericProjectile.level())
        );
        super.onBlockBlockHit(blockHitResult);
    }

    public void applyBoneMeal(Level level, BlockPos pPos) {
        BlockState blockstate = level.getBlockState(pPos);

        if (level instanceof ServerLevel ) {
            if(blockstate.getBlock() instanceof CropBlock cropBlock){
                if(cropBlock.isMaxAge(blockstate)) {
                    UtilityHelpers.dropItemsOrBlock(genericProjectile, pPos, false, false);
                    level.setBlockAndUpdate(pPos, cropBlock.getStateForAge(0));
                    int col1 = this.getElementType().particleColourPrimary();
                    int col2 = this.getElementType().particleColourFaded();
                    var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, 8, 1f, col1, col2, false);
                    ParticleHandlers.particleBurst(level, pPos.getCenter().add(0,0.4,0), 3, genericParticle);
                }
            }
            if (!(blockstate.getBlock() instanceof BonemealableBlock bonemealableblock)) return;
            if (!(bonemealableblock.isValidBonemealTarget(level, pPos, blockstate))) return;
            if(Random.nextInt(0, 5) == 0){
                BoneMealItem.applyBonemeal(ItemStack.EMPTY, level, pPos, null);
                ModHelpers.getSoundWithPosition(level, pPos, SoundEvents.BONE_MEAL_USE);
            }
        }
    }

    void nova(Projectile projectile, double novaMaxSize){
        if(counter < novaMaxSize){
            if(counter < 2) counter *= 1.8; else counter += 0.5;
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
        var lifetime = (int) this.bonemalRange * 2;
        var size = 3;

        var genericParticle = genericParticleOptions(SOFT_PARTICLE_SELECTION, lifetime, (float) (size - 0.2), col1, col2, false);
        ParticleHandlers.sendParticles(
            level, genericParticle, worldPosition, 0, directions.x, directions.y+0.05, directions.z, Random.nextDouble(this.bonemalRange/12, this.bonemalRange/9)
        );
    }

    @Override
    public void onTickMethod() {
        super.onTickMethod();
        if (!(genericProjectile.level() instanceof ServerLevel)) return;
        if(this.hasHitBlock) this.nova(genericProjectile, this.bonemalRange);
    }


}
