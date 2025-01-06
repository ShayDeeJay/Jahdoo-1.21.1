package org.jahdoo.ability;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import org.jahdoo.utils.ModHelpers;

import javax.annotation.Nullable;
import java.util.Objects;

import static org.jahdoo.particle.ParticleStore.*;

public abstract class AbstractElement {
    private String elementId = null;

    public final String setAbilityId() {
        if (elementId == null) {
            var resourceLocation = Objects.requireNonNull(getAbilityResource());
            elementId = resourceLocation.getPath().intern();
        }

        return elementId;
    }

    public abstract ResourceLocation getAbilityResource();

    public String getElementName(){
        return ModHelpers.stringIdToName(elementId);
    }

    public abstract int getTypeId();

    public abstract int textColourPrimary();

    public abstract int textColourSecondary();

    public abstract int particleColourPrimary();

    public abstract int particleColourSecondary();

    public abstract int particleColourFaded();

    @Nullable
    public abstract Item getWand();

    public ElementProperties getParticleGroup(){
        return new ElementProperties(
            bakedParticleFast(this.getTypeId()),
            bakedParticleSlow(this.getTypeId()),
            genericParticleFast(this.particleColourPrimary(), this.particleColourFaded()),
            genericParticleSlow(this.particleColourSecondary(), this.particleColourFaded()),
            genericParticleSlow(this.particleColourPrimary(), this.particleColourFaded()),
            genericParticleFast(this.particleColourSecondary(), this.particleColourFaded())
        );
    }

    @Nullable
    public abstract ResourceLocation getAbilityProjectileTexture();

    public abstract SoundEvent getElementSound();

    public abstract Holder<MobEffect> elementEffect();

    public abstract Holder<Attribute> getTypeCooldownReduction();

    public abstract Holder<Attribute> getTypeManaReduction();

    public abstract Holder<Attribute> getDamageTypeAmplifier();

}
