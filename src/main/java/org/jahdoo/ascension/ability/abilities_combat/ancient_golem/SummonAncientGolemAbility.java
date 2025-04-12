package org.jahdoo.ascension.ability.abilities_combat.ancient_golem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.registers.mod.ElementReg;

import static net.minecraft.core.BlockPos.containing;
import static net.minecraft.sounds.SoundEvents.ELDER_GUARDIAN_DEATH;
import static org.jahdoo.common.registers.SoundReg.EXPLOSION;
import static org.jahdoo.common.registers.mod.EntityDataReg.SUMMON_ANCIENT_GOLEM;

public class SummonAncientGolemAbility extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("ancient_golem");

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.ETERNAL;
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
        return DISTANCE_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementReg.vitality();
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(200)
            .setStaticCooldown(6000)
            .setDamage(40, 10, 5, 2)
            .setEffectStrength(10, 0, 1, 1)
            .setEffectDuration(600, 200, 50, 1)
            .setEffectChance(60, 20, 10, 1)
            .setCastingDistance(30, 10, 5, 2)
            .setLifetime(12000, 2400, 1200, 1)
            .buildAndReturn();
    }

    @Override
    public void invokeAbility(Player player) {
        var location = player.pick(40, 0, false).getLocation();
        var selectedAbility = SUMMON_ANCIENT_GOLEM.get().setAbilityId();
        var intern = abilityId.getPath().intern();
        var aoeCloud = new AoeCloud(player.level(), player, 0f, selectedAbility, intern);

        aoeCloud.setPos(location.x, location.y, location.z);
        player.level().addFreshEntity(aoeCloud);
        Helpers.getSoundWithPosition(player.level(), containing(location), ELDER_GUARDIAN_DEATH, 2F, 1.4F);
        Helpers.getSoundWithPosition(player.level(), containing(location), EXPLOSION.get(), 2F, 1.2F);
    }

    @Override
    public int levelRequirement() {
        return 50;
    }

    @Override
    public int getAbilityCost() {
        return 16;
    }

}
