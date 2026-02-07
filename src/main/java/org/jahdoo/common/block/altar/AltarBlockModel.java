package org.jahdoo.common.block.altar;

import org.jahdoo.common.items.block_items.ChallengeAltarBlockItem;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class AltarBlockModel extends DefaultedItemGeoModel<ChallengeAltarBlockItem> {

    public AltarBlockModel() {
        super(JahdooHelpers.res("challenge_altar"));
    }

}
