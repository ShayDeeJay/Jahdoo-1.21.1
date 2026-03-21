package org.jahdoo.trial_nexus.element;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.attachment.AttachmentType;
import org.jahdoo.trial_nexus.attachments.effects.AbstractElementEffect;
import org.jahdoo.trial_nexus.attachments.effects.AbstractEntityEffect;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import javax.annotation.Nullable;
import java.util.Objects;

import static org.jahdoo.common.particle.ParticleStore.*;

public abstract class AbstractElement {

    private String elementId = null;

    @Nullable
    public abstract Item getWand();

    @Nullable
    public abstract ResourceLocation projectileTexture();

    public abstract ResourceLocation iconTexture();

    public abstract ResourceLocation backgroundTexture();

    public abstract int id();

    public abstract int textColourA();

    public abstract int textColourB();

    public abstract int partColourA();

    public abstract int partColourB();

    public abstract int partColourFade();

    public abstract SoundEvent sound();

    public AttachmentType<AbstractEntityEffect> attachmentType() {
        return null;
    };

    public AbstractElementEffect aEffect(){
        return null;
    };

    public abstract ResourceLocation abilityResource();

    public abstract Holder<Attribute> cooldownReduction();

    public abstract Holder<Attribute> manaReduction();

    public abstract Holder<Attribute> damageAmplifier();

    public abstract ResourceKey<DamageType> damageTypeResourceKey();

    public abstract String elementDescription();

    public String name(){
        return TextHelpers.stringIdToName(abilityResource().getPath().intern());
    }

    public final String setAbilityId() {
        if (elementId == null) {
            var resourceLocation = Objects.requireNonNull(abilityResource());
            elementId = resourceLocation.getPath().intern();
        }

        return elementId;
    }

    public ElementProperties getParticleGroup(){
        return new ElementProperties(
            bakedParticleFast(this.id()),
            bakedParticleSlow(this.id()),
            genericParticleFast(this.partColourA(), this.partColourFade()),
            genericParticleSlow(this.partColourB(), this.partColourFade()),
            genericParticleSlow(this.partColourA(), this.partColourFade()),
            genericParticleFast(this.partColourB(), this.partColourFade())
        );
    }

}
