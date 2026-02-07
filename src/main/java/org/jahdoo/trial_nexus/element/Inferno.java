package org.jahdoo.trial_nexus.element;

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
import org.jahdoo.common.registers.SoundReg;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.res;
import static org.jahdoo.common.registers.AttributeReg.*;
import static org.jahdoo.common.registers.EffectReg.INFERNO_EFFECT;
import static org.jahdoo.common.registers.ItemReg.WAND_ITEM_INFERNO;

public class Inferno extends AbstractElement {
    private ResourceLocation abilityId = res("inferno");

    @Override
    public ResourceLocation abilityResource() {
        return abilityId;
    }

    @Override
    public int id() {
        return 2;
    }

    @Override
    public int textColourA() {
        return color(255, 170, 70);
    }

    @Override
    public int textColourB() {
        return color(238, 144, 45);
    }

    @Override
    public int partColourA() {
        return color(255, 68, 0);
    }

    @Override
    public int partColourB() {
        return color(255, 121, 73);
    }

    @Override
    public int partColourFade() {
        return -34487;
    }

    @Override
    public Item getWand() {
        return WAND_ITEM_INFERNO.get();
    }

    @Override
    public SoundEvent sound() {
        return SoundReg.FIRE_ABILITY.get();
    }

    @Override
    public Holder<MobEffect> effect() {
        return INFERNO_EFFECT.getDelegate();
    }

    @Override
    public Holder<Attribute> cooldownReduction() {
        return INFERNO_COOLDOWN_REDUCTION;
    }

    @Override
    public Holder<Attribute> manaReduction() {
        return INFERNO_MANA_COST_REDUCTION;
    }

    @Override
    public Holder<Attribute> damageAmplifier() {
        return INFERNO_MAGIC_DAMAGE_MULTIPLIER;
    }

    @Override
    public ResourceKey<DamageType> damageTypeResourceKey() {
        return DamageTypeReg.INFERNO_SOURCE;
    }

    @Override
    public String elementDescription() {
        return "A force of raw destruction, delivering powerful strikes and setting enemies ablaze, causing them to suffer burn damage over time.";
    }

    @Override
    public ResourceLocation projectileTexture() {
        return res("textures/entity/fire_projectile.png");
    }

    @Override
    public @Nullable ResourceLocation iconTexture() {
        return Icons.INFERNO_ICON;
    }
}
