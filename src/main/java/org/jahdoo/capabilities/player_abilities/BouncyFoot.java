package org.jahdoo.capabilities.player_abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.capabilities.AbstractAttachment;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.items.wand.CastHelper;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.particle.particle_options.BakedParticleOptions;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.DataComponentHelper;
import org.jahdoo.utils.GeneralHelpers;

import java.util.List;

import static org.jahdoo.all_magic.AbilityBuilder.COOLDOWN;
import static org.jahdoo.all_magic.AbilityBuilder.MANA_COST;
import static org.jahdoo.all_magic.all_abilities.abilities.DimensionalRecallAbility.abilityId;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.registers.AttachmentRegister.*;
import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;

public class BouncyFoot implements AbstractAttachment {

    private double currentDelta;
    private double previousDelta;
    private int effectTimer;

    public void saveNBTData(CompoundTag nbt) {
    }

    public void loadNBTData(CompoundTag nbt) {
    }

    public static void setBouncyFoot(Player player, int effectTimer){
        player.getData(BOUNCY_FOOT).setEffectTimer(effectTimer);
    }

    public static void staticTickEvent(Player player){
        player.getData(BOUNCY_FOOT).onTick(player);
    }

    public void onTick(Player player){
        this.previousDelta = this.currentDelta;
        this.currentDelta = player.getDeltaMovement().y;

        System.out.println(effectTimer);

        if(effectTimer > 0){
            effectTimer--;
            if (player.isShiftKeyDown()) this.currentDelta = 0;
            else player.resetFallDistance();

            if (player.verticalCollisionBelow && previousDelta != currentDelta) {
                player.makeSound(SoundEvents.SLIME_SQUISH);
                player.setDeltaMovement(player.getDeltaMovement().add(0, Math.abs(previousDelta / 1.5), 0));
            }
        }
    }

    public void setEffectTimer(int effectTimer){
        this.effectTimer = effectTimer;
    }

}
