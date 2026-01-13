package org.jahdoo.trial_nexus.ability.abilities_utility.hammer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.common.block.chaos_cube.ChaosCubeEntity;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.particle.particle_options.GenericParticleOptions;
import org.jahdoo.trial_nexus.ability.AbstractUtilityProjectile;
import org.jahdoo.trial_nexus.ability.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.utils.Helpers;

import static org.jahdoo.trial_nexus.ability.AbilityBuilder.*;
import static org.jahdoo.trial_nexus.ability.UtilityHelpers.canBreakInDim;
import static org.jahdoo.trial_nexus.ability.UtilityHelpers.dropItemsOrBlock;

public class Hammer extends AbstractUtilityProjectile {

    private static final ResourceLocation abilityId = Helpers.res("hammer_property");
    private double breakerSize;
    private double voidBlocks;
    private double fortune;
    private double silkTouch;
    private double smelter;
    private double collector;
    private double reinforced;
    private int size;

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.breakerSize = this.getTag(SIZE);
        var offset = (int) this.getTag(OFFSET);
        this.size = (int) ((breakerSize/2) - offset);
        this.voidBlocks = this.getTag(VOID_BLOCKS);
        this.fortune = this.getTag(FORTUNE);
        this.silkTouch = this.getTag(SILK_TOUCH);
        this.smelter = this.getTag(SMELTER);
        this.collector = this.getTag(AUTO_COLLECT);
        this.reinforced = this.getTag(REINFORCED);
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
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
        super.onBlockBlockHit(blockHitResult);
        var projectile = generic;
        if(projectile.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ChaosCubeEntity) return;
        var owner = (LivingEntity) projectile.getOwner();
        if(owner == null && projectile.blockEntityPos == null) return;
        if(!(projectile.level() instanceof ServerLevel serverLevel)) return;

        var radius = (int) (this.breakerSize / 2);
        var pos = blockHitResult.getBlockPos();
        var direction = owner == null ? projectile.getDirection() : owner.getDirection();
        var lookAngleY = projectile.getLookAngle().y;
        var isLookingUpOrDown = lookAngleY < -0.8 || lookAngleY > 0.8;
        var axisZ = direction.getAxis() == Direction.Axis.Z;
        var axisX = direction.getAxis() == Direction.Axis.X;

        projectile.level().playSound(
            null, projectile.getX(), projectile.getY(), projectile.getZ(),
            projectile.level().getBlockState(blockHitResult.getBlockPos()).getSoundType().getBreakSound(),
            SoundSource.BLOCKS, 1, 1
        );

        var isPos = projectile.blockEntityPos != null;
        pos = pos.relative(lookAngleY < -0.8 ? direction.getOpposite() : direction, !isLookingUpOrDown || isPos ? 0 : size).above(isLookingUpOrDown || isPos ? 0 : size);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos offsetPos = pos.offset(
                        x * (isLookingUpOrDown || axisZ ? 1 : 0),
                        y * (isLookingUpOrDown ? 0 : 1),
                        z * (isLookingUpOrDown || axisX ? 1 : 0)
                    );


                    if(canBreakInDim(serverLevel, pos)){
                        dropItemsOrBlock(
                            projectile,
                            offsetPos,
                            reinforced == 2 ? 50 : 0,
                            (int) fortune,
                            valueToBool(silkTouch),
                            valueToBool(voidBlocks),
                            valueToBool(smelter),
                            valueToBool(collector)
                        );
                    }

                    var particle = new GenericParticleOptions(
                        ParticleStore.SOFT_PARTICLE,
                        this.getElementType().partColourA(),
                        this.getElementType().partColourFade(),
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
        projectile.discard();
    }

    public static boolean valueToBool(double value){
        return value == 2;
    }

}
