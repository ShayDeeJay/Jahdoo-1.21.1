package org.jahdoo.ascension.boon.level_boons.negative;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.ascension.boon.level_boons.MobLevelBoon;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.client.Icons;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public class InfernoCreeper extends MobLevelBoon {

    @Override
    public String id() {
        return "inferno_creeper";
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.incrementInfernoCreeper(value);
    }

    @Override
    public ResourceLocation getIcon() {
        return Icons.INFERNO_CREEPER;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.ETERNAL;
    }

    @Override
    public boolean isTrashMob() {
        return false;
    }
}
