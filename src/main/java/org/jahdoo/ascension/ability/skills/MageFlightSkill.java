package org.jahdoo.ascension.ability.skills;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import org.jahdoo.common.client.Icons;
import org.jahdoo.common.registers.EffectReg;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.ascension.ability.skills.TripleJumpSkill.TRIPLE_JUMP;

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
        //TODO
    }

    @Override
    public List<Component> tooltip() {
        var components = new ArrayList<Component>();

//        components.add(Helpers.withStyleComponent(Helpers.stringIdToName(id()), ColourStore.HEADER_COLOUR));
//        components.add(Helpers.withStyleComponent(description(), ColourStore.AETHER_BLUE));

        return components;
    }

    @Override
    public int unlockCost() {
        return 20;
    }

    @Override
    public String dependency() {
        return TRIPLE_JUMP;
    }

    @Override
    public int levelRequirement() {
        return 30;
    }

    @Override
    public String description() {
        return "Tap into divine energy to defy gravity itself. You can now levitate freely, gliding through the air at the cost of mana.";
    }

}