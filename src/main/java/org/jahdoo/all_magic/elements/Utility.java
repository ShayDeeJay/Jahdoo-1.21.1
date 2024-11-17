package org.jahdoo.all_magic.elements;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.particle.ParticleStore.rgbToInt;

public class Utility extends AbstractElement {
    ResourceLocation abilityId = ModHelpers.res("utility");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public int getTypeId() {
        return 6;
    }

    @Override
    public int textColourPrimary() {
        return -16602009;
    }

    @Override
    public int textColourSecondary() {
        return -14160752;
    }

    @Override
    public int particleColourPrimary() {
        return rgbToInt(29, 172, 103);
    }

    @Override
    public int particleColourSecondary() {
        return rgbToInt(39, 236, 144);
    }

    @Override
    public int particleColourFaded() {
        return -8585278;
    }

    @Override
    public Item getWand() {
        return null;
    }

    @Override
    public ResourceLocation getAbilityProjectileTexture() {
        return null;
    }

    @Override
    public SoundEvent getElementSound() {
        return SoundEvents.AXE_SCRAPE;
    }

    @Override
    public Holder<MobEffect> elementEffect() {
        return EffectsRegister.ARCANE_EFFECT.getDelegate();
    }
}
