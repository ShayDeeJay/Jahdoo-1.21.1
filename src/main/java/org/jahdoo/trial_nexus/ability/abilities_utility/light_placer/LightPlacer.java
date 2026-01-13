package org.jahdoo.trial_nexus.ability.abilities_utility.light_placer;

import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.common.block.chaos_cube.ChaosCubeEntity;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.trial_nexus.ability.AbstractUtilityProjectile;
import org.jahdoo.trial_nexus.ability.DefaultEntityBehaviour;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.trial_nexus.utils.ModTags;

public class LightPlacer extends AbstractUtilityProjectile {

    private final ResourceLocation abilityId = Helpers.res("light_placer_property");
    BlockPos hitPos;
    boolean hitBlock;

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new LightPlacer();
    }

    @Override
    public void discardCondition() {
        if(this.generic.tickCount > 500) this.generic.discard();
    }

    @Override
    public String abilityId() {
        return LightPlacerAbility.abilityId.getPath().intern();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        super.onBlockBlockHit(blockHitResult);
        if(getLevel() instanceof CustomLevel) return;

        if(this.generic.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ChaosCubeEntity) return;
        var level = this.generic.level();
        var replaceBlock = BlockReg.LIGHTING.get().defaultBlockState();
        var blockPos = blockHitResult.getBlockPos();
        var side = blockHitResult.getDirection();
        var blockPoseRelative = blockPos.relative(side);

        if(!level.isClientSide){
            if(level.getBlockState(blockPoseRelative).is(ModTags.Block.CAN_REPLACE_BLOCK)){
                this.hitBlock = true;
                level.setBlockAndUpdate(blockPoseRelative, replaceBlock);
                this.hitPos = blockPoseRelative;
                generic.setDeltaMovement(0,0,0);
            }
        }

        var placeSound = level.getBlockState(blockHitResult.getBlockPos()).getSoundType().getPlaceSound();
        level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), placeSound, SoundSource.BLOCKS, 1,1);
//        this.hitPos = blockPoseRelative;
        this.generic.discard();
//        generic.setDeltaMovement(0,0,0);
    }

}
