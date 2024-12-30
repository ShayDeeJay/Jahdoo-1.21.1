package org.jahdoo.client.entity_renderer.decoy;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.entities.living.Decoy;
import org.jahdoo.utils.ModHelpers;

public class AbstractDecoyRenderer <T extends Decoy, M extends DecoyModel<T>> extends HumanoidMobRenderer<T, M> {
    public static final ResourceLocation DECOY = ModHelpers.res("textures/entity/eternal_wizard/decoy.png");

    protected AbstractDecoyRenderer(EntityRendererProvider.Context pContext, M pModel, M pInnerModel, M pOuterModel) {
        super(pContext, pModel, 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, pInnerModel, pOuterModel, pContext.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(T pEntity) {
        return DECOY;
    }
}
