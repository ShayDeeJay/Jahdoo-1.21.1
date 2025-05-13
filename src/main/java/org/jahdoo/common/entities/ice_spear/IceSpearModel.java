package org.jahdoo.common.entities.ice_spear;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.Helpers;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class IceSpearModel extends GeoModel<IceSpear> {

    @Override
    public ResourceLocation getModelResource(IceSpear entity) {
        return  Helpers.res("geo/entity/ice_spear.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IceSpear entity) {
        return Helpers.res("textures/entity/ice_spear.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IceSpear entity) {
        return Helpers.res("animations/entity/ice_spear.animation.json");
    }

    @Override
    public void applyMolangQueries(AnimationState<IceSpear> animationState, double animTime) {
        super.applyMolangQueries(animationState, animTime);
    }

}

