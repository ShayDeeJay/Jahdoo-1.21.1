package org.jahdoo.common.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.trial_nexus.utils.MixinMethods;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.jahdoo.trial_nexus.utils.MixinMethods.getCorrectDrop;

@Mixin(ComposterBlock.class)
public abstract class ComposterBlockMixin {

    @Shadow protected abstract void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random);

    @Shadow @Final public static IntegerProperty LEVEL;

    @Inject(
        method = "handleFill",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void handleFill(
        Level level, BlockPos pos, boolean success, CallbackInfo ci
    ) {
        if(MixinMethods.hasBlockNeeded(level, pos)){
            level.playLocalSound(pos, success ? SoundEvents.COMPOSTER_FILL_SUCCESS : SoundEvents.COMPOSTER_FILL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            MixinMethods.onTickComposter(level, pos);
            ci.cancel();
        }
    }

    @Inject(
        method = "getContainer",
        at = @At("HEAD"),
        cancellable = true
    )
    private void addSecondOutput(
        BlockState state, LevelAccessor level, BlockPos pos, CallbackInfoReturnable<WorldlyContainer> cir
    ) {
        int i = state.getValue(LEVEL);
        var sLevel = level.getServer();
        if(sLevel != null){
            var stack = getCorrectDrop(pos, sLevel.overworld());
            cir.setReturnValue(
                i == 8 ? new MixinMethods.OutputContainer(state, level, pos, stack) : (i < 7 ? new MixinMethods.InputContainer(state, level, pos) : new MixinMethods.EmptyContainer())
            );
        }
    }


    @Inject(
        method = "extractProduce",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void produceNexite(
        Entity entity, BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<BlockState> cir
    ) {
        if (!level.isClientSide) {
            Vec3 vec3 = Vec3.atLowerCornerWithOffset(pos, 0.5F, 1.01, 0.5F).offsetRandom(level.random, 0.7F);
            ItemEntity itementity = new ItemEntity(level, vec3.x(), vec3.y(), vec3.z(), getCorrectDrop(pos, level));
            itementity.setDefaultPickUpDelay();
            level.addFreshEntity(itementity);
        }

        BlockState blockstate = state.setValue(LEVEL, 0);
        level.setBlock(pos, blockstate, 3);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(null, blockstate));
        level.playSound(null, pos, SoundEvents.COMPOSTER_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);

       cir.setReturnValue(blockstate);
    }

}
