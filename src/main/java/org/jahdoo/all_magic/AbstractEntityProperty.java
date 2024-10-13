package org.jahdoo.all_magic;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.utils.GeneralHelpers;

import java.util.Objects;

public abstract class AbstractEntityProperty {
    protected String abilityId = null;

    public final String setAbilityId() {
        if (abilityId == null) {
            var resourceLocation = Objects.requireNonNull(getAbilityResource());
            abilityId = resourceLocation.getPath().intern();
        }

        return abilityId;
    }

    public abstract ResourceLocation getAbilityResource();

    public DefaultEntityBehaviour getEntityProperty(){
        return null;
    }

    public String getProjectilePropertyName(){
        return GeneralHelpers.stringIdToName(setAbilityId());
    }

}
