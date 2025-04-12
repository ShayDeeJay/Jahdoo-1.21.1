package org.jahdoo.ascension.ability.skills;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.EffectReg;

public class MageFlightSkill extends AbstractSkill {

    public static final String MAGE_FLIGHT = "mage_flight";

    @Override
    public String id() {
        return MAGE_FLIGHT;
    }

    @Override
    public ResourceLocation icon() {
        return Icons.MAGE_FLIGHT;
    }

    @Override
    public Holder<MobEffect> skillEffect() {
        return EffectReg.MAGE_FLIGHT;
    }

    @Override
    public int unlockCost() {
        return 20;
    }

    @Override
    public int levelRequirement() {
        return 50;
    }

    @Override
    public String description() {
        return "Tap into divine energy to defy gravity itself. You can now levitate freely, gliding through the air at the cost of mana.";
    }

}