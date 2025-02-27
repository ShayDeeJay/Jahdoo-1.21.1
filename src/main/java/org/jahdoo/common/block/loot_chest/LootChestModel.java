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
    public RenderType getRenderType(LootChestEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getTextureResource(LootChestEntity animatable) {
//        System.out.println(ModHelpers.res("textures/block/loot_chest_1"));
        return Helpers.res("textures/block/"+animatable.getTexture);
    }

    @Override
    public ResourceLocation getModelResource(LootChestEntity animatable, @Nullable GeoRenderer<LootChestEntity> renderer) {
        return super.getModelResource(animatable, renderer);
    }
}
