package org.jahdoo.common.entities.ice_spear;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class IceSpearModel extends GeoModel<IceSpear> {

    @Override
    public ResourceLocation getModelResource(IceSpear entity) {
        return  JahdooHelpers.res("geo/entity/ice_spear.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IceSpear entity) {
        return JahdooHelpers.res("textures/entity/ice_spear.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IceSpear entity) {
        return JahdooHelpers.res("animations/entity/ice_spear.animation.json");
    }

    @Override
    public void applyMolangQueries(AnimationState<IceSpear> animationState, double animTime) {
        super.applyMolangQueries(animationState, animTime);
    }

}

