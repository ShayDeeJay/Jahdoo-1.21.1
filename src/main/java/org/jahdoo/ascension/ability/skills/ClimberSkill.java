package org.jahdoo.ascension.ability.skills;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jahdoo.common.client.Icons;

import java.util.List;

import static org.jahdoo.ascension.ability.Ability.NON;

public class ClimberSkill extends AbstractSkill {

    @Override
    String name() {
        return "Climber";
    }

    @Override
    ResourceLocation icon() {
        return Icons.CLIMBER;
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
        return 3;
    }

    @Override
    String dependency() {
        return NON;
    }

    @Override
    int levelRequirement() {
        return 0;
    }

}