package org.jahdoo.trial_nexus.trackable.level_modifiers.negative;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.trial_nexus.trackable.level_modifiers.AbstractLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.shaydee.shaydeeapi.helpers.MathHelpers;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_KNOCKBACK;

public class MobKnockback extends AbstractLevelBoon {

    @Override
    public String id() {
        return KEY_KNOCKBACK;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.incrementKnockBack(value);
    }

    @Override
    public boolean isPositive() {
        return false;
    }

    @Override
    public boolean isPercentageOf() {
        return true;
    }

    @Override
    public ResourceLocation getIcon() {
        return Icons.KNOCKBACK_RESISTANCE;
    }

    @Override
    public double value(JahdooRarity rarity) {
        var getRarity = rarity.getAttributes();
        return MathHelpers.doubleFormattedDouble(getRarity.getRandomManaReduction());
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
    }

}
