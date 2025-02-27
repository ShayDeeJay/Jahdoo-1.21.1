package org.jahdoo.ascension.ability.abilities.storm_rush;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.ability.AbstractAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.attachments.player_abilities.BouncyFoot;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.ElementRegistry;
import org.jahdoo.common.registers.SoundRegister;
import org.jahdoo.ascension.utils.Helpers;

import static org.jahdoo.ascension.ability.AbilityBuilder.DAMAGE;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.common.particle.ParticleHandlers.spawnElectrifiedParticles;
import static org.jahdoo.common.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.ascension.utils.Helpers.*;

public class StormRush extends AbstractAbility {

    private final Player player;
    private final WandAbilityHolder wandAbilityHolder;

    public StormRush(Player player){
        this.player = player;
        this.wandAbilityHolder = WandAbilityHolder.getHolderFromWand(player);
    }

    public void launchPlayerDirection() {
        var launchDistances = getTag(StormRushAbility.launchDistance);
        var damage = getTag(DAMAGE);
        var damageModified = attributeModifierCalculator(player, (float) damage, true, MAGIC_DAMAGE_MULTIPLIER, getType().getDamageTypeAmplifier());
        var particleOptions = genericParticleOptions(ParticleStore.ELECTRIC_PARTICLE_SELECTION, this.getType(), Random.nextInt(10,18), 1f, 0.3);
        var itemInHand = Helpers.getUsedItem(player);

        if(player instanceof ServerPlayer serverPlayer) serverPlayer.getAbilities().mayfly = true;
        player.playSound(SoundRegister.DASH_EFFECT_INSTANT.get(),0.5f,1.5F);
        player.playSound(SoundRegister.ICE_ATTACH.get(), 0.5f,0.8f);
        player.startAutoSpinAttack(10, damageModified, itemInHand);

        if(player.level().isClientSide){
            var lookVector = player.getLookAngle().scale(launchDistances);
            player.setDeltaMovement(lookVector);
        }

        BouncyFoot.setBouncyFoot(player, 320);
        spawnElectrifiedParticles(player.level(), player.position(), particleOptions, 10, player,0.08);
        spawnElectrifiedParticles(player.level(), player.position(), this.getType().getParticleGroup().magic(), 30, player, 0.08);
        BouncyFoot.setBouncyFoot(player, 320);
    }

    AbstractElement getType(){
        return ElementRegistry.FROST.get();
    }

    @Override
    public WandAbilityHolder getWandAbilityHolder() {
        return wandAbilityHolder;
    }

    @Override
    public String abilityId() {
        return StormRushAbility.abilityId.getPath().intern();
    }
}
