package org.jahdoo.client.block_models;

import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.items.block_items.ChallengeAltarBlockItem;
import org.jahdoo.items.block_items.InfuserBlockItem;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class ChallengeAltarBlockModel extends DefaultedItemGeoModel<ChallengeAltarBlockItem> {
    public ChallengeAltarBlockModel() {
        super(ModHelpers.res("challenge_alter"));
    }
}
