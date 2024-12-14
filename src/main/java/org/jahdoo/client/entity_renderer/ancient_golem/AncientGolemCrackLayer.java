package org.jahdoo.client.entity_renderer.ancient_golem;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.animal.IronGolem;
import org.jahdoo.utils.ModHelpers;

import java.util.Map;

public class AncientGolemCrackLayer extends RenderLayer<IronGolem, IronGolemModel<IronGolem>> {
    private static final Map<Crackiness.Level, ResourceLocation> resourceLocations;

    public AncientGolemCrackLayer(RenderLayerParent<IronGolem, IronGolemModel<IronGolem>> renderer) {
        super(renderer);
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, IronGolem livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!livingEntity.isInvisible()) {
            Crackiness.Level crackedLevel = livingEntity.getCrackiness();
            if (crackedLevel != Crackiness.Level.NONE) {
                ResourceLocation resourcelocation = resourceLocations.get(crackedLevel);
                renderColoredCutoutModel(this.getParentModel(), resourcelocation, poseStack, buffer, packedLight, livingEntity, -1);
            }
        }
    }

    static {
        resourceLocations = ImmutableMap.of(
            Crackiness.Level.LOW, ModHelpers.res("textures/entity/ancient_golem/ancient_golem_crackiness_low.png"),
            Crackiness.Level.MEDIUM, ModHelpers.res("textures/entity/ancient_golem/ancient_golem_crackiness_medium.png"),
            Crackiness.Level.HIGH, ModHelpers.res("textures/entity/ancient_golem/ancient_golem_crackiness_high.png")
        );
    }
}