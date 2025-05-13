package org.jahdoo.common.items.armor.knight_king;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.Helpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class KnightKingArmorRenderer extends GeoArmorRenderer<KnightKingArmor> {

    public KnightKingArmorRenderer() {
        super(new DefaultedItemGeoModel<>(Helpers.res("armor/knight_king_armor")));
    }

    @Override
    public ResourceLocation getTextureLocation(KnightKingArmor animatable) {
        return Helpers.res("textures/item/armor/knight_king_armor.png");
    }

}
