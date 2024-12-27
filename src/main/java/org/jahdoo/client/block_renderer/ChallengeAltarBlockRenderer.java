package org.jahdoo.client.block_renderer;

import org.jahdoo.client.block_models.ChallengeAltarBlockModel;
import org.jahdoo.client.block_models.InfuserBlockModel;
import org.jahdoo.items.block_items.ChallengeAltarBlockItem;
import org.jahdoo.items.block_items.InfuserBlockItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ChallengeAltarBlockRenderer extends GeoItemRenderer<ChallengeAltarBlockItem> {

    public ChallengeAltarBlockRenderer() {
        super(new ChallengeAltarBlockModel());
    }

}
