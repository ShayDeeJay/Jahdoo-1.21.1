package org.jahdoo.common.entities.eternal_wizard;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jahdoo.ascension.utils.Helpers;

import static net.minecraft.client.renderer.entity.EntityRendererProvider.*;

@OnlyIn(Dist.CLIENT)
public class EternalWizardBodyRenderer extends HumanoidMobRenderer<EternalWizard, WizardModel<EternalWizard>> {

    private static final ResourceLocation SKELETON_LOCATION = Helpers.res("textures/entity/eternal_wizard/eternal_wizard.png");

    public EternalWizardBodyRenderer(
        Context context,
        ModelLayerLocation skeletonLayer,
        ModelLayerLocation innerModelLayer,
        ModelLayerLocation outerModelLayer
    ) {
        super(context, new WizardModel<>(context.bakeLayer(skeletonLayer)), 0.5F);

        this.addLayer(new HumanoidArmorLayer<>(this,
            new WizardModel<>(context.bakeLayer(innerModelLayer)),
            new WizardModel<>(context.bakeLayer(outerModelLayer)),
            context.getModelManager())
        );
    }

    @Override
    public ResourceLocation getTextureLocation(EternalWizard eternalWizard) {
        return SKELETON_LOCATION;
    }

}
