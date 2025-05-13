package org.jahdoo.common.block.loot_chest;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.Helpers;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.List;

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
        return Helpers.res(
            entity.isCoinChest() ? "textures/block/coin_chest.png" :
            "textures/block/" + List.of(
                "loot_chest.png",
                "loot_chest_1.png",
                "loot_chest_2.png",
                "loot_chest_3.png"
            ).get(entity.getRarity)
        );
    }

    @Override
    public ResourceLocation getModelResource(LootChestEntity entity, GeoRenderer<LootChestEntity> renderer) {
        var coinChest = "geo/block/coin_chest.geo.json";
        var lootChest = "geo/block/loot_chest.geo.json";
        return Helpers.res(entity.isCoinChest() ? coinChest : lootChest);
    }

}
