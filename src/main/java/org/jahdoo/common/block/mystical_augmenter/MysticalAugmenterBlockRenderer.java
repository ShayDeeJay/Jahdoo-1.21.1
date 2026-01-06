package org.jahdoo.common.block.mystical_augmenter;

import org.jahdoo.common.items.block_items.MysticalAugmenterItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MysticalAugmenterBlockRenderer extends GeoItemRenderer<MysticalAugmenterItem> {
    public MysticalAugmenterBlockRenderer() {
        super(new MysticalAugmenterBlockModel());
    }
}
