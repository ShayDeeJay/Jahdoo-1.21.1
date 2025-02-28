package org.jahdoo.common.block.loot_chest;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.ascension.utils.Helpers;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class LootChestModel extends DefaultedBlockGeoModel<LootChestEntity> {
    public LootChestModel() {
        super(Helpers.res("loot_chest"));
    }

    @Override
    public RenderType getRenderType(LootChestEntity entity, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(entity));
    }

    @Override
    public ResourceLocation getTextureResource(LootChestEntity entity) {
        return Helpers.res("textures/block/"+entity.getTexture);
    }

    @Override
    public ResourceLocation getModelResource(LootChestEntity entity, GeoRenderer<LootChestEntity> renderer) {
        return super.getModelResource(entity, renderer);
    }
}
