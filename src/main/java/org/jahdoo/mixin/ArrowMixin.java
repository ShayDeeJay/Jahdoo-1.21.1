package org.jahdoo.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jahdoo.registers.AttachmentRegister;
import org.jahdoo.utils.MixinMethods;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Arrow.class)
public abstract class ArrowMixin extends AbstractArrow {

    public ArrowMixin(EntityType<? extends Arrow> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if(this.getData(AttachmentRegister.BOOL.get())){
            MixinMethods.onTargetHit(result.getLocation(), this.level());
        }
        super.onHitBlock(result);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if(this.getData(AttachmentRegister.BOOL.get())){
            MixinMethods.onTargetHit(result.getLocation(), this.level());
        }
        super.onHitEntity(result);
    }



}
