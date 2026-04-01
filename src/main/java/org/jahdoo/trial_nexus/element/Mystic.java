package org.jahdoo.trial_nexus.element;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.DamageTypeReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.attachments.effects.AbstractElementEffect;
import org.jahdoo.trial_nexus.attachments.effects.AbstractEntityEffect;
import org.jahdoo.trial_nexus.attachments.effects.MysticEffect;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.registers.AttributeReg.*;
import static org.jahdoo.common.registers.ItemReg.*;
import static org.jahdoo.trial_nexus.utils.Icons.GUI_BUTTON_MYSTIC_SQUARE;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.res;

public class Mystic extends AbstractElement {
    public static final String abilityId = "mystic";

    @Override
    public ResourceLocation abilityResource() {
        return res(abilityId);
    }

    @Override
    public int id() {
        return 3;
    }

    @Override
    public int textColourA() {
        return color(205, 112, 242);
    }

    @Override
    public int textColourB() {
        return color(160, 27, 212);
    }

    @Override
    public int partColourA() {
        return color(151, 77, 178);
    }

    @Override
    public int partColourB() {
        return color(193, 97, 228);
    }

    @Override
    public int partColourFade() {
        return -12702125;
    }

    @Override
    public Item getCommonWand() {
        return COMMON_WAND_MYSTIC.get();
    }

    @Override
    public Item getStandardWand() {
        return WAND_MYSTIC.get();
    }

    @Override
    public @Nullable Item getEternalWand() {
        return ETERNAL_WAND_MYSTIC.get();
    }

    @Override
    public @Nullable Item getUniqueWand() {
        return UNIQUE_WAND_MYSTIC.get();
    }

    @Override
    public SoundEvent sound() {
        return SoundReg.MYSTIC_ABILITY.get();
    }

    @Override
    public AttachmentType<AbstractEntityEffect> attachmentType() {
        return AttachmentReg.MYSTIC_EFFECT.get();
    }

    @Override
    public AbstractElementEffect aEffect() {
        return new MysticEffect();
    }

    @Override
    public Holder<Attribute> cooldownReduction() {
        return MYSTIC_COOLDOWN_REDUCTION;
    }

    @Override
    public Holder<Attribute> manaReduction() {
        return MYSTIC_MANA_COST_REDUCTION;
    }

    @Override
    public Holder<Attribute> damageAmplifier() {
        return MYSTIC_MAGIC_DAMAGE_MULTIPLIER;
    }

    @Override
    public ResourceKey<DamageType> damageTypeResourceKey() {
        return DamageTypeReg.MYSTIC_SOURCE;
    }

    @Override
    public String elementDescription() {
        return "Focused on mobility and control, this element manipulates space to shift the flow of battle. Its power adapts to the situation, making it highly versatile.";
    }

    @Override
    public ResourceLocation projectileTexture() {
        return res("textures/entity/mystic_projectile.png");
    }

    @Override
    public ResourceLocation iconTexture() {
        return Icons.MYSTIC_ICON;
    }

    @Override
    public ResourceLocation backgroundTexture() {
        return GUI_BUTTON_MYSTIC_SQUARE;
    }
}
