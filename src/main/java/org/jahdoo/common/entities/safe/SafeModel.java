package org.jahdoo.common.entities.safe;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.model.GeoModel;


public class SafeModel extends GeoModel<Safe> {

    @Override
    public ResourceLocation getModelResource(Safe animatable) {
        return JahdooHelpers.res("geo/entity/safe.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Safe animatable) {
        return JahdooHelpers.res("textures/entity/safe.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Safe animatable) {
        return JahdooHelpers.res("animations/entity/safe.animation.json");
    }

}

