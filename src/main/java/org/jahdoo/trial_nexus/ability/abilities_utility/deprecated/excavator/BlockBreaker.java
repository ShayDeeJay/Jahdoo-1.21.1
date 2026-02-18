package org.jahdoo.trial_nexus.ability.abilities_utility.deprecated.excavator;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.common.block.chaos_cube.ChaosCubeEntity;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.trial_nexus.ability.AbstractUtilityProjectile;
import org.jahdoo.trial_nexus.ability.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.ability.UtilityHelpers;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

public class BlockBreaker extends AbstractUtilityProjectile {

    private static final ResourceLocation abilityId = JahdooHelpers.res("block_breaker_property");

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        var level = generic.level();
        var isAcceptableBlockType = level.getBlockState(blockHitResult.getBlockPos()).is(BlockReg.NEXITE_ORE);
        if(LevelGenerator.isNexus(level) && !isAcceptableBlockType) return;

        super.onBlockBlockHit(blockHitResult);
        if(this.generic.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ChaosCubeEntity) return;
        var breakSound = generic.level().getBlockState(blockHitResult.getBlockPos()).getSoundType().getBreakSound();

        generic.level().playSound(null, generic.getX(), generic.getY(), generic.getZ(), breakSound, SoundSource.BLOCKS, 1,1);
        UtilityHelpers.dropItemsOrBlock(generic, blockHitResult.getBlockPos(), 0, 0, false, false, false, false);
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
