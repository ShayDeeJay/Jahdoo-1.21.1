package org.jahdoo.ascension.ability.abilities_combat.storm_rush;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ascension.ability.AbstractAbility;
import org.jahdoo.ascension.attachments.CasterData;
import org.jahdoo.ascension.attachments.player_abilities.Rebound;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.networking.server2client.MoveClientEntityS2CP;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.mod.ElementReg;

import static org.jahdoo.ascension.ability.AbilityBuilder.DAMAGE;
import static org.jahdoo.ascension.utils.Helpers.Random;
import static org.jahdoo.ascension.utils.Helpers.attributeModifierCalculator;
import static org.jahdoo.common.particle.ParticleHandlers.spawnElectrifiedParticles;
import static org.jahdoo.common.registers.AttributeReg.MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.common.registers.SoundReg.DASH_EFFECT_INSTANT;

public class StormRush extends AbstractAbility {

    private final Player player;
    private final AbilityHolder abilityHolder;

    public StormRush(Player player){
        this.player = player;
        this.abilityHolder = CasterData.entityHolderWithSelected(player);
    }

    AbstractElement getType(){
        return ElementReg.frost();
    }

    @Override
    public AbilityHolder getAbilityHolder() {
        return abilityHolder;
    }

    @Override
    public String abilityId() {
        return StormRushAbility.abilityId.getPath().intern();
    }

    public void launchPlayerDirection() {
        var damage = getTag(DAMAGE);
        var damageModified = attributeModifierCalculator(player, (float) damage, true, MAGIC_DAMAGE_MULTIPLIER, getType().damageAmplifier());
        var particleOptions = ParticleHandlers.genericParticle(ParticleStore.ELECTRIC_PARTICLE, this.getType(), Random.nextInt(10,18), 1f, 0.3);
        var level = player.level();
        var pos = player.position();

        if(player instanceof ServerPlayer serverPlayer) {
            serverPlayer.getAbilities().mayfly = true;
            var launchDistances = getTag(StormRushAbility.launchDistance);
            var lookVector = serverPlayer.getLookAngle().scale(launchDistances);
            PacketDistributor.sendToPlayer(serverPlayer, new MoveClientEntityS2CP(lookVector.x, lookVector.y, lookVector.z, serverPlayer.getId()));
            Helpers.getSoundWithPosition(level, serverPlayer.blockPosition(), DASH_EFFECT_INSTANT.get(), 2f);
        }

        player.startAutoSpinAttack(10, damageModified, ItemStack.EMPTY);

        Rebound.setBouncyFoot(player, 320);
        spawnElectrifiedParticles(level, pos, particleOptions, 10, player, 0.08);
        spawnElectrifiedParticles(level, pos, this.getType().getParticleGroup().magic(), 30, player, 0.08);
        Rebound.setBouncyFoot(player, 320);
    }

}
