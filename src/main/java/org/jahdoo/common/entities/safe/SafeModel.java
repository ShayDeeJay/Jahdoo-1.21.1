package org.jahdoo.common.entities.safe;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.Helpers;
import software.bernie.geckolib.model.GeoModel;


public class SafeModel extends GeoModel<Safe> {

    @Override
    public ResourceLocation getModelResource(Safe animatable) {
        return Helpers.res("geo/entity/safe.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Safe animatable) {
        return Helpers.res("textures/entity/safe.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Safe animatable) {
        return Helpers.res("animations/entity/safe.animation.json");
    }

}

