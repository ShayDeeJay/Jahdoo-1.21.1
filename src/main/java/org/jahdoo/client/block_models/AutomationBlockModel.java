package org.jahdoo.client.block_models;

import org.jahdoo.items.block_items.AutomationBlockItem;
import org.jahdoo.items.block_items.InfuserBlockItem;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class AutomationBlockModel extends DefaultedItemGeoModel<AutomationBlockItem> {
    public AutomationBlockModel() {
        super(ModHelpers.modResourceLocation("automation_block"));
    }
}
