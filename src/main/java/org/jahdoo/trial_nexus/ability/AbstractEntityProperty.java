package org.jahdoo.trial_nexus.ability;

import net.minecraft.resources.ResourceLocation;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.Objects;

public abstract class AbstractEntityProperty extends AbstractAbility {
    protected String abilityId = null;

    public abstract ResourceLocation getAbilityResource();

    public DefaultEntityBehaviour getEntityProperty(){ return null; }

    public String getProjectilePropertyName(){ return TextHelpers.stringIdToName(setAbilityId()); }

    public final String setAbilityId() {
        if (abilityId == null) {
            var resourceLocation = Objects.requireNonNull(getAbilityResource());
            abilityId = resourceLocation.getPath().intern();
        }
        return abilityId;
    }

}
