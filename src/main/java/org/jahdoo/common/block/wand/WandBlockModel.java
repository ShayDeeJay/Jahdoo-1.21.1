package org.jahdoo.common.block.wand;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.registers.ItemsRegister;
import org.jahdoo.ascension.utils.Helpers;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.Map;

import static org.jahdoo.common.block.wand.WandBlockEntity.GET_WAND_SLOT;

public class WandBlockModel extends GeoModel<WandBlockEntity> {
//    @Override
//    public ResourceLocation getModelResource(WandBlockEntity animatable) {
//        return ModHelpers.res("geo/block/wand.geo.json");
//    }

    @Override
    public ResourceLocation getModelResource(WandBlockEntity animatable, @Nullable GeoRenderer<WandBlockEntity> renderer) {
        return Helpers.res("geo/block/wand.geo.json");
    }

    @Override
    public ResourceLocation getModelResource(WandBlockEntity animatable) {
        return Helpers.res("geo/block/wand.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WandBlockEntity animatable, @Nullable GeoRenderer<WandBlockEntity> renderer) {
        ItemStack itemStack = animatable.inputItemHandler.getStackInSlot(GET_WAND_SLOT);
        Map<Item, String> wandTextures = Map.of(
            ItemsRegister.WAND_ITEM_FROST.get(), "wand_frost",
            ItemsRegister.WAND_ITEM_INFERNO.get(), "wand_inferno",
            ItemsRegister.WAND_ITEM_VITALITY.get(), "wand_vitality"
        );

        String type = wandTextures.getOrDefault(itemStack.getItem(), "wand_mystic");
        String texturePath = "textures/block/" + type + ".png";
        return Helpers.res(texturePath);
    }

    @Override
    public ResourceLocation getTextureResource(WandBlockEntity animatable) {
        ItemStack itemStack = animatable.inputItemHandler.getStackInSlot(GET_WAND_SLOT);
        Map<Item, String> wandTextures = Map.of(
            ItemsRegister.WAND_ITEM_FROST.get(), "wand_frost",
            ItemsRegister.WAND_ITEM_INFERNO.get(), "wand_inferno",
            ItemsRegister.WAND_ITEM_VITALITY.get(), "wand_vitality"
        );

        String type = wandTextures.getOrDefault(itemStack.getItem(), "wand_mystic");
        String texturePath = "textures/block/" + type + ".png";
        return Helpers.res(texturePath);
    }

//    @Override
//    public ResourceLocation getTextureResource(WandBlockEntity animatable) {
//        ItemStack itemStack = animatable.inputItemHandler.getStackInSlot(GET_WAND_SLOT);
//        Map<Item, String> wandTextures = Map.of(
//            ItemsRegister.WAND_ITEM_FROST.get(), "wand_frost",
//            ItemsRegister.WAND_ITEM_INFERNO.get(), "wand_inferno",
//            ItemsRegister.WAND_ITEM_LIGHTNING.get(), "wand_lightning",
//            ItemsRegister.WAND_ITEM_VITALITY.get(), "wand_vitality"
//        );
//
//        String type = wandTextures.getOrDefault(itemStack.getItem(), "wand_mystic");
//        String texturePath = "textures/block/" + type + ".png";
//        return ModHelpers.res(texturePath);
//    }

    @Override
    public ResourceLocation getAnimationResource(WandBlockEntity animatable) {
        return Helpers.res("animations/block/wand.animation.json");
    }

    @Override
    public RenderType getRenderType(WandBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
