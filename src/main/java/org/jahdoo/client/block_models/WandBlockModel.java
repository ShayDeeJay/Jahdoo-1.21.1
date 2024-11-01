package org.jahdoo.client.block_models;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.registers.ItemsRegister;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.model.GeoModel;

import java.util.Map;

import static org.jahdoo.block.wand.WandBlockEntity.GET_WAND_SLOT;

public class WandBlockModel extends GeoModel<WandBlockEntity> {
    @Override
    public ResourceLocation getModelResource(WandBlockEntity animatable) {
        return ModHelpers.modResourceLocation("geo/block/wand.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WandBlockEntity animatable) {
        ItemStack itemStack = animatable.inputItemHandler.getStackInSlot(GET_WAND_SLOT);
        Map<Item, String> wandTextures = Map.of(
            ItemsRegister.WAND_ITEM_FROST.get(), "wand_frost",
            ItemsRegister.WAND_ITEM_INFERNO.get(), "wand_inferno",
            ItemsRegister.WAND_ITEM_LIGHTNING.get(), "wand_lightning",
            ItemsRegister.WAND_ITEM_VITALITY.get(), "wand_vitality"
        );

        String type = wandTextures.getOrDefault(itemStack.getItem(), "wand_mystic");
        String texturePath = "textures/block/" + type + ".png";
        return ModHelpers.modResourceLocation(texturePath);
    }

    @Override
    public ResourceLocation getAnimationResource(WandBlockEntity animatable) {
        return ModHelpers.modResourceLocation("animations/block/wand.animation.json");
    }

    @Override
    public RenderType getRenderType(WandBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
