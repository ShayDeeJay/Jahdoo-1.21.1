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

public class Vitality extends AbstractElement {
    ResourceLocation abilityId = Helpers.res("vitality");

    @Override
    public ResourceLocation abilityResource() {
        return abilityId;
    }

    @Override
    public int id() {
        return 4;
    }

    @Override
    public int textColourA() {
        return FastColor.ARGB32.color(226, 51, 119);
    }

    @Override
    public int textColourB() {
        return FastColor.ARGB32.color(219, 0, 85);
    }

    @Override
    public int partColourA() {
        return FastColor.ARGB32.color(129, 0, 51);
    }

    @Override
    public int partColourB() {
        return FastColor.ARGB32.color(233, 0, 93);
    }

    @Override
    public int partColourFade() {
        return -35155;
    }

    @Override
    public Item getWand() {
        return ItemsRegister.WAND_ITEM_VITALITY.get();
    }

    @Override
    public ResourceLocation projectileTexture() {
        return Helpers.res("textures/entity/vitality_projectile.png");
    }

    @Override
    public SoundEvent sound() {
        return SoundEvents.ENDER_EYE_DEATH;
    }

    @Override
    public Holder<MobEffect> effect() {
        return EffectsRegister.VITALITY_EFFECT.getDelegate();
    }

    @Override
    public Holder<Attribute> cooldownReduction() {
        return  AttributesRegister.VITALITY_COOLDOWN_REDUCTION;
    }

    @Override
    public Holder<Attribute> manaReduction() {
        return  AttributesRegister.VITALITY_MANA_COST_REDUCTION;
    }

    @Override
    public Holder<Attribute> damageAmplifier() {
        return AttributesRegister.VITALITY_MAGIC_DAMAGE_MULTIPLIER;
    }
}
