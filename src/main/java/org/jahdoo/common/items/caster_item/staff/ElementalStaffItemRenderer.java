package org.jahdoo.common.items.caster_item.staff;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ElementalStaffItemRenderer extends GeoItemRenderer<ElementalStaff> {
    public ElementalStaffItemRenderer() {
        super(new ElementalStaffItemModel());
    }

    @Override
    public void actuallyRender(
        PoseStack poseStack,
        ElementalStaff wandItem,
        BakedGeoModel model,
        @Nullable RenderType renderType,
        MultiBufferSource bufferSource,
        @Nullable VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        super.actuallyRender(
            poseStack,
            wandItem,
            model,
            renderType,
            bufferSource,
            buffer,
            isReRender,
            partialTick,
            packedLight,
            packedOverlay,
            colour
        );
    }
}

