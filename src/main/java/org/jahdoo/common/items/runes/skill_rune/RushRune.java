package org.jahdoo.common.items.runes.skill_rune;

import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.common.registers.mod.SkillReg;
import org.jahdoo.trial_nexus.magic.skills.AbstractSkill;
import org.jahdoo.trial_nexus.magic.skills.RushSkill;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

public class RushRune extends AbstractSkillRune {

    @Override
    DeferredHolder<AbstractSkill, AbstractSkill> skill() {
        return SkillReg.RUSH;
    }

    @Override
    String getName() {
        return RushSkill.RUSH;
    }

    @Override
    public JahdooRarity runeRarity() {
        return JahdooRarity.EPIC;
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomCooldown() / 2;
    }

}
