package org.jahdoo.ability.elements;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FastColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.registers.AttributesRegister;
import org.jahdoo.registers.EffectsRegister;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;

public class Frost extends AbstractElement {
    ResourceLocation abilityId = ModHelpers.res("frost");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public int getTypeId() {
        return 2;
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
        return ModHelpers.res("textures/entity/cryo_projectile.png");
    }

    @Override
    public Holder<MobEffect> elementEffect() {
        return EffectsRegister.ICE_EFFECT.getDelegate();
    }

    @Override
    public Pair<String, Holder<Attribute>> getTypeCooldownReduction() {
        return Pair.of(AttributesRegister.FROST_COOLDOWN_REDUCTION_PREFIX, AttributesRegister.FROST_COOLDOWN_REDUCTION);
    }

    @Override
    public Pair<String, Holder<Attribute>> getTypeManaReduction() {
        return Pair.of(AttributesRegister.FROST_MANA_COST_REDUCTION_PREFIX, AttributesRegister.FROST_MANA_COST_REDUCTION);
    }

    @Override
    public Pair<String, Holder<Attribute>> getDamageTypeAmplifier() {
        return Pair.of(AttributesRegister.FROST_MAGIC_DAMAGE_MULTIPLIER_PREFIX, AttributesRegister.FROST_MAGIC_DAMAGE_MULTIPLIER);
    }

    @Override
    public SoundEvent getElementSound() {
        return SoundRegister.ICE_ATTACH.get();
    }
}
