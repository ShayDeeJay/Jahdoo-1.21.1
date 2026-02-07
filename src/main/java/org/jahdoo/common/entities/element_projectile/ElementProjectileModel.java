package org.jahdoo.common.entities.element_projectile;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.model.GeoModel;


public class ElementProjectileModel extends GeoModel<ElementProjectile> {

    private final ResourceLocation getModel;

    public ElementProjectileModel(ResourceLocation getModel){
        this.getModel = getModel;
    }


    @Override
    public ResourceLocation getModelResource(ElementProjectile animatable) {
        return JahdooHelpers.res("geo/entity/projectile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ElementProjectile animatable) {
        return getModel;
    }

    @Override
    public ResourceLocation getAnimationResource(ElementProjectile animatable) {
        return JahdooHelpers.res("animations/entity/projectile.animation.json");
    }

}

