package org.jahdoo.common.block.altar;

import org.jahdoo.common.items.block_items.ChallengeAltarBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class AltarBlockRenderer extends GeoItemRenderer<ChallengeAltarBlockItem> {

    public AltarBlockRenderer() {
        super(new AltarBlockModel());
    }

}
