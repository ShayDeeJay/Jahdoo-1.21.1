package org.jahdoo.ability.all_abilities.abilities.raw_abilities.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.ability.AbstractUtilityProjectile;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.UtilityHelpers;
import org.jahdoo.ability.all_abilities.abilities.Utility.HammerAbility;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.entities.GenericProjectile;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.GenericParticleOptions;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.particle.ParticleHandlers;

import static org.jahdoo.ability.AbilityBuilder.OFFSET;
import static org.jahdoo.ability.AbilityBuilder.SIZE;

public class Hammer extends AbstractUtilityProjectile {
    ResourceLocation abilityId = ModHelpers.res("hammer_property");
    BlockHitResult blockHitResult;
    double breakerSize;
    int size;

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.breakerSize = this.getTagUtility(SIZE);
        var offset = (int) this.getTagUtility(OFFSET);
        this.size = (int) ((breakerSize/2) - offset);
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Hammer();
    }

    @Override
    public String abilityId() {
        return HammerAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        if(this.genericProjectile.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ModularChaosCubeEntity) return;
        var owner = (LivingEntity) genericProjectile.getOwner();
        if(owner == null && genericProjectile.blockEntityPos == null) return;

        if(genericProjectile.level() instanceof ServerLevel serverLevel) {
            this.blockHitResult = blockHitResult;
            var radius = (int) (this.breakerSize / 2);
            var pos = blockHitResult.getBlockPos();
            var pDirection = owner == null ? genericProjectile.getDirection() : owner.getDirection();
            var lookAngleY = genericProjectile.getLookAngle().y;
            var isLookingUpOrDown = lookAngleY < -0.8 || lookAngleY > 0.8;
            var axisZ = pDirection.getAxis() == Direction.Axis.Z;
            var axisX = pDirection.getAxis() == Direction.Axis.X;

            genericProjectile.level().playSound(
                null, genericProjectile.getX(), genericProjectile.getY(), genericProjectile.getZ(),
                genericProjectile.level().getBlockState(blockHitResult.getBlockPos()).getSoundType().getBreakSound(),
                SoundSource.BLOCKS, 1, 1
            );

            var isPos = this.genericProjectile.blockEntityPos != null;
            pos = pos.relative(lookAngleY < -0.8 ? pDirection.getOpposite() : pDirection, !isLookingUpOrDown || isPos ? 0 : size).above(isLookingUpOrDown || isPos ? 0 : size);

            if (UtilityHelpers.range.contains(UtilityHelpers.destroySpeed(blockHitResult.getBlockPos(), genericProjectile.level()))) {
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            BlockPos offsetPos = pos.offset(
                                x * (isLookingUpOrDown || axisZ ? 1 : 0),
                                y * (isLookingUpOrDown ? 0 : 1),
                                z * (isLookingUpOrDown || axisX ? 1 : 0)
                            );

                            UtilityHelpers.dropItemsOrBlock(genericProjectile, offsetPos, false, false);

                            var particle = new GenericParticleOptions(
                                ParticleStore.SOFT_PARTICLE_SELECTION,
                                this.getElementType().particleColourPrimary(),
                                this.getElementType().particleColourFaded(),
                                3, 1, false, 0
                            );

                            ParticleHandlers.particleBurst(serverLevel, offsetPos.getCenter(), 1,
                                particle,
                                !(isLookingUpOrDown && axisX) ? 0 : 0.15, isLookingUpOrDown ? 0 : 0.15, !(isLookingUpOrDown && axisZ) ? 0 : 0.15,
                                0.005f, 1
                            );
                        }
                    }
                }
            }

            super.onBlockBlockHit(blockHitResult);
            genericProjectile.discard();
        }

    }

}
