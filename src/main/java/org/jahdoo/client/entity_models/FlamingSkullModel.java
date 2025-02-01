package org.jahdoo.client.entity_models;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.entities.BurningSkull;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;


public class FlamingSkullModel extends GeoModel<BurningSkull> {

    @Override
    public ResourceLocation getModelResource(BurningSkull animatable) {
        return  ModHelpers.res("geo/entity/flaming_skull.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BurningSkull animatable) {
        return ModHelpers.res("textures/entity/flaming_skull.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BurningSkull animatable) {
        return ModHelpers.res("animations/entity/flaming_skull.animation.json");
    }

    @Override
    public void applyMolangQueries(AnimationState<BurningSkull> animationState, double animTime) {
        super.applyMolangQueries(animationState, animTime);
    }
}

