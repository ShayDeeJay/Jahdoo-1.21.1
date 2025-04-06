package org.jahdoo.ascension.ability.abilities_combat.eternal_wizard;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.registers.ElementReg;

import static net.minecraft.core.BlockPos.containing;
import static net.minecraft.sounds.SoundEvents.ELDER_GUARDIAN_DEATH;
import static org.jahdoo.ascension.utils.Helpers.getSoundWithPosition;
import static org.jahdoo.ascension.utils.Helpers.res;
import static org.jahdoo.common.registers.EntityDataReg.SUMMON_ETERNAL_WIZARD;
import static org.jahdoo.common.registers.SoundReg.EXPLOSION;

public class SummonEternalWizardAbility extends Ability {

    public static final ResourceLocation abilityId = res("eternal_wizard");

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.EPIC;
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
    public void invokeAbility(Player player) {
        var location = player.pick(40, 0,false).getLocation();
        var aoeCloud = new AoeCloud(player.level(), player, 0f, SUMMON_ETERNAL_WIZARD.get().setAbilityId(), abilityId.getPath().intern());

        aoeCloud.setPos(location.x, location.y, location.z);
        getSoundWithPosition(player.level(), containing(location), ELDER_GUARDIAN_DEATH, 2F, 1.4F);
        getSoundWithPosition(player.level(), containing(location), EXPLOSION.get(), 2F, 1.2F);
        player.level().addFreshEntity(aoeCloud);
    }

    @Override
    public int getAbilityCost() {
        return 8;
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(100)
            .setStaticCooldown(6000)
            .setDamage(40, 15, 5, 1, 1.5)
            .setEffectStrength(10, 0, 1, 1, 1.5)
            .setEffectDuration(600, 200, 50, 1, 1.5)
            .setEffectChance(60, 20, 10, 1, 1.5)
            .setCastingDistance(30, 10, 5, 1, 1.5)
            .setLifetime(12000, 2400, 1200, 1, 1.5)
            .buildAndReturn();
    }

}
