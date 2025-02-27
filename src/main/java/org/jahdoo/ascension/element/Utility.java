package org.jahdoo.ascension.element;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import org.jahdoo.common.registers.EffectsRegister;
import org.jahdoo.ascension.utils.Helpers;

public class Utility extends AbstractElement {
    ResourceLocation abilityId = Helpers.res("utility");

    @Override
    public ResourceLocation abilityResource() {
        return abilityId;
    }

    @Override
    public int id() {
        return 5;
    }

    @Override
    public int textColourA() {
        return -16602009;
    }

    @Override
    public int textColourB() {
        return -14160752;
    }

    @Override
    public int partColourA() {
        return FastColor.ARGB32.color(29, 172, 103);
    }

    @Override
    public int partColourB() {
        return FastColor.ARGB32.color(39, 236, 144);
    }

    @Override
    public int partColourFade() {
        return -8585278;
    }

    @Override
    public Item getWand() {
        return null;
    }

    @Override
    public ResourceLocation projectileTexture() {
        return null;
    }

    @Override
    public SoundEvent sound() {
        return SoundEvents.AXE_SCRAPE;
    }

    @Override
    public Holder<MobEffect> effect() {
        return EffectsRegister.MYSTIC_EFFECT.getDelegate();
    }

    @Override
    public Holder<Attribute> cooldownReduction() {
        return null;
    }

    @Override
    public Holder<Attribute> manaReduction() {
        return null;
    }

    @Override
    public Holder<Attribute> damageAmplifier() {
        return null;
    }
}
