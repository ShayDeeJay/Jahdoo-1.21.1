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
import org.jahdoo.trial_nexus.attachments.effects.VitalityEffect;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.registers.AttributeReg.*;
import static org.jahdoo.common.registers.ItemReg.*;
import static org.jahdoo.trial_nexus.utils.Icons.GUI_BUTTON_VITALITY_SQUARE;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.res;

public class Vitality extends AbstractElement {

    public static final String abilityId = "vitality";

    @Override
    public ResourceLocation abilityResource() {
        return res(abilityId);
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
    public AttachmentType<AbstractEntityEffect> attachmentType() {
        return AttachmentReg.VITALITY_EFFECT.get();
    }

    @Override
    public Item getCommonWand() {
        return COMMON_WAND_VITALITY.get();
    }

    @Override
    public Item getStandardWand() {
        return WAND_VITALITY.get();
    }

    @Override
    public @Nullable Item getEternalWand() {
        return ETERNAL_WAND_VITALITY.get();
    }

    @Override
    public @Nullable Item getUniqueWand() {
        return UNIQUE_WAND_VITALITY.get();
    }

    @Override
    public SoundEvent sound() {
        return SoundReg.VITALITY_ABILITY.get();
    }

    @Override
    public AbstractElementEffect aEffect() {
        return new VitalityEffect();
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
    public ResourceLocation iconTexture() {
        return Icons.VITALITY_ICON;
    }

    @Override
    public ResourceLocation backgroundTexture() {
        return GUI_BUTTON_VITALITY_SQUARE;
    }
}
