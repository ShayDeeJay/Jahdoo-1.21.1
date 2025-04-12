package org.jahdoo.ascension.ability.skills;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.EffectReg;

public class TripleJumpSkill extends AbstractSkill {
    public static final String TRIPLE_JUMP = "triple_jump";

    @Override
    public String id() {
        return TRIPLE_JUMP;
    }

    @Override
    public ResourceLocation icon() {
        return Icons.TRIPLE_JUMP;
    }

    @Override
    public Holder<MobEffect> skillEffect() {
        return EffectReg.TRIPLE_JUMP;
    }

    @Override
    public int unlockCost() {
        return 10;
    }

    @Override
    public int levelRequirement() {
        return 30;
    }

    @Override
    public String description() {
        return "By harnessing ethereal power, you can summon brief platforms beneath your feet, allowing you to jump up to three times in midair.";
    }

}