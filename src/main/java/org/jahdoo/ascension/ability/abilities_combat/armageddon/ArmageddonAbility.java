package org.jahdoo.ascension.ability.abilities_combat.armageddon;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.aoe_cloud.AoeCloud;
import org.jahdoo.common.registers.EntityDataReg;
import org.jahdoo.common.registers.SoundReg;

import static org.jahdoo.ascension.utils.GlobalStrings.BLOCK_MINER_DESCRIPTION;
import static org.jahdoo.common.registers.ElementReg.inferno;

public class ArmageddonAbility extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("armageddon");
    public static final String SPAWNING_SPEED = "Spawning Speed";

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
    public AbstractElement getElemenType() {
        return inferno();
    }

    @Override
    public void invokeAbility(Player player) {
        Vec3 location = player.pick(40, 0,false).getLocation();
        AoeCloud aoeCloud = new AoeCloud(player.level(), player, 3f, EntityDataReg.ARMAGEDDON.get().setAbilityId(), abilityId.getPath().intern());
        aoeCloud.setPos(location.x, location.y, location.z);
        player.level().playSound(null, BlockPos.containing(location), SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 1.4f, 0.3f);
        player.level().playSound(null, BlockPos.containing(location), SoundReg.ORB_FIRE.get(), SoundSource.NEUTRAL, 0.8f, 0.6f);
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
            .setDamage(40, 20, 5, 1, 1.5)
            .setCastingDistance(30, 10, 5, 1, 1.5)
            .setLifetime(400, 200, 40, 1, 1.5)
            .setAoe(6,4,0.5, 1, 1.5)
            .setAbilityTagModifiersRandom(SPAWNING_SPEED, 30,5, false, 5, 1, 1.5)
            .buildAndReturn();
    }

}
