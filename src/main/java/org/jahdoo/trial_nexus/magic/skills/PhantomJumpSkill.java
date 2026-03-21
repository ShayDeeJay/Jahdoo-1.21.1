package org.jahdoo.trial_nexus.magic.skills;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.trial_nexus.utils.Icons;

public class PhantomJumpSkill extends AbstractSkill {
    public static final String PHANTOM_JUMP = "phantom_jump";

    @Override
    public String id() {
        return PHANTOM_JUMP;
    }

    @Override
    public ResourceLocation icon() {
        return Icons.PHANTOM_JUMP;
    }

    @Override
    public int unlockCost() {
        return 14;
    }

    @Override
    public int levelRequirement() {
        return 45;
    }

    @Override
    public String description() {
        return "By harnessing ethereal power, you can summon brief platforms beneath your feet, allowing you to jump up to three times in midair.";
    }

    @Override
    public Holder<Attribute> attributeModifier() {
        return AttributeReg.PHANTOM_JUMP;
    }
}