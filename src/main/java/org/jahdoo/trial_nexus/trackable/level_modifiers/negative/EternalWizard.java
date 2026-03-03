package org.jahdoo.trial_nexus.trackable.level_modifiers.negative;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.trial_nexus.trackable.level_modifiers.MobLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.common.client.Icons;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_ETERNAL_WIZARD;

public class EternalWizard extends MobLevelBoon {

    @Override
    public String id() {
        return KEY_ETERNAL_WIZARD;
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
