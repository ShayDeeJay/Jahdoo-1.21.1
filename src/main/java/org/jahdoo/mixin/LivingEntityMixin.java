package org.jahdoo.mixin;

import net.minecraft.core.Holder;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static org.jahdoo.registers.EffectsRegister.MYSTIC_EFFECT;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract boolean hasEffect(Holder<MobEffect> effect);
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public int getTeamColor() {
        int color = super.getTeamColor();
        if (this.hasEffect(MYSTIC_EFFECT)) color = FastColor.ARGB32.color(255, 202,125,255);
        return color;
    }
}
