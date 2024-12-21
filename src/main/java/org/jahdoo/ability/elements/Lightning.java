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

public class Lightning extends AbstractElement {
    ResourceLocation abilityId = ModHelpers.res("lightning");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public int getTypeId() {
        return 3;
    }

    @Override
    public int textColourPrimary() {
        return FastColor.ARGB32.color(183, 218, 218);
    }

    @Override
    public int textColourSecondary() {
        return FastColor.ARGB32.color(165, 210, 210);
    }

    @Override
    public int particleColourPrimary() {
        return FastColor.ARGB32.color(110, 176, 186);
    }

    @Override
    public int particleColourSecondary() {
        return FastColor.ARGB32.color(220, 233, 235);
    }

    @Override
    public int particleColourFaded() {
        return FastColor.ARGB32.color(226, 252, 255);
    }

    @Override
    public Item getWand() {
        return ItemsRegister.WAND_ITEM_LIGHTNING.get();
    }

    @Override
    public ResourceLocation getAbilityProjectileTexture() {
        return ModHelpers.res("textures/entity/lightning_projectile2.png");
    }

    @Override
    public SoundEvent getElementSound() {
        return SoundRegister.BOLT.get();
    }

    @Override
    public Holder<MobEffect> elementEffect() {
        return EffectsRegister.LIGHTNING_EFFECT.getDelegate();
    }

    @Override
    public Pair<String, Holder<Attribute>> getTypeCooldownReduction() {
        return Pair.of(AttributesRegister.LIGHTNING_COOLDOWN_REDUCTION_PREFIX, AttributesRegister.LIGHTNING_COOLDOWN_REDUCTION);
    }

    @Override
    public Pair<String, Holder<Attribute>> getTypeManaReduction() {
        return Pair.of(AttributesRegister.LIGHTNING_MANA_COST_REDUCTION_PREFIX, AttributesRegister.LIGHTNING_MANA_COST_REDUCTION);
    }

    @Override
    public Pair<String, Holder<Attribute>> getDamageTypeAmplifier() {
        return Pair.of(AttributesRegister.LIGHTNING_MAGIC_DAMAGE_MULTIPLIER_PREFIX, AttributesRegister.LIGHTNING_MAGIC_DAMAGE_MULTIPLIER);
    }
}
