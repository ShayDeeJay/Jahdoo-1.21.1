package org.jahdoo.client.entity_renderer.ancient_golem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.IronGolemCrackinessLayer;
import net.minecraft.client.renderer.entity.layers.IronGolemFlowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;
import org.jahdoo.utils.ModHelpers;

public class AncientGolemRenderer extends MobRenderer<IronGolem, IronGolemModel<IronGolem>> {

    private static final ResourceLocation GOLEM_LOCATION = ModHelpers.res("textures/entity/ancient_golem.png");

    public AncientGolemRenderer(EntityRendererProvider.Context p_174188_) {
        super(p_174188_, new IronGolemModel(p_174188_.bakeLayer(ModelLayers.IRON_GOLEM)), 0.7F);
        this.addLayer(new IronGolemCrackinessLayer(this));
        this.addLayer(new IronGolemFlowerLayer(this, p_174188_.getBlockRenderDispatcher()));
    }

    public ResourceLocation getTextureLocation(IronGolem entity) {
        return GOLEM_LOCATION;
    }

    protected void setupRotations(IronGolem entity, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale) {
        super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
        if (!((double)entity.walkAnimation.speed() < 0.01)) {
            float f = 13.0F;
            float f1 = entity.walkAnimation.position(partialTick) + 6.0F;
            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            poseStack.mulPose(Axis.ZP.rotationDegrees(6.5F * f2));
        }

    }
}
