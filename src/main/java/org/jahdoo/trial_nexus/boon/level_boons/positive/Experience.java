package org.jahdoo.trial_nexus.boon.level_boons.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.jahdoo.trial_nexus.boon.level_boons.AbstractLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_EXPERIENCE;

public class Experience extends AbstractLevelBoon {

    @Override
    public String id() {
        return KEY_EXPERIENCE;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        for (var player : level.players()) {
            RunData.addExperienceToTotal((int) value, player);
        }
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
        return Icons.TRIAL_EXPERIENCE;
    }

    @Override
    public int getStampIndex() {
        return 10;
    }

    @Override
    public double value(JahdooRarity rarity) {
        var getRarity = rarity.getAttributes();
        return (int) getRarity.getRandomManaPool();
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.EPIC;
    }

    @Override
    public int getHeaderColour() {
        return ColourHelpers.getCosmicPurple();
    }
}
