package org.jahdoo.client.block_models;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.JahdooMod;
import org.jahdoo.items.infuser_block_item.InfuserBlockItem;
import org.jahdoo.utils.GeneralHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class InfuserBlockModel extends DefaultedItemGeoModel<InfuserBlockItem> {
    public InfuserBlockModel() {
        super(GeneralHelpers.modResourceLocation("infuser"));
    }
}
