package org.jahdoo.trial_nexus.trackable.level_modifiers.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.trial_nexus.trackable.level_modifiers.AbstractLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_COMMON_LOOT_MULTIPLIER;
import static org.jahdoo.trial_nexus.rarity.JahdooRarity.COMMON;

public class CommonLootMultiplier extends AbstractLevelBoon {

    @Override
    public String id() {
        return KEY_COMMON_LOOT_MULTIPLIER;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.setCommonLootMultiplier((int) value);
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public boolean isPercentageOf() {
        return false;
    }

    @Override
    public ResourceLocation getIcon() {
        return Icons.CHEST_COMMON;
    }

    @Override
    public double value(JahdooRarity rarity) {
        return 1;
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

    @Override
    public int getStampIndex() {
        return 3;
    }


    @Override
    public int getHeaderColour() {
        return COMMON.getColour();
    }

}
