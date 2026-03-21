package org.jahdoo.common.items.runes.skill_rune;

import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.common.registers.mod.SkillReg;
import org.jahdoo.trial_nexus.magic.skills.AbstractSkill;
import org.jahdoo.trial_nexus.magic.skills.PhantomJumpSkill;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.rarity.RarityAttributes;

public class PhantomJumpRune extends AbstractSkillRune {

    @Override
    DeferredHolder<AbstractSkill, AbstractSkill> skill() {
        return SkillReg.PHANTOM_JUMP;
    }

    @Override
    String getName() {
        return PhantomJumpSkill.PHANTOM_JUMP;
    }

    @Override
    public JahdooRarity runeRarity() {
        return JahdooRarity.EPIC;
    }

    @Override
    public double getAttribute(RarityAttributes rarityAttributes) {
        return Math.round(rarityAttributes.getRandomCooldown() / 2) + 1;
    }

}
