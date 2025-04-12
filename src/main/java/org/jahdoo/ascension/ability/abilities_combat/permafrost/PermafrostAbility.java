package org.jahdoo.ascension.ability.abilities_combat.permafrost;

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
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.EntityDataReg;

public class PermafrostAbility extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("permafrost");

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
        return 30;
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
        Helpers.getSoundWithPositionV(player.level(), aoeCloud.position(), SoundReg.ICE_ATTACH.get(), 1.2f, 0.6f);
        Helpers.getSoundWithPositionV(player.level(), aoeCloud.position(), SoundReg.MAGIC_EXPLOSION.get(), 0.4f, 0.8f);
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
            .setEffectDuration(300, 100, 50, 1)
            .setEffectStrength(10, 5,1, 1)
            .setLifetime(200, 100, 20, 1)
            .setAoe(4, 2, 0.5, 2)
            .buildAndReturn();
    }

}
