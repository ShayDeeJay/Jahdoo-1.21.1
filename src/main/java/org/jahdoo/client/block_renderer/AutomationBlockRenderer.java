package org.jahdoo.client.block_renderer;

import org.jahdoo.client.block_models.AutomationBlockModel;
import org.jahdoo.items.block_items.AutomationBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AutomationBlockRenderer extends GeoItemRenderer<AutomationBlockItem> {

    public AutomationBlockRenderer() {
        super(new AutomationBlockModel());
    }



}
