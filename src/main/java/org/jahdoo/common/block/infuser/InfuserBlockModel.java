package org.jahdoo.common.block.infuser;

import org.jahdoo.common.items.block_items.InfuserBlockItem;
import org.jahdoo.ascension.utils.Helpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class InfuserBlockModel extends DefaultedItemGeoModel<InfuserBlockItem> {
    public InfuserBlockModel() {
        super(Helpers.res("infuser"));
    }
}
