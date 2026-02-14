package org.jahdoo.trial_nexus.boon.level_boons.negative;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.trial_nexus.boon.level_boons.AbstractLevelBoon;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.shaydee.shaydeeapi.helpers.MathHelpers;

import static net.minecraft.world.effect.MobEffects.DAMAGE_BOOST;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.attachments.InstanceData.KEY_ATTACK_DAMAGE;
import static org.jahdoo.trial_nexus.boon.player_boons.BoonSelection.iconFromEffect;

public class MobDamage extends AbstractLevelBoon {

    @Override
    public String id() {
        return KEY_ATTACK_DAMAGE;
    }

    @Override
    public void execute(ServerLevel level, double value) {
        var data = level.getData(INSTANCE_DATA);
        data.incrementAttackDamage(value);
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
        return iconFromEffect(DAMAGE_BOOST);
    }

    @Override
    public double value(JahdooRarity rarity) {
        var getRarity = rarity.getAttributes();
        return MathHelpers.doubleFormattedDouble(getRarity.getRandomMaxHealth());
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.COMMON;
    }

}
