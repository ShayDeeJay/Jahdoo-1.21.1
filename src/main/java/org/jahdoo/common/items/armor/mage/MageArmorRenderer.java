package org.jahdoo.common.items.armor.mage;

import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.items.armor.wizard.WizardArmor;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class MageArmorRenderer extends GeoArmorRenderer<WizardArmor> {

    public MageArmorRenderer() {
        super(new DefaultedItemGeoModel<>(JahdooHelpers.res("armor/mage_armor")));
    }

}
