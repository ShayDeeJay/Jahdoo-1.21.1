package org.jahdoo.common.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.mod.SkillReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.attachments.player_abilities.Blink;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    //Yoinked from relics mod, only intended to use a part of this mod which is a part of a mod pack and not
    //use individually as a competitor to Relics mod.
    @ModifyVariable(
        method = "move",
        ordinal = 1,
        index = 3,
        name = "vec32",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"
        )
    )
    public Vec3 fluidCollision(Vec3 original) {
        if (!((Entity) (Object) this instanceof LivingEntity entity) || original.y > 0) return original;
        var level = entity.level();

        int[][] offsets = {
            {1, 0, 1}, {1, 0, 0}, {1, -1, 0}, {1, 0, -1},
            {0, 0, 1}, {0, 0, 0}, {0, -1, 0}, {0, 0, -1},
            {-1, 0, 1}, {-1, 0, 0}, {-1, -1, 0}, {-1, 0, -1}
        };

        var highestValue = original.y;
        FluidState highestFluid = null;

        for (int[] offset : offsets) {
            var sourcePos = entity.blockPosition();
            var pos = new BlockPos(sourcePos.getX() + offset[0], sourcePos.getY() + offset[1], sourcePos.getZ() + offset[2]);
            var fluidState = level.getFluidState(pos);

            if (fluidState.isEmpty()) continue;

            var shape = Shapes.block().move(pos.getX(), pos.getY() + fluidState.getOwnHeight(), pos.getZ());
            var shape2 = Shapes.create(entity.getBoundingBox().inflate(0.5));
            var checkShape = Shapes.joinIsNotEmpty(shape, shape2, BooleanOp.AND);

            if (checkShape) {
                var height = shape.max(Direction.Axis.Y) - entity.getY() - 1;
                if (highestValue < height) {
                    highestValue = height;
                    highestFluid = fluidState;
                }
            }
        }

        if(CasterData.hasSkill(entity, SkillReg.DRIP_WALK.get().id())){
            return highestFluid == null ? original : new Vec3(original.x, highestValue, original.z);
        }

        return original;
    }

    @Inject(
        method = "playStepSound",
        at = @At("HEAD"),
        cancellable = true
    )
    private void stopStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self instanceof Player player) {
            if(Blink.isActive(player)){
                ci.cancel();
            }
        }
    }

    @Inject(method = "isCurrentlyGlowing", at = @At("HEAD"), cancellable = true)
    private void forceGlow(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity)(Object)this;

        cir.setReturnValue(self.hasData(AttachmentReg.MYSTIC_EFFECT));
    }


}
