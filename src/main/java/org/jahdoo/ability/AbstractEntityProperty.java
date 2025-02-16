package org.jahdoo.ability;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.utils.ModHelpers;

import java.util.Objects;

public abstract class AbstractEntityProperty extends AbstractAbility {
    protected String abilityId = null;

    public final String setAbilityId() {
        if (abilityId == null) {
            var resourceLocation = Objects.requireNonNull(getAbilityResource());
            abilityId = resourceLocation.getPath().intern();
        }
        return abilityId;
    }

    public abstract ResourceLocation getAbilityResource();

    public DefaultEntityBehaviour getEntityProperty(){return null;}

    public String getProjectilePropertyName(){
        return ModHelpers.stringIdToName(setAbilityId());
    }

}
