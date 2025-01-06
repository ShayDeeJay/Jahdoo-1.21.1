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

public class Inferno extends AbstractElement {
    ResourceLocation abilityId = ModHelpers.res("inferno");

    @Override
    public String getElementName() {
        return abilityId.getPath().intern().substring(0,1).toUpperCase() + abilityId.getPath().intern().substring(1);
    }

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
        return FastColor.ARGB32.color(255, 170, 70);
    }

    @Override
    public int textColourSecondary() {
        return FastColor.ARGB32.color(238, 144, 45);
    }

    @Override
    public int particleColourPrimary() {
        return FastColor.ARGB32.color(255, 68, 0);
    }

    @Override
    public int particleColourSecondary() {
        return FastColor.ARGB32.color(255, 121, 73);
    }

    @Override
    public int particleColourFaded() {
        return -34487;
    }

    @Override
    public Item getWand() {
        return ItemsRegister.WAND_ITEM_INFERNO.get();
    }

    @Override
    public ResourceLocation getAbilityProjectileTexture() {
        return ModHelpers.res("textures/entity/fire_projectile.png");
    }

    @Override
    public SoundEvent getElementSound() {
        return SoundEvents.FIRECHARGE_USE;
    }

    @Override
    public Holder<MobEffect> elementEffect() {
        return EffectsRegister.INFERNO_EFFECT.getDelegate();
    }

    @Override
    public Holder<Attribute> getTypeCooldownReduction() {
        return AttributesRegister.INFERNO_COOLDOWN_REDUCTION;
    }

    @Override
    public Holder<Attribute> getTypeManaReduction() {
        return AttributesRegister.INFERNO_MANA_COST_REDUCTION;
    }

    @Override
    public Holder<Attribute> getDamageTypeAmplifier() {
        return AttributesRegister.INFERNO_MAGIC_DAMAGE_MULTIPLIER;
    }
}
