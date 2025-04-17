package org.jahdoo.ascension.element;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.DamageTypeReg;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.sounds.SoundEvents.ENDER_EYE_DEATH;
import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.ascension.utils.Helpers.res;
import static org.jahdoo.common.registers.AttributeReg.*;
import static org.jahdoo.common.registers.EffectReg.VITALITY_EFFECT;
import static org.jahdoo.common.registers.ItemReg.WAND_ITEM_VITALITY;

public class Vitality extends AbstractElement {

    ResourceLocation abilityId = res("vitality");

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
        return color(226, 51, 119);
    }

    @Override
    public int textColourB() {
        return color(219, 0, 85);
    }

    @Override
    public int partColourA() {
        return color(129, 0, 51);
    }

    @Override
    public int partColourB() {
        return color(233, 0, 93);
    }

    @Override
    public int partColourFade() {
        return -35155;
    }

    @Override
    public Item getWand() {
        return WAND_ITEM_VITALITY.get();
    }

    @Override
    public SoundEvent sound() {
        return ENDER_EYE_DEATH;
    }

    @Override
    public Holder<MobEffect> effect() {
        return VITALITY_EFFECT.getDelegate();
    }

    @Override
    public Holder<Attribute> cooldownReduction() {
        return VITALITY_COOLDOWN_REDUCTION;
    }

    @Override
    public Holder<Attribute> manaReduction() {
        return VITALITY_MANA_COST_REDUCTION;
    }

    @Override
    public Holder<Attribute> damageAmplifier() {
        return VITALITY_MAGIC_DAMAGE_MULTIPLIER;
    }


    @Override
    public ResourceKey<DamageType> damageTypeResourceKey() {
        return DamageTypeReg.VITALITY_SOURCE;
    }

    @Override
    public String elementDescription() {
        return "Focused on life and renewal, this power heals wounds and summons allies to the fray. It shifts the tide of battle by supporting and strengthening those who fight alongside you.";
    }

    @Override
    public ResourceLocation projectileTexture() {
        return res("textures/entity/vitality_projectile.png");
    }

    @Override
    public @Nullable ResourceLocation iconTexture() {
        return Icons.VITALITY_ICON;
    }
}
