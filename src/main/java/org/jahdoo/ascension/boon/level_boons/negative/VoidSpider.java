package org.jahdoo.ascension.boon.level_boons.negative;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.ascension.boon.level_boons.MobLevelBoon;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.client.Icons;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public class VoidSpider extends MobLevelBoon {

    @Override
    public String id() {
        return "void_spider";
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

}
