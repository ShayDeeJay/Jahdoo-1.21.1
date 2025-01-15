package org.jahdoo.ability.abilities.ability.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.ability.AbstractUtilityProjectile;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.abilities.ability_data.Utility.LightPlacerAbility;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.utils.ModHelpers;
import org.jahdoo.utils.ModTags;

public class LightPlacer extends AbstractUtilityProjectile {
    ResourceLocation abilityId = ModHelpers.res("light_placer_property");

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
        super.onBlockBlockHit(blockHitResult);
        if(this.genericProjectile.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ModularChaosCubeEntity) return;
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


    @Override
    public String abilityId() {
        return LightPlacerAbility.abilityId.getPath().intern();
    }
}
