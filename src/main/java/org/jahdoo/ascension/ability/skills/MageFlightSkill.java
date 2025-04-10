package org.jahdoo.ascension.ability.skills;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;

import java.util.List;

public class MageFlightSkill extends AbstractSkill {

    @Override
    String name() {
        return "Mage Flight";
    }

    @Override
    ResourceLocation icon() {
        return Icons.MAGE_FLIGHT;
    }

    @Override
    void doOnCall() {
        //TODO
    }

    @Override
    List<Component> tooltip() {
        return List.of();
    }

    @Override
    int unlockCost() {
        return 20;
    }

    @Override
    String dependency() {
        return "Triple Jump";
    }

    @Override
    int levelRequirement() {
        return 30;
    }

}