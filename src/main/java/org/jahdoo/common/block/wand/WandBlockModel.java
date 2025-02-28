package org.jahdoo.common.block.wand;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.registers.ItemsRegister;
import org.jahdoo.ascension.utils.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.Map;

import static net.minecraft.client.renderer.RenderType.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.common.block.wand.WandBlockEntity.GET_WAND_SLOT;
import static org.jahdoo.common.registers.ItemsRegister.*;

public class WandBlockModel extends GeoModel<WandBlockEntity> {

    @Override
    public ResourceLocation getModelResource(WandBlockEntity animatable, @Nullable GeoRenderer<WandBlockEntity> renderer) {
        return res("geo/block/wand.geo.json");
    }

    @Override
    public ResourceLocation getModelResource(WandBlockEntity animatable) {
        return res("geo/block/wand.geo.json");
    }

    @Override
    public ResourceLocation getAnimationResource(WandBlockEntity animatable) {
        return res("animations/block/wand.animation.json");
    }

    @Override
    public RenderType getRenderType(WandBlockEntity animatable, ResourceLocation texture) {
        return entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getTextureResource(WandBlockEntity animatable) {
        return getResourceLocation(animatable);
    }

    @Override
    public ResourceLocation getTextureResource(WandBlockEntity animatable, @Nullable GeoRenderer<WandBlockEntity> renderer) {
        return getResourceLocation(animatable);
    }

    private static @NotNull ResourceLocation getResourceLocation(WandBlockEntity animatable) {
        var itemStack = animatable.inputItemHandler.getStackInSlot(GET_WAND_SLOT);
        var wandTextures = Map.of(
            WAND_ITEM_FROST.get(), "wand_frost",
            WAND_ITEM_INFERNO.get(), "wand_inferno",
            WAND_ITEM_VITALITY.get(), "wand_vitality"
        );

        var type = wandTextures.getOrDefault(itemStack.getItem(), "wand_mystic");
        var texturePath = "textures/block/" + type + ".png";
        return res(texturePath);
    }

}
