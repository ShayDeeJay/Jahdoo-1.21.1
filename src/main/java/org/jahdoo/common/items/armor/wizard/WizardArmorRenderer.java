package org.jahdoo.common.items.armor.wizard;

import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WizardArmorRenderer extends GeoArmorRenderer<WizardArmor> {

    public WizardArmorRenderer() {
        super(new DefaultedItemGeoModel<>(JahdooHelpers.res("armor/wizard_armor")));
    }



}
