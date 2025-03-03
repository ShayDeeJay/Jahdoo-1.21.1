package org.jahdoo.ascension.element;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;

import javax.annotation.Nullable;
import java.util.Objects;

import static org.jahdoo.ascension.utils.Helpers.capitaliseFirst;
import static org.jahdoo.common.particle.ParticleStore.*;

public abstract class AbstractElement {

    private String elementId = null;

    @Nullable
    public abstract Item getWand();

    public abstract int id();

    public abstract int textColourA();

    public abstract int textColourB();

    public abstract int partColourA();

    public abstract int partColourB();

    public abstract int partColourFade();

    public abstract SoundEvent sound();

    public abstract Holder<MobEffect> effect();

    @Nullable
    public abstract ResourceLocation projectileTexture();

    @Nullable
    public abstract ResourceLocation iconTexture();

    public abstract ResourceLocation abilityResource();

    public abstract Holder<Attribute> cooldownReduction();

    public abstract Holder<Attribute> manaReduction();

    public abstract Holder<Attribute> damageAmplifier();

    public String name(){
        return capitaliseFirst(abilityResource().getPath().intern());
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
