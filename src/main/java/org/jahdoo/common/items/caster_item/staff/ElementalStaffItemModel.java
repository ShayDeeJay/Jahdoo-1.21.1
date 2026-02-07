package org.jahdoo.common.items.caster_item.staff;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.model.GeoModel;

public class ElementalStaffItemModel extends GeoModel<ElementalStaff> {

    @Override
    public ResourceLocation getModelResource(ElementalStaff animatable) {
        return JahdooHelpers.res("geo/item/elemental_staff.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ElementalStaff animatable) {
        return JahdooHelpers.res("textures/item/elemental_staff.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ElementalStaff animatable) {
        return JahdooHelpers.res("animations/item/elemental_staff.animation.json");
    }

}