package org.jahdoo.common.items.caster_item.elemental_wand;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.model.GeoModel;

public class ElementalWandItemModel extends GeoModel<ElementalWand> {
    @Override
    public ResourceLocation getModelResource(ElementalWand animatable) {
        return JahdooHelpers.res("geo/item/" + ElementalWand.isBasic(animatable.type) + "wand.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ElementalWand animatable) {
        return JahdooHelpers.res("textures/item/"+ElementalWand.isBasic(animatable.type) + animatable.location+".png");
    }

    @Override
    public ResourceLocation getAnimationResource(ElementalWand animatable) {
        return JahdooHelpers.res("animations/item/wand.animation.json");
    }

}