package org.jahdoo.ascension.ability.abilities.light_placer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.ascension.ability.AbstractUtilityProjectile;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.common.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.ModTags;

public class LightPlacer extends AbstractUtilityProjectile {

    private final ResourceLocation abilityId = Helpers.res("light_placer_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new LightPlacer();
    }

    @Override
    public void onTickMethod() {
        super.onTickMethod();
    }

    @Override
    public void discardCondition() {
        if(generic.tickCount > 500) generic.discard();
    }

    @Override
    public String abilityId() {
        return LightPlacerAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        super.onBlockBlockHit(blockHitResult);
        if(this.generic.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ModularChaosCubeEntity) return;
        Level level = generic.level();
        BlockState replaceBlock = BlockReg.LIGHTING.get().defaultBlockState();
        BlockPos blockPos = blockHitResult.getBlockPos();
        Direction side = blockHitResult.getDirection();
        BlockPos blockPoseRelative = blockPos.relative(side);

        if(!level.isClientSide){
            if(level.getBlockState(blockPoseRelative).is(ModTags.Block.CAN_REPLACE_BLOCK)){
                level.setBlock(blockPoseRelative, replaceBlock, 3);
            }
        }
        level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), level.getBlockState(blockHitResult.getBlockPos()).getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1,1);
        generic.discard();
    }

}
