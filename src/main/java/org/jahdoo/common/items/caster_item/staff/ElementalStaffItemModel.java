package org.jahdoo.common.items.caster_item.staff;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.Helpers;
import software.bernie.geckolib.model.GeoModel;

public class ElementalStaffItemModel extends GeoModel<ElementalStaff> {

    @Override
    public ResourceLocation getModelResource(ElementalStaff animatable) {
        return Helpers.res("geo/item/elemental_staff.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ElementalStaff animatable) {
        return Helpers.res("textures/item/elemental_staff.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ElementalStaff animatable) {
        return Helpers.res("animations/item/elemental_staff.animation.json");
    }

}