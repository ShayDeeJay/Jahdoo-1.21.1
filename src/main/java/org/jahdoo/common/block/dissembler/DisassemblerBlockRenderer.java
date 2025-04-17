package org.jahdoo.common.block.dissembler;

import org.jahdoo.common.items.block_items.DisassemblerBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DisassemblerBlockRenderer extends GeoItemRenderer<DisassemblerBlockItem> {
    public DisassemblerBlockRenderer() {
        super(new DisassemblerBlockModel());
    }
}
