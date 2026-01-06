package org.jahdoo.common.block.mystical_augmenter;

import org.jahdoo.common.items.block_items.MysticalAugmenterItem;
import org.jahdoo.trial_nexus.utils.Helpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class MysticalAugmenterBlockModel extends DefaultedItemGeoModel<MysticalAugmenterItem> {
    public MysticalAugmenterBlockModel() {
        super(Helpers.res("mystical_augmenter"));
    }
}
