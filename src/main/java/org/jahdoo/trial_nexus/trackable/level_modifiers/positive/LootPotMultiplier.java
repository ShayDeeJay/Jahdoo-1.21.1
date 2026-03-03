package org.jahdoo.trial_nexus.trackable.level_modifiers.positive;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.common.client.Icons;
import org.jahdoo.trial_nexus.trackable.level_modifiers.AbstractLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_LOOT_POT_MULTIPLIER;

public class LootPotMultiplier extends AbstractLevelBoon {

    @Override
    public String id() {
        return KEY_LOOT_POT_MULTIPLIER;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.setLootPotMultiplier((int) value);
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
        return Icons.LOOT_POT_ICON;
    }

    @Override
    public double value(JahdooRarity rarity) {
        var getRarity = rarity.getAttributes();
        return (int) getRarity.getRandomHealChance();
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.LEGENDARY;
    }

    @Override
    public int getStampIndex() {
        return 12;
    }

    @Override
    public int getHeaderColour() {
        return ColourHelpers.getRating3Orange();
    }
}
