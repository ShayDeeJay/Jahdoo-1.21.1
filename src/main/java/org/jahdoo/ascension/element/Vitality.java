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
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public int getTypeId() {
        return 4;
    }

    @Override
    public int textColourPrimary() {
        return FastColor.ARGB32.color(226, 51, 119);
    }

    @Override
    public int textColourSecondary() {
        return FastColor.ARGB32.color(219, 0, 85);
    }

    @Override
    public int particleColourPrimary() {
        return FastColor.ARGB32.color(129, 0, 51);
    }

    @Override
    public int particleColourSecondary() {
        return FastColor.ARGB32.color(233, 0, 93);
    }

    @Override
    public int particleColourFaded() {
        return -35155;
    }

    @Override
    public Item getWand() {
        return ItemsRegister.WAND_ITEM_VITALITY.get();
    }

    @Override
    public ResourceLocation getAbilityProjectileTexture() {
        return Helpers.res("textures/entity/vitality_projectile.png");
    }

    @Override
    public SoundEvent getElementSound() {
        return SoundEvents.ENDER_EYE_DEATH;
    }

    @Override
    public Holder<MobEffect> elementEffect() {
        return EffectsRegister.VITALITY_EFFECT.getDelegate();
    }

    @Override
    public Holder<Attribute> getTypeCooldownReduction() {
        return  AttributesRegister.VITALITY_COOLDOWN_REDUCTION;
    }

    @Override
    public Holder<Attribute> getTypeManaReduction() {
        return  AttributesRegister.VITALITY_MANA_COST_REDUCTION;
    }

    @Override
    public Holder<Attribute> getDamageTypeAmplifier() {
        return AttributesRegister.VITALITY_MAGIC_DAMAGE_MULTIPLIER;
    }
}
