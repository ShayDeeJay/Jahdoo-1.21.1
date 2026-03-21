package org.jahdoo.common.items.runes.skill_rune;

import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.common.registers.mod.SkillReg;
import org.jahdoo.trial_nexus.magic.skills.AbstractSkill;
import org.jahdoo.trial_nexus.magic.skills.SwiftSkill;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

public class SwiftRune extends AbstractSkillRune {

    @Override
    DeferredHolder<AbstractSkill, AbstractSkill> skill() {
        return SkillReg.SWIFT;
    }

    @Override
    String getName() {
        return SwiftSkill.SWIFT;
    }

    @Override
    public JahdooRarity runeRarity() {
        return JahdooRarity.COMMON;
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return rarityAttributes.getRandomManaRegen();
    }

    @Override
    public DisplayType displayType() {
        return DisplayType.PERCENT;
    }
}
