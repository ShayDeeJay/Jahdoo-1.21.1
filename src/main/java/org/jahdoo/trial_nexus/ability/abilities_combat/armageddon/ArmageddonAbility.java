package org.jahdoo.trial_nexus.ability.abilities_combat.armageddon;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.trial_nexus.ability.Ability;
import org.jahdoo.trial_nexus.ability.AbilityBuilder;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.EntityDataReg;

import static org.jahdoo.trial_nexus.utils.GlobalStrings.BLOCK_MINER_DESCRIPTION;
import static org.jahdoo.common.registers.mod.ElementReg.inferno;

public class ArmageddonAbility extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("armageddon");
    public static final String SPAWNING_SPEED = "Spawning Speed";

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.MYTHIC;
    }

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public String getDescription() {
        return BLOCK_MINER_DESCRIPTION;
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
    public int levelRequirement() {
        return 80;
    }

    @Override
    public AbstractElement getElemenType() {
        return inferno();
    }

    @Override
    public void invokeAbility(Player player) {
        Vec3 location = player.pick(40, 0,false).getLocation();
        AoeCloud aoeCloud = new AoeCloud(player.level(), player, 3f, EntityDataReg.ARMAGEDDON.get().setAbilityId(), abilityId.getPath().intern());
        aoeCloud.setPos(location.x, location.y, location.z);
        player.level().playSound(null, BlockPos.containing(location), SoundReg.FIRE_ABILITY.get(), SoundSource.NEUTRAL, 1.4f, 1);
        player.level().playSound(null, BlockPos.containing(location), SoundReg.TELEPORT.get(), SoundSource.NEUTRAL, 1f, 1f);
        player.level().addFreshEntity(aoeCloud);
    }

    @Override
    public int getAbilityCost() {
        return 16;
    }

    @Override
    public AbilityHolder setModifiers( ) {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(150)
            .setStaticCooldown(4800)
            .setDamage(60, 30, 10, 2)
            .setCastingDistance(30, 10, 10, 2)
            .setLifetime(400, 200, 100, 3)
            .setAoe(6, 4, 1, 3)
            .setAbilityTagModifiersRandom(SPAWNING_SPEED, 20, 5, false, 5, 2)
            .buildAndReturn();
    }

}
