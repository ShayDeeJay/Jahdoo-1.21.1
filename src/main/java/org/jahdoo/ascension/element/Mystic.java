package org.jahdoo.ascension.element;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import org.jahdoo.common.registers.AttributesRegister;
import org.jahdoo.common.registers.EffectsRegister;
import org.jahdoo.common.registers.ItemsRegister;
import org.jahdoo.ascension.utils.Helpers;

public class Mystic extends AbstractElement {
    ResourceLocation abilityId = Helpers.res("mystic");

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
        return FastColor.ARGB32.color(205, 112, 242);
    }

    @Override
    public int textColourB() {
        return FastColor.ARGB32.color(160, 27, 212);
    }

    @Override
    public int partColourA() {
        return FastColor.ARGB32.color(151, 77, 178);
    }

    @Override
    public int partColourB() {
        return FastColor.ARGB32.color(193, 97, 228);
    }

    @Override
    public int partColourFade() {
        return -12702125;
    }

    @Override
    public Item getWand() {
        return ItemsRegister.WAND_ITEM_MYSTIC.get();
    }

    @Override
    public ResourceLocation projectileTexture() {
        return Helpers.res("textures/entity/mystic_projectile.png");
    }

    @Override
    public SoundEvent sound() {
        return SoundEvents.AMETHYST_CLUSTER_BREAK;
    }

    @Override
    public Holder<MobEffect> effect() {
        return EffectsRegister.MYSTIC_EFFECT.getDelegate();
    }

    @Override
    public Holder<Attribute> cooldownReduction() {
        return AttributesRegister.MYSTIC_COOLDOWN_REDUCTION;
    }

    @Override
    public Holder<Attribute> manaReduction() {
        return AttributesRegister.MYSTIC_MANA_COST_REDUCTION;
    }

    @Override
    public Holder<Attribute> damageAmplifier() {
        return AttributesRegister.MYSTIC_MAGIC_DAMAGE_MULTIPLIER;
    }
}
