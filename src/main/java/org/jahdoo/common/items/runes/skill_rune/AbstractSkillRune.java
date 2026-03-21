package org.jahdoo.common.items.runes.skill_rune;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.common.items.runes.AbstractRune;
import org.jahdoo.common.items.runes.rune_data.RuneCategories;
import org.jahdoo.trial_nexus.magic.skills.AbstractSkill;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.items.runes.rune_data.RuneCategories.SKILL;

public abstract class AbstractSkillRune extends AbstractRune {

    abstract DeferredHolder<AbstractSkill, AbstractSkill> skill();
    abstract String getName();

    @Override
    public String runeId() {
        return getName() + "_rune";
    }

    @Override
    public Holder<Attribute> attributeHolder() {
        return skill().get().attributeModifier();
    }

    @Override
    public RuneCategories runeCategory() {
        return SKILL;
    }

    @Override
    public int runeColour() {
        return color(232, 173, 255);
    }

    @Override
    public DisplayType displayType() {
        return DisplayType.FIXED;
    }
}
