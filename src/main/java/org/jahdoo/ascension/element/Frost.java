package org.jahdoo.ascension.element;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.DamageTypeReg;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.utils.Helpers.res;
import static org.jahdoo.common.registers.AttributeReg.*;
import static org.jahdoo.common.registers.EffectReg.FROST_EFFECT;
import static org.jahdoo.common.registers.ItemReg.WAND_ITEM_FROST;
import static org.jahdoo.common.registers.SoundReg.FROST_ABILITY;

public class Frost extends AbstractElement {
    ResourceLocation abilityId = res("frost");

    @Override
    public ResourceLocation abilityResource() {
        return abilityId;
    }

    @Override
    public int id() {
        return 1;
    }

    @Override
    public int textColourA() {
        return color(160,209,243);
    }

    @Override
    public int textColourB() {
        return color(103, 200, 249);
    }

    @Override
    public int partColourA() {
        return color(45, 169, 255);
    }

    @Override
    public int partColourB() {
        return color(135,204,254);
    }

    @Override
    public int partColourFade() {
        return -3479555;
    }

    @Override
    public Item getWand() {
        return WAND_ITEM_FROST.get();
    }

    @Override
    public Holder<MobEffect> effect() {
        return FROST_EFFECT.getDelegate();
    }

    @Override
    public Holder<Attribute> cooldownReduction() {
        return FROST_COOLDOWN_REDUCTION;
    }

    @Override
    public Holder<Attribute> manaReduction() {
        return FROST_MANA_COST_REDUCTION;
    }

    @Override
    public Holder<Attribute> damageAmplifier() {
        return FROST_MAGIC_DAMAGE_MULTIPLIER;
    }

    @Override
    public ResourceKey<DamageType> damageTypeResourceKey() {
        return DamageTypeReg.FROST_SOURCE;
    }

    @Override
    public String elementDescription() {
        return "A force that prioritizes control over raw power, gradually slowing foes with its icy grip. Over time, it can freeze enemies solid, stopping them in their tracks.";
    }

    @Override
    public SoundEvent sound() {
        return FROST_ABILITY.get();
    }

    @Override
    public ResourceLocation projectileTexture() {
        return res("textures/entity/cryo_projectile.png");
    }

    @Override
    public @Nullable ResourceLocation iconTexture() {
        return Icons.FROST_ICON;
    }
}
