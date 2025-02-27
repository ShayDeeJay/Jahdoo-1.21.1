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

public class Inferno extends AbstractElement {
    ResourceLocation abilityId = Helpers.res("inferno");

    @Override
    public String name() {
        return abilityId.getPath().intern().substring(0,1).toUpperCase() + abilityId.getPath().intern().substring(1);
    }

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
        return FastColor.ARGB32.color(255, 170, 70);
    }

    @Override
    public int textColourB() {
        return FastColor.ARGB32.color(238, 144, 45);
    }

    @Override
    public int partColourA() {
        return FastColor.ARGB32.color(255, 68, 0);
    }

    @Override
    public int partColourB() {
        return FastColor.ARGB32.color(255, 121, 73);
    }

    @Override
    public int partColourFade() {
        return -34487;
    }

    @Override
    public Item getWand() {
        return ItemsRegister.WAND_ITEM_INFERNO.get();
    }

    @Override
    public ResourceLocation projectileTexture() {
        return Helpers.res("textures/entity/fire_projectile.png");
    }

    @Override
    public SoundEvent sound() {
        return SoundEvents.FIRECHARGE_USE;
    }

    @Override
    public Holder<MobEffect> effect() {
        return EffectsRegister.INFERNO_EFFECT.getDelegate();
    }

    @Override
    public Holder<Attribute> cooldownReduction() {
        return AttributesRegister.INFERNO_COOLDOWN_REDUCTION;
    }

    @Override
    public Holder<Attribute> manaReduction() {
        return AttributesRegister.INFERNO_MANA_COST_REDUCTION;
    }

    @Override
    public Holder<Attribute> damageAmplifier() {
        return AttributesRegister.INFERNO_MAGIC_DAMAGE_MULTIPLIER;
    }
}
