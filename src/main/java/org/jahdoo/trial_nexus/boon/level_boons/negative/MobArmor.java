package org.jahdoo.trial_nexus.boon.level_boons.negative;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.trial_nexus.boon.level_boons.AbstractLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;

import static net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_ARMOR;
import static org.jahdoo.trial_nexus.boon.player_boons.BoonSelection.iconFromEffect;

public class MobArmor extends AbstractLevelBoon {

    @Override
    public String id() {
        return KEY_ARMOR;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.incrementArmor(value);
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
        return iconFromEffect(DAMAGE_RESISTANCE);
    }

    @Override
    public double value(JahdooRarity rarity) {
        var getRarity = rarity.getAttributes();
        return org.shaydee.shaydeeapi.Maths.doubleFormattedDouble(getRarity.getRandomManaRegen());
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.COMMON;
    }

}
