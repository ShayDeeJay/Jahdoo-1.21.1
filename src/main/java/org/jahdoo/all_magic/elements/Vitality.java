package org.jahdoo.all_magic.elements;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.all_magic.ElementProperties;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.particle.ParticleStore.*;

public class Vitality extends AbstractElement {
    ResourceLocation abilityId = GeneralHelpers.modResourceLocation("vitality");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public int getTypeId() {
        return 5;
    }

    @Override
    public int textColourPrimary() {
        return -8323021;
    }

    @Override
    public int textColourSecondary() {
        return -1507235;
    }

    @Override
    public int particleColourPrimary() {
        return rgbToInt(129, 0, 51);
    }

    @Override
    public int particleColourSecondary() {
        return rgbToInt(233, 0, 93);
    }

    @Override
    public int particleColourFaded() {
        return -35155;
    }

    @Override
    public Item getWand() {
        return ItemsRegister.WAND_ITEM_VITALITY.get();
    }

    @Override
    public ResourceLocation getAbilityProjectileTexture() {
        return GeneralHelpers.modResourceLocation("textures/entity/vitality_projectile.png");
    }

    @Override
    public SoundEvent getElementSound() {
        return SoundEvents.ENDER_EYE_DEATH;
    }

    @Override
    public Holder<MobEffect> elementEffect() {
        return EffectsRegister.VITALITY_EFFECT.getDelegate();
    }
}
