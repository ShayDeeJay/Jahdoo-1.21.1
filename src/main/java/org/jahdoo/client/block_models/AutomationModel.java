package org.jahdoo.client.block_models;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.block.infuser.InfuserBlockEntity;
import org.jahdoo.utils.ModHelpers;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class AutomationModel extends DefaultedBlockGeoModel<AutomationBlockEntity> {
    public AutomationModel() {
        super(ModHelpers.modResourceLocation("automation_block"));
    }

    @Override
    public RenderType getRenderType(AutomationBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
