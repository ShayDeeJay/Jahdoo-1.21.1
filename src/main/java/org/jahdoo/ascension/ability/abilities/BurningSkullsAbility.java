package org.jahdoo.ascension.ability.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.entities.burning_skull.BurningSkull;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.ascension.utils.GlobalStrings;
import org.jahdoo.ascension.utils.Helpers;

import java.util.ArrayList;

import static org.jahdoo.ascension.ability.AbilityBuilder.ENTITY_MULTIPLIER;
import static org.jahdoo.common.components.DataComponentHelper.*;

public class BurningSkullsAbility extends AbilityRegistrar {

    public static final ResourceLocation abilityId = Helpers.res("burning_skulls");

    public static void infernoSoundEffect(Player player) {
        player.playSound(SoundReg.HEAL.get(), 1, 0.65F);
        player.playSound(SoundEvents.FIRECHARGE_USE, 1, 0.65F);
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
    public void setModifiers(ItemStack itemStack) {
        new AbilityBuilder(itemStack, abilityId.getPath().intern())
            .setStaticMana(40)
            .setStaticCooldown(300)
            .setDamage(15, 5, 2)
            .setEffectDuration(200, 100, 20)
            .setEffectStrength(4, 0, 1)
            .setEffectChance(25, 0, 5)
            .shotMultiplier(5, 2, 1)
            .setLifetime(50, 30, 5)
            .build();
    }

    @Override
    public void invokeAbility(Player player) {
        var itemInHand = Helpers.getUsedItem(player);
        var projectileCount = getSpecificValue(player, itemInHand, ENTITY_MULTIPLIER);
        var adjustSpread = 1.8 - (projectileCount / 4);
        var getLocalEntities = new ArrayList<>(BurningSkull.getValidTargets(player, player, 10));
        var totalWidth = (projectileCount - 1) * adjustSpread;
        var startOffset = -totalWidth / 2.0;
        infernoSoundEffect(player);

        if(!player.level().isClientSide){
            for (int i = 0; i < projectileCount; i++) {
                var isValid = !getLocalEntities.isEmpty();
                var skull = new BurningSkull(player, 0, isValid ? getLocalEntities.getFirst() : null);
                if (isValid) getLocalEntities.removeFirst();
                var offset = projectileCount == 1 ? 0 : startOffset + i * (totalWidth / (projectileCount - 1));
                var directionOffset = calculateDirectionOffset(player, offset);
                var direction = player.getLookAngle().add(directionOffset).normalize();
                fireProjectileDirection(skull, player, 0.3F, direction);
            }
        }
    }

}
