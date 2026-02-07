package org.jahdoo.common.items.armor.ancient_golem_armor;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class AncientGolemArmorRenderer extends GeoArmorRenderer<AncientGolemArmor> {

    public AncientGolemArmorRenderer() {
        super(new DefaultedItemGeoModel<>(JahdooHelpers.res("armor/ancient_golem_armor")));
    }

    @Override
    public ResourceLocation getTextureLocation(AncientGolemArmor animatable) {
        return JahdooHelpers.res("textures/item/armor/ancient_golem_armor.png");
    }

}
