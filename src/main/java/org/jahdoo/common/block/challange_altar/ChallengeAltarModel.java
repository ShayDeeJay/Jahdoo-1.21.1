package org.jahdoo.common.block.challange_altar;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.ascension.utils.Helpers;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class ChallengeAltarModel extends DefaultedBlockGeoModel<ChallengeAltarBlockEntity> {
    public ChallengeAltarModel() {
        super(Helpers.res("challenge_altar"));
    }

    @Override
    public RenderType getRenderType(ChallengeAltarBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
