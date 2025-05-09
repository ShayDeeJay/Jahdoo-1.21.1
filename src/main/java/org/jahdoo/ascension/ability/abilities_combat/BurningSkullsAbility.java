package org.jahdoo.ascension.ability.abilities_combat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.Ability;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.attachments.CasterData;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.entities.burning_skull.BurningSkull;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;

import java.util.ArrayList;

import static org.jahdoo.ascension.ability.AbilityBuilder.SHOT_MULTIPLIER;

public class BurningSkullsAbility extends Ability {

    public static final ResourceLocation abilityId = Helpers.res("burning_skulls");

    public static void vitalitySoundEffect(Player player) {
        if(player instanceof ServerPlayer serverPlayer){
            Helpers.sendClientSound(serverPlayer, SoundReg.ELEMENTAL_BULLET.get(), 0.6F, 1F);
            Helpers.sendClientSound(serverPlayer, SoundReg.VITALITY_ABILITY.get(), 2, 1.2F);
        }
    }

    public static void mysticSoundEffect(Player player) {
        if(player instanceof ServerPlayer serverPlayer){
            Helpers.sendClientSound(serverPlayer, SoundReg.ELEMENTAL_BULLET.get(), 0.6F, 1F);
            Helpers.sendClientSound(serverPlayer, SoundReg.MYSTIC_ABILITY.get(), 2, 1.2F);
        }
    }

    public static void frostSoundEffect(Player player) {
        if(player instanceof ServerPlayer serverPlayer){
            Helpers.sendClientSound(serverPlayer, SoundReg.ELEMENTAL_BULLET.get(), 0.6F, 1F);
            Helpers.sendClientSound(serverPlayer, SoundReg.FROST_ABILITY.get(), 1F, 1.2F);
        }
    }

    public static void infernoSoundEffect(Player player) {
        if(player instanceof ServerPlayer serverPlayer){
            Helpers.sendClientSound(serverPlayer, SoundReg.ELEMENTAL_BULLET.get(), 0.6F, 1F);
            Helpers.sendClientSound(serverPlayer, SoundReg.FIRE_ABILITY.get(), 2, 1.2F);
        }
    }

    @Override
    public JahdooRarity rarity() {
        return JahdooRarity.RARE;
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
        return PROJECTILE_CAST;
    }

    @Override
    public int getCastDuration(Player player) {
        return 0;
    }

    @Override
    public AbstractElement getElemenType() {
        return ElementReg.inferno();
    }

    @Override
    public AbilityHolder setModifiers() {
        return new AbilityBuilder(abilityId.getPath().intern())
            .setStaticMana(40)
            .setStaticCooldown(300)
            .setDamage(32, 25, 2, 1)
            .setEffectDuration(200, 100, 20, 1)
            .setEffectStrength(4, 0, 1, 1)
            .setEffectChance(25, 0, 5, 1)
            .shotMultiplier(10, 2, 2, 1)
            .setLifetime(100, 40, 20, 1)
            .buildAndReturn();
    }

    @Override
    public void invokeAbility(Player player) {
        var projectileCount = CasterData.getSpecificValue(player, SHOT_MULTIPLIER);
        var adjustSpread = 1.8 - (projectileCount / 4);
        var getLocalEntities = new ArrayList<>(BurningSkull.getValidTargets(player, player, 50));
        var totalWidth = (projectileCount - 1) * adjustSpread;
        var startOffset = -totalWidth / 2.0;
        infernoSoundEffect(player);

        if(!player.level().isClientSide){
            for (int i = 0; i < projectileCount; i++) {
                var isValid = !getLocalEntities.isEmpty();
                var randomTarget = Helpers.listRandom(getLocalEntities);
                var skull = new BurningSkull(player, 0, isValid ? randomTarget : null);
                if (isValid) getLocalEntities.remove(randomTarget);
                var offset = projectileCount == 1 ? 0 : startOffset + i * (totalWidth / (projectileCount - 1));
                var directionOffset = calculateDirectionOffset(player, offset);
                var direction = player.getLookAngle().add(directionOffset).normalize();
                fireProjectileDirection(skull, player, 0.3F, direction);
            }
        }
    }

    @Override
    public int levelRequirement() {
        return 10;
    }

    @Override
    public int getAbilityCost() {
        return 5;
    }

}
