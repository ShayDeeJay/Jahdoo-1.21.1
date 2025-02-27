package org.jahdoo.common.block.infuser;

import org.jahdoo.common.items.block_items.InfuserBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class InfuserBlockRenderer extends GeoItemRenderer<InfuserBlockItem> {

    public InfuserBlockRenderer() {
        super(new InfuserBlockModel());
    }



}
