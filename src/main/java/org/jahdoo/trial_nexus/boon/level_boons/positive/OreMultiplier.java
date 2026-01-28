package org.jahdoo.trial_nexus.boon.level_boons.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.boon.level_boons.AbstractLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.ColourStore;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_ORE_MULTIPLIER;

public class OreMultiplier extends AbstractLevelBoon {

    @Override
    public String id() {
        return KEY_ORE_MULTIPLIER;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.setOreMultiplier((int) value);
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
        return Icons.ORE_MULTIPLIER;
    }

    @Override
    public double value(JahdooRarity rarity) {
        var getRarity = rarity.getAttributes();
        return (int) getRarity.getRandomHealChance();
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.MYTHIC;
    }

    @Override
    public int getStampIndex() {
        return 11;
    }

    @Override
    public int getHeaderColour() {
        return ColourStore.NETHERITE_BOX;
    }
}
