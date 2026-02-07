package org.jahdoo.common.block.dissembler;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class DisassemblerModel extends DefaultedBlockGeoModel<DisassemblerBlockEntity> {
    public DisassemblerModel() {
        super(JahdooHelpers.res("disassembler"));
    }

    @Override
    public RenderType getRenderType(DisassemblerBlockEntity entity, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(entity));
    }
}
