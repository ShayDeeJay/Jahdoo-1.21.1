package org.jahdoo.ascension.ability.abilities.storm_rush;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.AbstractAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.attachments.player_abilities.BouncyFoot;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.ascension.utils.Helpers;

import static org.jahdoo.ascension.ability.AbilityBuilder.DAMAGE;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.particle.ParticleHandlers.spawnElectrifiedParticles;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.common.registers.SoundReg.*;

public class StormRush extends AbstractAbility {

    private final Player player;
    private final WandAbilityHolder wandAbilityHolder;

    public StormRush(Player player){
        this.player = player;
        this.wandAbilityHolder = WandAbilityHolder.getHolderFromWand(player);
    }

    AbstractElement getType(){
        return ElementReg.frost();
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return wandAbilityHolder;
    }

    @Override
    public String abilityId() {
        return StormRushAbility.abilityId.getPath().intern();
    }

    public void launchPlayerDirection() {
        var launchDistances = getTag(StormRushAbility.launchDistance);
        var damage = getTag(DAMAGE);
        var damageModified = attributeModifierCalculator(player, (float) damage, true, MAGIC_DAMAGE_MULTIPLIER, getType().damageAmplifier());
        var particleOptions = ParticleHandlers.genericParticle(ParticleStore.ELECTRIC_PARTICLE, this.getType(), Random.nextInt(10,18), 1f, 0.3);
        var itemInHand = Helpers.getUsedItem(player);
        var level = player.level();
        var pos = player.position();

        if(player instanceof ServerPlayer serverPlayer) serverPlayer.getAbilities().mayfly = true;
        player.playSound(DASH_EFFECT_INSTANT.get(),0.5f,1.5F);
        player.playSound(ICE_ATTACH.get(), 0.5f,0.8f);
//        player.startAutoSpinAttack(10, damageModified, itemInHand);

        if(level.isClientSide){
            var lookVector = player.getLookAngle().scale(launchDistances);
            player.setDeltaMovement(lookVector);
        }

        BouncyFoot.setBouncyFoot(player, 320);
        spawnElectrifiedParticles(level, pos, particleOptions, 10, player, 0.08);
        spawnElectrifiedParticles(level, pos, this.getType().getParticleGroup().magic(), 30, player, 0.08);
        BouncyFoot.setBouncyFoot(player, 320);
    }

}
