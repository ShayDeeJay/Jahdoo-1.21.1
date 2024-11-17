package org.jahdoo.client.entity_renderer.etneral_wizzard;

import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class EternalWizardBodyRenderer extends HumanoidMobRenderer<AbstractSkeleton, WizardModel<AbstractSkeleton>> {
    private static final ResourceLocation SKELETON_LOCATION = ModHelpers.res("textures/entity/eternal_wizard/eternal_wizard.png");

    public EternalWizardBodyRenderer(EntityRendererProvider.Context pContext, ModelLayerLocation pSkeletonLayer, ModelLayerLocation pInnerModelLayer, ModelLayerLocation pOuterModelLayer) {
        super(pContext, new WizardModel<>(pContext.bakeLayer(pSkeletonLayer)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
            new SkeletonModel(pContext.bakeLayer(pInnerModelLayer)),
            new SkeletonModel(pContext.bakeLayer(pOuterModelLayer)),
            pContext.getModelManager())
        );
    }

    public @NotNull ResourceLocation getTextureLocation(AbstractSkeleton pEntity) {
        return SKELETON_LOCATION;
    }

    protected boolean isShaking(AbstractSkeleton pEntity) {
        return pEntity.isShaking();
    }
}
