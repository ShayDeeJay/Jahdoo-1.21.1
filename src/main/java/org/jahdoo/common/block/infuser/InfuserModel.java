package org.jahdoo.common.block.infuser;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.ascension.utils.Helpers;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class InfuserModel extends DefaultedBlockGeoModel<InfuserBlockEntity> {
    public InfuserModel() {
        super(Helpers.res("infuser"));
    }

    @Override
    public RenderType getRenderType(InfuserBlockEntity entity, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(entity));
    }
}
