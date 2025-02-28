package org.jahdoo.common.items.armor.wizard;

import org.jahdoo.ascension.utils.Helpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WizardArmorRenderer extends GeoArmorRenderer<WizardArmor> {

    public WizardArmorRenderer() {
        super(new DefaultedItemGeoModel<>(Helpers.res("armor/wizard_armor")));
    }



}
