package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities.utility;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.utils.ModHelpers;

import static net.minecraft.world.level.block.LeverBlock.POWERED;

public class RedstoneActivator extends AbstractUtilityProjectile {
    ResourceLocation abilityId = ModHelpers.res("redstone_activator");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new RedstoneActivator();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        var blockPos = blockHitResult.getBlockPos();
        var level = this.genericProjectile.level();
        var blockState = level.getBlockState(blockPos);

        if(blockState.hasProperty(POWERED)){
            level.setBlockAndUpdate(blockPos, blockState.cycle(POWERED));
        }

        this.genericProjectile.discard();
    }
}
