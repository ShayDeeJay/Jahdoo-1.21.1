package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities.utility;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.all_magic.UtilityHelpers;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.particle.ParticleHandlers;

public class BlockBreaker extends AbstractUtilityProjectile {
    ResourceLocation abilityId = ModHelpers.modResourceLocation("block_breaker_property");
    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        if(this.genericProjectile.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof AutomationBlockEntity) return;
        genericProjectile.level().playSound(null, genericProjectile.getX(), genericProjectile.getY(), genericProjectile.getZ(), genericProjectile.level().getBlockState(blockHitResult.getBlockPos()).getSoundType().getBreakSound(), SoundSource.BLOCKS, 1,1);
        UtilityHelpers.dropItemsOrBlock(genericProjectile, blockHitResult.getBlockPos(), false, false);
        super.onBlockBlockHit(blockHitResult);
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
}
