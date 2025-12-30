package org.jahdoo.common.entities.explosive_barrel;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.Helpers;
import software.bernie.geckolib.model.GeoModel;


public class ExplosiveBarrelModel extends GeoModel<ExplosiveBarrel> {

    @Override
    public ResourceLocation getModelResource(ExplosiveBarrel animatable) {
        return Helpers.res("geo/entity/damage_barrel.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ExplosiveBarrel animatable) {
        var type = switch (animatable.getBarrelType()){
            case 1 -> "frost_barrel";
            case 2 -> "fire_barrel";
            case 3 -> "mystic_barrel";
            default -> "vitality_barrel";
        };

        return Helpers.res("textures/entity/damage_barrel/" + type + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(ExplosiveBarrel animatable) {
        return Helpers.res("animations/entity/damage_barrel.animation.json");
    }

}

