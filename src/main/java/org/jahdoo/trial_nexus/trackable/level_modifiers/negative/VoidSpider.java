package org.jahdoo.trial_nexus.trackable.level_modifiers.negative;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.trial_nexus.trackable.level_modifiers.MobLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.common.client.Icons;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_VOID_SPIDER;

public class VoidSpider extends MobLevelBoon {

    @Override
    public String id() {
        return KEY_VOID_SPIDER;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.incrementVoidSpider(value);
    }

    @Override
    public ResourceLocation getIcon() {
        return Icons.VOID_SPIDER;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.EPIC;
    }

    @Override
    public boolean isTrashMob() {
        return false;
    }
}
