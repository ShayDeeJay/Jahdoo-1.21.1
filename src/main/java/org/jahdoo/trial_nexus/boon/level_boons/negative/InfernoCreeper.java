package org.jahdoo.trial_nexus.boon.level_boons.negative;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.trial_nexus.boon.level_boons.MobLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.common.client.Icons;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_INFERNO_CREEPER;

public class InfernoCreeper extends MobLevelBoon {

    @Override
    public String id() {
        return KEY_INFERNO_CREEPER;
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
        return JahdooRarity.MYTHIC;
    }

    @Override
    public boolean isTrashMob() {
        return false;
    }
}
