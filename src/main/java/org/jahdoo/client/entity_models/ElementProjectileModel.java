package org.jahdoo.client.entity_models;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.entities.ElementProjectile;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.model.GeoModel;


public class ElementProjectileModel extends GeoModel<ElementProjectile> {

    private final ResourceLocation getModel;

    public ElementProjectileModel(ResourceLocation getModel){
        this.getModel = getModel;
    }

    @Override
    public ResourceLocation getModelResource(ElementProjectile animatable) {
        return ModHelpers.modResourceLocation("geo/entity/projectile.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ElementProjectile animatable) {
        return getModel;
    }

    @Override
    public ResourceLocation getAnimationResource(ElementProjectile animatable) {
        return ModHelpers.modResourceLocation("animations/entity/projectile.animation.json");
    }

}

