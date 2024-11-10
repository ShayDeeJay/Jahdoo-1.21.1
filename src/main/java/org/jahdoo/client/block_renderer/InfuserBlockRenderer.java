package org.jahdoo.client.block_renderer;

import org.jahdoo.client.block_models.InfuserBlockModel;
import org.jahdoo.items.block_items.InfuserBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class InfuserBlockRenderer extends GeoItemRenderer<InfuserBlockItem> {

    public InfuserBlockRenderer() {
        super(new InfuserBlockModel());
    }



}
