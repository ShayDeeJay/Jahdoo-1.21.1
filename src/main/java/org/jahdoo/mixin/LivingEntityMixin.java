package org.jahdoo.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ElementRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {


    @Shadow public abstract boolean hasEffect(Holder<MobEffect> effect);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public int getTeamColor() {
        int color = super.getTeamColor();
        if (this.hasEffect(EffectsRegister.ARCANE_EFFECT)) {
            color = ElementRegistry.MYSTIC.get().particleColourSecondary();
        }
        return color;
    }

}
