package org.jahdoo.common.client.armor_renderer;

import org.jahdoo.common.items.armor.WizardArmor;
import org.jahdoo.ascension.utils.Helpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MageArmorRenderer extends GeoArmorRenderer<WizardArmor> {

    public MageArmorRenderer() {
        super(new DefaultedItemGeoModel<>(Helpers.res("armor/mage_armor")));
    }

}
