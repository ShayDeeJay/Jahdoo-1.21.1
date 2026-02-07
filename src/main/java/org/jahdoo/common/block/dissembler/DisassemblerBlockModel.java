package org.jahdoo.common.block.dissembler;

import org.jahdoo.common.items.block_items.DisassemblerBlockItem;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class DisassemblerBlockModel extends DefaultedItemGeoModel<DisassemblerBlockItem> {
    public DisassemblerBlockModel() {
        super(JahdooHelpers.res("disassembler"));
    }
}
