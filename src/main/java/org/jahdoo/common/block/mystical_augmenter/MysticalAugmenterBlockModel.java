package org.jahdoo.common.block.mystical_augmenter;

import org.jahdoo.common.items.block_items.MysticalAugmenterItem;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class MysticalAugmenterBlockModel extends DefaultedItemGeoModel<MysticalAugmenterItem> {
    public MysticalAugmenterBlockModel() {
        super(JahdooHelpers.res("mystical_augmenter"));
    }
}
