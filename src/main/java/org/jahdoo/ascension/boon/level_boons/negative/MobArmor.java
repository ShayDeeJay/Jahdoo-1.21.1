package org.jahdoo.ascension.boon.level_boons.negative;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.ascension.boon.level_boons.AbstractLevelBoon;
import org.jahdoo.ascension.rarity.JahdooRarity;

import static net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE;
import static org.jahdoo.ascension.boon.player_boons.BoonSelection.iconFromEffect;
import static org.jahdoo.ascension.rarity.JahdooRarity.getRarity;
import static org.jahdoo.ascension.utils.Maths.doubleFormattedDouble;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public class MobArmor extends AbstractLevelBoon {

    @Override
    public String id() {
        return "mob_armor";
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
    public double value() {
        var getRarity = getRarity().getAttributes();
        return doubleFormattedDouble(getRarity.getRandomManaRegen());
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.COMMON;
    }

}
