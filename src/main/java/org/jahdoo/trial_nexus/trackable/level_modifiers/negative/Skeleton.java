package org.jahdoo.trial_nexus.trackable.level_modifiers.negative;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.trial_nexus.trackable.level_modifiers.MobLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.common.client.Icons;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_SKELETON;

public class Skeleton extends MobLevelBoon {

    @Override
    public String id() {
        return KEY_SKELETON;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.incrementSkeleton(value);
    }

    @Override
    public ResourceLocation getIcon() {
        return Icons.SKELETON;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

    @Override
    public boolean isTrashMob() {
        return true;
    }
}
