package org.jahdoo.ascension.element;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import org.jahdoo.common.client.Icons;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.sounds.SoundEvents.AMETHYST_CLUSTER_BREAK;
import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.utils.Helpers.res;
import static org.jahdoo.common.registers.AttributeReg.*;
import static org.jahdoo.common.registers.EffectReg.MYSTIC_EFFECT;
import static org.jahdoo.common.registers.ItemReg.WAND_ITEM_MYSTIC;

public class Mystic extends AbstractElement {
    ResourceLocation abilityId = res("mystic");

    @Override
    public ResourceLocation abilityResource() {
        return abilityId;
    }

    @Override
    public int id() {
        return 3;
    }

    @Override
    public int textColourA() {
        return color(205, 112, 242);
    }

    @Override
    public int textColourB() {
        return color(160, 27, 212);
    }

    @Override
    public int partColourA() {
        return color(151, 77, 178);
    }

    @Override
    public int partColourB() {
        return color(193, 97, 228);
    }

    @Override
    public int partColourFade() {
        return -12702125;
    }

    @Override
    public Item getWand() {
        return WAND_ITEM_MYSTIC.get();
    }

    @Override
    public SoundEvent sound() {
        return AMETHYST_CLUSTER_BREAK;
    }

    @Override
    public Holder<MobEffect> effect() {
        return MYSTIC_EFFECT.getDelegate();
    }

    @Override
    public Holder<Attribute> cooldownReduction() {
        return MYSTIC_COOLDOWN_REDUCTION;
    }

    @Override
    public Holder<Attribute> manaReduction() {
        return MYSTIC_MANA_COST_REDUCTION;
    }

    @Override
    public Holder<Attribute> damageAmplifier() {
        return MYSTIC_MAGIC_DAMAGE_MULTIPLIER;
    }

    @Override
    public String elementDescription() {
        return "Focused on mobility and control, this element manipulates space to shift the flow of battle. Its power adapts to the situation, making it highly versatile.";
    }

    @Override
    public ResourceLocation projectileTexture() {
        return res("textures/entity/mystic_projectile.png");
    }

    @Override
    public @Nullable ResourceLocation iconTexture() {
        return Icons.MYSTIC_ICON;
    }
}
