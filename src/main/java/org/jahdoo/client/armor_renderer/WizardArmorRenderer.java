package org.jahdoo.client.armor_renderer;

import org.jahdoo.items.armor.WizardArmor;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WizardArmorRenderer extends GeoArmorRenderer<WizardArmor> {

    public WizardArmorRenderer() {
        super(new DefaultedItemGeoModel<>(ModHelpers.modResourceLocation("armor/wizard_armor")));
    }

}
