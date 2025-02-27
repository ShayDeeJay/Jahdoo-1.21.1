package org.jahdoo.ascension.element;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import org.jahdoo.common.registers.AttributesRegister;
import org.jahdoo.common.registers.EffectsRegister;
import org.jahdoo.common.registers.ItemsRegister;
import org.jahdoo.common.registers.SoundRegister;
import org.jahdoo.ascension.utils.Helpers;

public class Frost extends AbstractElement {
    ResourceLocation abilityId = Helpers.res("frost");

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
        return FastColor.ARGB32.color(160,209,243);
    }

    @Override
    public int textColourB() {
        return FastColor.ARGB32.color(103, 200, 249);
    }

    @Override
    public int partColourA() {
        return FastColor.ARGB32.color(45, 169, 255);
    }

    @Override
    public int partColourB() {
        return FastColor.ARGB32.color(135,204,254);
    }

    @Override
    public int partColourFade() {
        return -3479555;
    }

    @Override
    public Item getWand() {
        return ItemsRegister.WAND_ITEM_FROST.get();
    }

    @Override
    public ResourceLocation projectileTexture() {
        return Helpers.res("textures/entity/cryo_projectile.png");
    }

    @Override
    public Holder<MobEffect> effect() {
        return EffectsRegister.FROST_EFFECT.getDelegate();
    }


    @Override
    public Holder<Attribute> cooldownReduction() {
        return AttributesRegister.FROST_COOLDOWN_REDUCTION;
    }

    @Override
    public Holder<Attribute> manaReduction() {
        return AttributesRegister.FROST_MANA_COST_REDUCTION;
    }

    @Override
    public Holder<Attribute> damageAmplifier() {
        return AttributesRegister.FROST_MAGIC_DAMAGE_MULTIPLIER;
    }

    @Override
    public SoundEvent sound() {
        return SoundRegister.ICE_ATTACH.get();
    }
}
