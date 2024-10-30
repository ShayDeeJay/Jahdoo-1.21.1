package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities.utility;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.UtilityHelpers;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.GeneralHelpers;
import org.jahdoo.particle.ParticleHandlers;

public class BlockBreaker extends AbstractUtilityProjectile {
    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("block_breaker_property");
    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        if (genericProjectile.level() instanceof  ServerLevel serverLevel) {
            genericProjectile.level().playSound(null, genericProjectile.getX(), genericProjectile.getY(), genericProjectile.getZ(), genericProjectile.level().getBlockState(blockHitResult.getBlockPos()).getSoundType().getBreakSound(), SoundSource.BLOCKS, 1,1);
            UtilityHelpers.dropItemsOrBlock(genericProjectile, blockHitResult.getBlockPos(), false, false);
            ParticleHandlers.spawnPoof(serverLevel, blockHitResult.getBlockPos().getCenter(), 1, ElementRegistry.UTILITY.get().getParticleGroup().genericSlow(),0,0,0,0.1f);
            genericProjectile.discard();
        }
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new BlockBreaker();
    }
}
