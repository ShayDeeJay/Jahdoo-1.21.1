package org.jahdoo.client.entity_renderer.etneral_wizzard;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import org.jahdoo.utils.ModHelpers;

public class
WizardClothingLayerRenderer<T extends Mob & RangedAttackMob, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation STRAY_CLOTHES_LOCATION = ModHelpers.res("textures/entity/eternal_wizard/eternal_wizard_outer_top_1.png");
    private final WizardModel<T> layerModel;

    public WizardClothingLayerRenderer(RenderLayerParent<T, M> pRenderer, EntityModelSet pModelSet) {
        super(pRenderer);
        this.layerModel = new WizardModel<>(pModelSet.bakeLayer(ModelLayers.STRAY_OUTER_LAYER));
    }

    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

        coloredCutoutModelCopyLayerRender(
            this.getParentModel(),
            this.layerModel,
            STRAY_CLOTHES_LOCATION,
            pPoseStack,
            pBuffer,
            pPackedLight,
            pLivingEntity,
            pLimbSwing,
            pLimbSwingAmount,
            pAgeInTicks,
            pNetHeadYaw,
            pHeadPitch,
            pPartialTicks,
            -1
        );

    }
}
