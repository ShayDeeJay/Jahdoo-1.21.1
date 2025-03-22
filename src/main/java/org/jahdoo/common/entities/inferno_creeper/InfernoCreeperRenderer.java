package org.jahdoo.common.entities.inferno_creeper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CreeperPowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;
import org.jahdoo.ascension.utils.Helpers;

public class InfernoCreeperRenderer extends MobRenderer<Creeper, CreeperModel<Creeper>> {

    private static final ResourceLocation INFERNO_CREEPER = Helpers.res("textures/entity/inferno_creeper.png");

    public InfernoCreeperRenderer(EntityRendererProvider.Context context) {
        super(context, new CreeperModel<>(context.bakeLayer(ModelLayers.CREEPER)), 0.5F);
        this.addLayer(new CreeperPowerLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(Creeper creeper) {
        return INFERNO_CREEPER;
    }

    protected void scale(Creeper livingEntity, PoseStack poseStack, float partialTickTime) {
        float f = livingEntity.getSwelling(partialTickTime);
        float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
        f = Mth.clamp(f, 0.0F, 1.0F);
        f *= f;
        f *= f;
        float f2 = (1.0F + f * 0.4F) * f1;
        float f3 = (1.0F + f * 0.1F) / f1;
        poseStack.scale(f2, f3, f2);
    }

    protected float getWhiteOverlayProgress(Creeper livingEntity, float partialTicks) {
        float f = livingEntity.getSwelling(partialTicks);
        return (int)(f * 20.0F) % 2 == 0 ? 0.0F : Mth.clamp(f, 0.5F, 1.0F);
    }

}
