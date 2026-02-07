package org.jahdoo.common.entities.decoy;

import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import static net.minecraft.client.renderer.entity.EntityRendererProvider.*;

public class AbstractDecoyRenderer <T extends Decoy, M extends DecoyModel<T>> extends HumanoidMobRenderer<T, M> {

    public static final ResourceLocation DECOY = JahdooHelpers.res("textures/entity/eternal_wizard/decoy.png");

    protected AbstractDecoyRenderer(Context context, M model, M innerModel, M outerModel) {
        super(context, model, 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, innerModel, outerModel, context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return DECOY;
    }

}
