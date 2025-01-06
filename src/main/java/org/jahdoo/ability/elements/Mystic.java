package org.jahdoo.ability.elements;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;

public class Mystic extends AbstractElement {
    ResourceLocation abilityId = ModHelpers.res("mystic");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public int getTypeId() {
        return 4;
    }

    @Override
    public int textColourPrimary() {
        return FastColor.ARGB32.color(205, 112, 242);
    }

    @Override
    public int textColourSecondary() {
        return FastColor.ARGB32.color(160, 27, 212);
    }

    @Override
    public int particleColourPrimary() {
        return FastColor.ARGB32.color(151, 77, 178);
    }

    @Override
    public int particleColourSecondary() {
        return FastColor.ARGB32.color(193, 97, 228);
    }

    @Override
    public int particleColourFaded() {
        return -12702125;
    }

    @Override
    public Item getWand() {
        return ItemsRegister.WAND_ITEM_MYSTIC.get();
    }

    @Override
    public ResourceLocation getAbilityProjectileTexture() {
        return ModHelpers.res("textures/entity/mystic_projectile.png");
    }

    @Override
    public SoundEvent getElementSound() {
        return SoundEvents.AMETHYST_CLUSTER_BREAK;
    }

    @Override
    public Holder<MobEffect> elementEffect() {
        return EffectsRegister.MYSTIC_EFFECT.getDelegate();
    }

    @Override
    public Holder<Attribute> getTypeCooldownReduction() {
        return AttributesRegister.MYSTIC_COOLDOWN_REDUCTION;
    }

    @Override
    public Holder<Attribute> getTypeManaReduction() {
        return AttributesRegister.MYSTIC_MANA_COST_REDUCTION;
    }

    @Override
    public Holder<Attribute> getDamageTypeAmplifier() {
        return AttributesRegister.MYSTIC_MAGIC_DAMAGE_MULTIPLIER;
    }
}
