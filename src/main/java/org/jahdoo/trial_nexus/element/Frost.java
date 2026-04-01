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
import org.jahdoo.trial_nexus.attachments.effects.AbstractElementEffect;
import org.jahdoo.trial_nexus.attachments.effects.AbstractEntityEffect;
import org.jahdoo.trial_nexus.attachments.effects.FrostEffect;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.registers.AttributeReg.*;
import static org.jahdoo.common.registers.ItemReg.*;
import static org.jahdoo.common.registers.SoundReg.FROST_ABILITY;
import static org.jahdoo.trial_nexus.utils.Icons.GUI_BUTTON_FROST_SQUARE;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.res;

public class Frost extends AbstractElement {
    public static final String abilityId = "frost";

    @Override
    public ResourceLocation abilityResource() {
        return res(abilityId);
    }

    @Override
    public int id() {
        return 1;
    }

    @Override
    public int textColourA() {
        return color(160,209,243);
    }

    @Override
    public int textColourB() {
        return color(103, 200, 249);
    }

    @Override
    public int partColourA() {
        return color(45, 169, 255);
    }

    @Override
    public int partColourB() {
        return color(135,204,254);
    }

    @Override
    public int partColourFade() {
        return -3479555;
    }

    @Override
    public Item getCommonWand() {
        return COMMON_WAND_FROST.get();
    }

    @Override
    public Item getStandardWand() {
        return WAND_FROST.get();
    }

    @Override
    public @Nullable Item getEternalWand() {
        return ETERNAL_WAND_FROST.get();
    }

    @Override
    public @Nullable Item getUniqueWand() {
        return UNIQUE_WAND_FROST.get();
    }

    @Override
    public AttachmentType<AbstractEntityEffect> attachmentType() {
        return AttachmentReg.FROST_EFFECT.get();
    }

    @Override
    public AbstractElementEffect aEffect() {
        return new FrostEffect();
    }

    @Override
    public Holder<Attribute> cooldownReduction() {
        return FROST_COOLDOWN_REDUCTION;
    }

    @Override
    public Holder<Attribute> manaReduction() {
        return FROST_MANA_COST_REDUCTION;
    }

    @Override
    public Holder<Attribute> damageAmplifier() {
        return FROST_MAGIC_DAMAGE_MULTIPLIER;
    }

    @Override
    public ResourceKey<DamageType> damageTypeResourceKey() {
        return DamageTypeReg.FROST_SOURCE;
    }

    @Override
    public String elementDescription() {
        return "A force that prioritizes control over raw power, gradually slowing foes with its icy grip. Over time, it can freeze enemies solid, stopping them in their tracks.";
    }

    @Override
    public SoundEvent sound() {
        return FROST_ABILITY.get();
    }

    @Override
    public ResourceLocation projectileTexture() {
        return res("textures/entity/cryo_projectile.png");
    }

    @Override
    public ResourceLocation iconTexture() {
        return Icons.FROST_ICON;
    }

    @Override
    public ResourceLocation backgroundTexture() {
        return GUI_BUTTON_FROST_SQUARE;
    }
}
