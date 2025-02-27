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
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public int getTypeId() {
        return 1;
    }

    @Override
    public int textColourPrimary() {
        return FastColor.ARGB32.color(160,209,243);
    }

    @Override
    public int textColourSecondary() {
        return FastColor.ARGB32.color(103, 200, 249);
    }

    @Override
    public int particleColourPrimary() {
        return FastColor.ARGB32.color(45, 169, 255);
    }

    @Override
    public int particleColourSecondary() {
        return FastColor.ARGB32.color(135,204,254);
    }

    @Override
    public int particleColourFaded() {
        return -3479555;
    }

    @Override
    public Item getWand() {
        return ItemsRegister.WAND_ITEM_FROST.get();
    }

    @Override
    public ResourceLocation getAbilityProjectileTexture() {
        return Helpers.res("textures/entity/cryo_projectile.png");
    }

    @Override
    public Holder<MobEffect> elementEffect() {
        return EffectsRegister.FROST_EFFECT.getDelegate();
    }


    @Override
    public Holder<Attribute> getTypeCooldownReduction() {
        return AttributesRegister.FROST_COOLDOWN_REDUCTION;
    }

    @Override
    public Holder<Attribute> getTypeManaReduction() {
        return AttributesRegister.FROST_MANA_COST_REDUCTION;
    }

    @Override
    public Holder<Attribute> getDamageTypeAmplifier() {
        return AttributesRegister.FROST_MAGIC_DAMAGE_MULTIPLIER;
    }

    @Override
    public SoundEvent getElementSound() {
        return SoundRegister.ICE_ATTACH.get();
    }
}
