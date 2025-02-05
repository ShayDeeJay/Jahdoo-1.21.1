package org.jahdoo.client.entity_renderer.void_spider;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Spider;
import org.jahdoo.entities.living.VoidSpider;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

public class VoidSpiderRenderer<T extends Spider> extends MobRenderer<T, SpiderModel<T>> {
    private static final ResourceLocation SPIDER_LOCATION = ModHelpers.res("textures/entity/void_spider.png");
    private static final ResourceLocation SPIDER_LOCATION_SPAWN = ModHelpers.res("textures/entity/void_spider_next.png");

    public VoidSpiderRenderer(EntityRendererProvider.Context p_174401_) {
        this(p_174401_, ModelLayers.SPIDER);
    }

    public VoidSpiderRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer) {
        super(context, new SpiderModel<>(context.bakeLayer(layer)), 0.8F);
        this.addLayer(new VoidSpiderEyesLayer(this));
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    protected float getFlipDegrees(@NotNull T livingEntity) {
        return 180.0F;
    }

    public ResourceLocation getTextureLocation(T entity) {
        if(entity instanceof VoidSpider voidSpider){
            return voidSpider.getScale() == 1.5F ? SPIDER_LOCATION_SPAWN : SPIDER_LOCATION;
        }

        return SPIDER_LOCATION_SPAWN;
    }
}
