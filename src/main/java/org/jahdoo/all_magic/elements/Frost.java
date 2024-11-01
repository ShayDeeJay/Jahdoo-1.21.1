package org.jahdoo.all_magic.elements;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.ElementProperties;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.particle.ParticleStore.*;

public class Frost extends AbstractElement {
    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("frost");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public int getTypeId() {
        return 2;
    }

    @Override
    public int textColourPrimary() {
        return -13784577;
    }

    @Override
    public int textColourSecondary() {
        return -7877378;
    }

    @Override
    public int particleColourPrimary() {
        return rgbToInt(45, 169, 255);
    }

    @Override
    public int particleColourSecondary() {
        return rgbToInt(135,204,254);
    }

    @Override
    public int particleColourFaded() {
        return -3479555;
    }

    @Override
    public Item getWand() {
        return ItemsRegister.WAND_ITEM_FROST.get();
    }

    @Override
    public ResourceLocation getAbilityProjectileTexture() {
        return GeneralHelpers.modResourceLocation("textures/entity/cryo_projectile.png");
    }

    @Override
    public Holder<MobEffect> elementEffect() {
        return EffectsRegister.ICE_EFFECT.getDelegate();
    }

    @Override
    public SoundEvent getElementSound() {
        return SoundRegister.ICE_ATTACH.get();
    }
}
