package org.jahdoo.trial_nexus.boon.level_boons.negative;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.trial_nexus.boon.level_boons.MobLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.common.client.Icons;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public class EternalWizard extends MobLevelBoon {

    @Override
    public String id() {
        return "eternal_wizard";
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.incrementEternalWizard(value);
    }

    @Override
    public ResourceLocation getIcon() {
        return Icons.ETERNAL_WIZARD;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.LEGENDARY;
    }

    @Override
    public boolean isTrashMob() {
        return false;
    }

}
