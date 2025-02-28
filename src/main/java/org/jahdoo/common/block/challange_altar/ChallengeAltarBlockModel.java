package org.jahdoo.common.block.challange_altar;

import org.jahdoo.common.items.block_items.ChallengeAltarBlockItem;
import org.jahdoo.ascension.utils.Helpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ChallengeAltarBlockModel extends DefaultedItemGeoModel<ChallengeAltarBlockItem> {

    public ChallengeAltarBlockModel() {
        super(Helpers.res("challenge_altar"));
    }

}
