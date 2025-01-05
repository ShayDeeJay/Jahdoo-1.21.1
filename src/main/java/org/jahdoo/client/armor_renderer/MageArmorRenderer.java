package org.jahdoo.client.armor_renderer;

import org.jahdoo.items.armor.WizardArmor;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MageArmorRenderer extends GeoArmorRenderer<WizardArmor> {

    public MageArmorRenderer() {
        super(new DefaultedItemGeoModel<>(ModHelpers.res("armor/mage_armor")));
    }

}
