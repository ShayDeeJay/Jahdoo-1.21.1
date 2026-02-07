package org.jahdoo.trial_nexus.ability.abilities_combat.permafrost;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.GlobalStrings;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.EntityDataReg;

public class PermafrostAbility extends Ability {

    public static final ResourceLocation abilityId = JahdooHelpers.res("permafrost");

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.LEGENDARY;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public String getDescription() {
        return GlobalStrings.BLOCK_MINER_DESCRIPTION;
    }

    @Override
    public int getCastType() {
        return AREA_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public int levelRequirement() {
        return 40;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementReg.frost();
    }

    @Override
    public void invokeAbility(Player player) {
        var aoeCloud = new AoeCloud(player.level(), player, 0f, EntityDataReg.ARCTIC_STORM.get().setAbilityId(), abilityId.getPath().intern());
        var position = player.position();
        aoeCloud.setPos(position.x, position.y, position.z);
        JahdooHelpers.getSoundWithPositionV(player.level(), aoeCloud.position(), SoundReg.FROST_ABILITY.get(), 1f, 0.8f);
        JahdooHelpers.getSoundWithPositionV(player.level(), aoeCloud.position(), SoundReg.IMPACT.get(),0.8f, 1f);
        JahdooHelpers.getSoundWithPositionV(player.level(), aoeCloud.position(), SoundReg.DASH_EFFECT_INSTANT.get(),1f, 0.8f);
        player.level().addFreshEntity(aoeCloud);
    }

    @Override
    public int getAbilityCost() {
        return 12;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(60)
            .setStaticCooldown(1200)
            .setEffectDuration(500, 100, 100, 1)
            .setEffectStrength(10, 6, 2, 2)
            .setLifetime(400, 200, 50, 1)
            .setAoe(6, 2, 1, 2)
            .buildAndReturn();
    }

}
