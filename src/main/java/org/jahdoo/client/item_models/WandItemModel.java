package org.jahdoo.client.item_models;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.JahdooMod;
import org.jahdoo.items.wand.WandItem;
import org.jahdoo.utils.GeneralHelpers;
import software.bernie.geckolib.model.GeoModel;

public class WandItemModel extends GeoModel<WandItem> {
    @Override
    public ResourceLocation getModelResource(WandItem animatable) {
        return GeneralHelpers.modResourceLocation("geo/item/wand.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WandItem animatable) {
        return GeneralHelpers.modResourceLocation("textures/item/"+animatable.location+".png");
    }

    @Override
    public ResourceLocation getAnimationResource(WandItem animatable) {
        return GeneralHelpers.modResourceLocation("animations/item/wand.animation.json");
    }

}