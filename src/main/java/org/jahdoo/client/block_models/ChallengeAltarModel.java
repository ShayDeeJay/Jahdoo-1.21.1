package org.jahdoo.client.block_models;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.block.challange_altar.ChallengeAltarBlockEntity;
import org.jahdoo.block.infuser.InfuserBlockEntity;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class ChallengeAltarModel extends DefaultedBlockGeoModel<ChallengeAltarBlockEntity> {
    public ChallengeAltarModel() {
        super(ModHelpers.res("challenge_altar"));
    }

    @Override
    public RenderType getRenderType(ChallengeAltarBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
