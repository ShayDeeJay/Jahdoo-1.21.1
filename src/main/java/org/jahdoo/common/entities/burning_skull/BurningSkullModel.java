package org.jahdoo.common.entities.burning_skull;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.ascension.utils.Helpers;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;


public class BurningSkullModel extends GeoModel<BurningSkull> {

    @Override
    public ResourceLocation getModelResource(BurningSkull animatable) {
        return  Helpers.res("geo/entity/flaming_skull.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BurningSkull animatable) {
        return Helpers.res("textures/entity/flaming_skull.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BurningSkull animatable) {
        return Helpers.res("animations/entity/flaming_skull.animation.json");
    }

    @Override
    public void applyMolangQueries(AnimationState<BurningSkull> animationState, double animTime) {
        super.applyMolangQueries(animationState, animTime);
    }
}

