package org.jahdoo.trial_nexus.element;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.common.registers.DamageTypeReg;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.sounds.SoundEvents.AXE_SCRAPE;
import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.registers.EffectReg.MYSTIC_EFFECT;

public class Utility extends AbstractElement {
    ResourceLocation abilityId = JahdooHelpers.res("utility");

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
        return color(29, 172, 103);
    }

    @Override
    public int partColourB() {
        return color(39, 236, 144);
    }

    @Override
    public int partColourFade() {
        return -8585278;
    }

    @Override
    public SoundEvent sound() {
        return AXE_SCRAPE;
    }

    @Override
    public Holder<MobEffect> effect() {
        return MYSTIC_EFFECT.getDelegate();
    }

    @Override
    public Item getWand() {
        return null;
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


    @Override
    public ResourceKey<DamageType> damageTypeResourceKey() {
        return DamageTypeReg.JAHDOO_SOURCE;
    }

    @Override
    public String elementDescription() {
        return "A power that enhances practical actions, from shaping the land to aiding in construction and resource gathering. It’s not meant for combat, but for manipulating the world to serve your needs.";
    }

    @Override
    public ResourceLocation projectileTexture() {
        return null;
    }

    @Override
    public @Nullable ResourceLocation iconTexture() {
        return Icons.UTILITY_ICON;
    }
}
