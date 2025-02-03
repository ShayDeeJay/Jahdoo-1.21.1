package org.jahdoo.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.registers.ElementRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static org.jahdoo.registers.EffectsRegister.*;
import static org.jahdoo.registers.ElementRegistry.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract boolean hasEffect(Holder<MobEffect> effect);
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public int getTeamColor() {
        int color = super.getTeamColor();

        if (this.hasEffect(MYSTIC_EFFECT)) {
            color = MYSTIC.get().textColourPrimary();
        } else if (this.hasEffect(INFERNO_EFFECT)) {
            color = INFERNO.get().textColourPrimary();
        } else if (this.hasEffect(FROST_EFFECT)) {
            color = FROST.get().textColourPrimary();
        } else if (this.hasEffect(LIGHTNING_EFFECT)) {
            color = LIGHTNING.get().textColourPrimary();
        } else if (this.hasEffect(VITALITY_EFFECT)) {
            color = VITALITY.get().textColourPrimary();
        }

        return color;
    }

}
