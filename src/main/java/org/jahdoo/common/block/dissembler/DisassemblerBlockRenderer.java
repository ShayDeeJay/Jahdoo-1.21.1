package org.jahdoo.common.block.dissembler;

import org.jahdoo.common.items.block_items.InfuserBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DisassemblerBlockRenderer extends GeoItemRenderer<InfuserBlockItem> {
    public DisassemblerBlockRenderer() {
        super(new DisassemblerBlockModel());
    }
}
