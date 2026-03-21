package org.jahdoo.common.items.runes.skill_rune;

import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.common.registers.mod.SkillReg;
import org.jahdoo.trial_nexus.magic.skills.AbstractSkill;
import org.jahdoo.trial_nexus.magic.skills.MageFlightSkill;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

public class MageFlightRune extends AbstractSkillRune {

    @Override
    DeferredHolder<AbstractSkill, AbstractSkill> skill() {
        return SkillReg.MAGE_FLIGHT;
    }

    @Override
    String getName() {
        return MageFlightSkill.MAGE_FLIGHT;
    }

    @Override
    public JahdooRarity runeRarity() {
        return JahdooRarity.MYTHIC;
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomCooldown();
    }

}
