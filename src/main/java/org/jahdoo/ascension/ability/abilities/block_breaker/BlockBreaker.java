package org.jahdoo.ascension.ability.abilities.block_breaker;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.ascension.ability.AbstractUtilityProjectile;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.UtilityHelpers;
import org.jahdoo.common.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.ascension.utils.Helpers;

public class BlockBreaker extends AbstractUtilityProjectile {
    ResourceLocation abilityId = Helpers.res("block_breaker_property");
    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        super.onBlockBlockHit(blockHitResult);
        if(this.genericProjectile.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ModularChaosCubeEntity) return;
        genericProjectile.level().playSound(null, genericProjectile.getX(), genericProjectile.getY(), genericProjectile.getZ(), genericProjectile.level().getBlockState(blockHitResult.getBlockPos()).getSoundType().getBreakSound(), SoundSource.BLOCKS, 1,1);
        UtilityHelpers.dropItemsOrBlock(genericProjectile, blockHitResult.getBlockPos(), false, false);
        genericProjectile.discard();
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new BlockBreaker();
    }

    @Override
    public String abilityId() {
        return BlockBreakerAbility.abilityId.getPath().intern();
    }
}
