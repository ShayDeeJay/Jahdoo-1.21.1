package org.jahdoo.common.block.altar;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.ascension.utils.Helpers;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class AltarModel extends DefaultedBlockGeoModel<AltarBlockEntity> {

    public AltarModel() {
        super(Helpers.res("challenge_altar"));
    }

    @Override
    public RenderType getRenderType(AltarBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

}
