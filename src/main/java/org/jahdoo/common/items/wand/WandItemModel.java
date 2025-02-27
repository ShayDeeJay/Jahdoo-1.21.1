package org.jahdoo.common.items.wand;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.ascension.utils.Helpers;
import software.bernie.geckolib.model.GeoModel;

public class WandItemModel extends GeoModel<WandItem> {
    @Override
    public ResourceLocation getModelResource(WandItem animatable) {
        return Helpers.res("geo/item/wand.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WandItem animatable) {
        return Helpers.res("textures/item/"+animatable.location+".png");
    }

    @Override
    public ResourceLocation getAnimationResource(WandItem animatable) {
        return Helpers.res("animations/item/wand.animation.json");
    }

}