package org.jahdoo.client.block_models;

import org.jahdoo.items.infuser_block_item.InfuserBlockItem;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class InfuserBlockModel extends DefaultedItemGeoModel<InfuserBlockItem> {
    public InfuserBlockModel() {
        super(ModHelpers.modResourceLocation("infuser"));
    }
}
