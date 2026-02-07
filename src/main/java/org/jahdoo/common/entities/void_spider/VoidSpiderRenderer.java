package org.jahdoo.common.entities.void_spider;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Spider;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jetbrains.annotations.NotNull;

public class VoidSpiderRenderer<T extends Spider> extends MobRenderer<T, SpiderModel<T>> {
    private static final ResourceLocation SPIDER_MOTHER = JahdooHelpers.res("textures/entity/void_spider.png");
    private static final ResourceLocation SPIDER_LOCATION_SPAWN = JahdooHelpers.res("textures/entity/void_spider_next.png");

    public VoidSpiderRenderer(EntityRendererProvider.Context p_174401_) {
        this(p_174401_, ModelLayers.SPIDER);
    }

    public VoidSpiderRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer) {
        super(context, new SpiderModel<>(context.bakeLayer(layer)), 0.8F);
        this.addLayer(new VoidSpiderEyesLayer(this));
    }

    protected float getFlipDegrees(@NotNull T livingEntity) {
        return 180.0F;
    }

    public ResourceLocation getTextureLocation(T entity) {
        return entity.getScale() == 1.5F ? SPIDER_LOCATION_SPAWN : SPIDER_MOTHER;
    }
}
