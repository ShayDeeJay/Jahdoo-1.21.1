package org.jahdoo.ascension.ability.abilities_utility.block_breaker;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.ascension.ability.AbstractUtilityProjectile;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.ascension.ability.UtilityHelpers;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.block.chaos_cube.ChaosCubeEntity;

public class BlockBreaker extends AbstractUtilityProjectile {

    private static final ResourceLocation abilityId = Helpers.res("block_breaker_property");

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        super.onBlockBlockHit(blockHitResult);
        if(this.generic.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ChaosCubeEntity) return;
        var breakSound = generic.level().getBlockState(blockHitResult.getBlockPos()).getSoundType().getBreakSound();

        generic.level().playSound(null, generic.getX(), generic.getY(), generic.getZ(), breakSound, SoundSource.BLOCKS, 1,1);
        UtilityHelpers.dropItemsOrBlock(generic, blockHitResult.getBlockPos(), false, false);
        generic.discard();
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
