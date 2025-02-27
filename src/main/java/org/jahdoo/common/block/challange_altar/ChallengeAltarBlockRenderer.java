package org.jahdoo.common.block.challange_altar;

import org.jahdoo.common.items.block_items.ChallengeAltarBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ChallengeAltarBlockRenderer extends GeoItemRenderer<ChallengeAltarBlockItem> {

    public ChallengeAltarBlockRenderer() {
        super(new ChallengeAltarBlockModel());
    }

}
