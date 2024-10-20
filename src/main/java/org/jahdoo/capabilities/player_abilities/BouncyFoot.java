package org.jahdoo.capabilities.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.capabilities.AbstractAttachment;
import org.jahdoo.utils.GeneralHelpers;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.registers.AttachmentRegister.*;

public class BouncyFoot implements AbstractAttachment {

    private double currentDelta;
    private double previousDelta;
    private int effectTimer;

    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putDouble("current_delta", this.currentDelta);
        nbt.putDouble("previous_data", this.previousDelta);
        nbt.putInt("effect_timer", this.effectTimer);
    }

    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        this.currentDelta = nbt.getDouble("current_delta");
        this.previousDelta = nbt.getDouble("previous_data");
        this.effectTimer = nbt.getInt("effect_timer");
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

        if(this.currentDelta == this.previousDelta) this.effectTimer = 0;

        if(effectTimer > 0){
            effectTimer--;
            if (player.isShiftKeyDown()) this.currentDelta = 0;
            else player.resetFallDistance();

            if (player.verticalCollisionBelow && previousDelta != currentDelta) {
                var reducedDelta = Math.abs(previousDelta / 1.5);
                player.playSound(SoundEvents.HONEY_BLOCK_HIT, (float) reducedDelta, 2f);
                player.setDeltaMovement(player.getDeltaMovement().add(0, Math.min(reducedDelta, 3), 0));
            }
        }
    }

    public void setEffectTimer(int effectTimer){
        this.effectTimer = effectTimer;
    }

}
