package org.jahdoo.common.block.altar;

import org.jahdoo.common.items.block_items.ChallengeAltarBlockItem;
import org.jahdoo.trial_nexus.utils.Helpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class AltarBlockModel extends DefaultedItemGeoModel<ChallengeAltarBlockItem> {

    public AltarBlockModel() {
        super(Helpers.res("challenge_altar"));
    }

}
