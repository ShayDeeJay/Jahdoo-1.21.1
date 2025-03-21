package org.jahdoo.ascension.boon.level_boons.negative;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.ascension.boon.level_boons.MobLevelBoon;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.client.Icons;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public class Skeleton extends MobLevelBoon {

    @Override
    public String id() {
        return "skeleton";
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

}
