package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.ModTags;

public class LightPlacer extends AbstractUtilityProjectile {
    ResourceLocation abilityId = ModHelpers.modResourceLocation("light_placer_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new LightPlacer();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        if(this.genericProjectile.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof AutomationBlockEntity) return;
        Level level = genericProjectile.level();
        BlockState replaceBlock = BlocksRegister.LIGHTING.get().defaultBlockState();
        BlockPos blockPos = blockHitResult.getBlockPos();
        Direction side = blockHitResult.getDirection();
        BlockPos blockPoseRelative = blockPos.relative(side);

        if(!level.isClientSide){
            if(level.getBlockState(blockPoseRelative).is(ModTags.Block.CAN_REPLACE_BLOCK)){
                level.setBlock(blockPoseRelative, replaceBlock, 3);
            }
        }
        level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), level.getBlockState(blockHitResult.getBlockPos()).getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1,1);
        super.onBlockBlockHit(blockHitResult);
        genericProjectile.discard();
    }

    @Override
    public void onTickMethod() {
        super.onTickMethod();
    }

    @Override
    public void discardCondition() {
        if(genericProjectile.tickCount > 500) genericProjectile.discard();
    }

}
