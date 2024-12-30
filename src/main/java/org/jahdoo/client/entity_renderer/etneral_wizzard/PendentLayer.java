package org.jahdoo.client.entity_renderer.etneral_wizzard;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import org.jahdoo.entities.living.EternalWizard;
import org.jahdoo.utils.ModHelpers;

public class PendentLayer <T extends AbstractSkeleton> extends EyesLayer<T, WizardModel<T>> {
    private static final RenderType PENDENT = RenderType.eyes(ModHelpers.res("textures/entity/eternal_wizard/pendent.png"));

    public PendentLayer(RenderLayerParent<T, WizardModel<T>> pRenderer) {
        super(pRenderer);
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if(pLivingEntity instanceof EternalWizard eternalWizard){
            if(eternalWizard.getMode()){
                pPoseStack.pushPose();
                pPoseStack.translate(0F, 0f, -0.03F);
                pPoseStack.scale(1.0F, 1.0F, 1.0F);
                super.render(pPoseStack, pBuffer, pPackedLight, pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks, pAgeInTicks, pNetHeadYaw, pHeadPitch);
                pPoseStack.popPose();
            }
        }
    }

    @Override
    public RenderType renderType() {
        return PENDENT;
    }
}
