package org.jahdoo.client.block_models;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.block.infuser.InfuserBlockEntity;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class InfuserModel extends DefaultedBlockGeoModel<InfuserBlockEntity> {
    public InfuserModel() {
        super(ModHelpers.res("infuser"));
    }

    @Override
    public RenderType getRenderType(InfuserBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
