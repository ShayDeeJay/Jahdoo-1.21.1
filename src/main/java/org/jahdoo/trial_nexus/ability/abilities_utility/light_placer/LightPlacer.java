package org.jahdoo.trial_nexus.ability.abilities_utility.light_placer;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
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
    int timer;

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
        if(hitBlock) timer++;
        if(timer == 20) {
            getLevel().setBlockAndUpdate(hitPos, Blocks.AIR.defaultBlockState());
            this.generic.discard();
        };
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
        if(this.generic.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ChaosCubeEntity) return;
        var level = generic.level();
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
////        generic.discard();
//        generic.setDeltaMovement(0,0,0);
    }

}
