package org.jahdoo.common.items.runes.skill_rune;

import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.common.registers.mod.SkillReg;
import org.jahdoo.trial_nexus.magic.skills.AbstractSkill;
import org.jahdoo.trial_nexus.magic.skills.ClimberSkill;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

public class ClimberRune extends AbstractSkillRune {

    @Override
    DeferredHolder<AbstractSkill, AbstractSkill> skill() {
        return SkillReg.CLIMBER;
    }

    @Override
    String getName() {
        return ClimberSkill.CLIMBER;
    }

    @Override
    public JahdooRarity runeRarity() {
        return JahdooRarity.COMMON;
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return Math.round(rarityAttributes.getRandomHealChance());
    }

}
