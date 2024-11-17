package org.jahdoo.client.entity_renderer.etneral_wizzard;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jahdoo.entities.EternalWizard;
import org.jahdoo.utils.ModHelpers;

@OnlyIn(Dist.CLIENT)
public class EternalWizardRenderer extends EternalWizardBodyRenderer {
    private static final ResourceLocation ETERNAL_WIZARD = ModHelpers.res("textures/entity/eternal_wizard/eternal_wizard.png");

    public EternalWizardRenderer(EntityRendererProvider.Context context) {
        super(context, ModelLayers.STRAY, ModelLayers.STRAY_INNER_ARMOR, ModelLayers.STRAY_OUTER_ARMOR);
        this.addLayer(new WizardClothingLayerRenderer<>(this, context.getModelSet()));
    }

    @Override
    public void render(AbstractSkeleton pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if(!(pEntity instanceof EternalWizard eternalWizard)) return;
        eternalWizard.setScale(Math.min(1, (eternalWizard.getInternalScale() + 0.017f)));
        pPoseStack.pushPose();
        var scale = eternalWizard.getInternalScale();
        pPoseStack.scale(scale, scale, scale);
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
        pPoseStack.popPose();
    }

    public ResourceLocation getTextureLocation(AbstractSkeleton pEntity) {
        return ETERNAL_WIZARD;
    }
}
