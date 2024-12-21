package org.jahdoo.ability.all_abilities.abilities.raw_abilities;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractAbility;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.ability.all_abilities.abilities.StormRushAbility;
import org.jahdoo.attachments.player_abilities.BouncyFoot;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.registers.SoundRegister;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.ability.AbilityBuilder.DAMAGE;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.spawnElectrifiedParticles;
import static org.jahdoo.registers.AttributesRegister.LIGHTNING_MAGIC_DAMAGE_MULTIPLIER;
import static org.jahdoo.registers.AttributesRegister.MAGIC_DAMAGE_MULTIPLIER;

public class StormRush extends AbstractAbility {

    Player player;
    WandAbilityHolder wandAbilityHolder;

    public StormRush(Player player){
        this.player = player;
        this.wandAbilityHolder = player.getMainHandItem().get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
    }

    public void launchPlayerDirection() {
        var launchDistances = getTag(StormRushAbility.launchDistance);
        var damage = getTag(DAMAGE);
        var damageModified = ModHelpers.attributeModifierCalculator(player, (float) damage, true, MAGIC_DAMAGE_MULTIPLIER, LIGHTNING_MAGIC_DAMAGE_MULTIPLIER);
        var particleOptions = genericParticleOptions(ParticleStore.ELECTRIC_PARTICLE_SELECTION, this.getType(), ModHelpers.Random.nextInt(10,18), 1f, 0.3);
        if(player instanceof ServerPlayer serverPlayer) serverPlayer.getAbilities().mayfly = true;

        player.playSound(SoundRegister.DASH_EFFECT_INSTANT.get(),0.5f,1);
        player.playSound(SoundRegister.BOLT.get(), 0.5f,0.8f);
        player.startAutoSpinAttack(10, damageModified, player.getMainHandItem());

        if(player.level().isClientSide){
            Vec3 lookVector = player.getLookAngle().scale(launchDistances);
            player.setDeltaMovement(lookVector);
        }

        BouncyFoot.setBouncyFoot(player, 320);

        spawnElectrifiedParticles(player.level(), player.position(), particleOptions, 10, player,0.08);
        spawnElectrifiedParticles(player.level(), player.position(), this.getType().getParticleGroup().magic(), 30, player, 0.08);

        BouncyFoot.setBouncyFoot(player, 320);
    }

    AbstractElement getType(){
        return ElementRegistry.LIGHTNING.get();
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
