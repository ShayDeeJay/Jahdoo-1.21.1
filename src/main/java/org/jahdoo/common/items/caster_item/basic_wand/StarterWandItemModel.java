package org.jahdoo.common.items.caster_item.basic_wand;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.Helpers;
import software.bernie.geckolib.model.GeoModel;

public class StarterWandItemModel extends GeoModel<StarterWand> {

    @Override
    public ResourceLocation getModelResource(StarterWand animatable) {
        return Helpers.res("geo/item/wand_basic.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StarterWand animatable) {
        return Helpers.res("textures/item/wand_basic.png");
    }

    @Override
    public ResourceLocation getAnimationResource(StarterWand animatable) {
        return Helpers.res("animations/item/wand_basic.animation.json");
    }

}