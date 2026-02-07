package org.jahdoo.common.items.armor.battle_mage;

import net.minecraft.resources.ResourceLocation;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BattleMageArmorRenderer extends GeoArmorRenderer<BattleMageArmor> {

    public BattleMageArmorRenderer() {
        super(new DefaultedItemGeoModel<>(JahdooHelpers.res("armor/battlemage_armor")));
    }

    @Override
    public ResourceLocation getTextureLocation(BattleMageArmor animatable) {
        return JahdooHelpers.res("textures/item/armor/mystic_battlemage_armor.png");
    }

}
