package org.jahdoo.trial_nexus.attachments.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.SkillReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.attachments.IAttachment;
import org.shaydee.shaydeeapi.helpers.MathHelpers;

import static org.jahdoo.common.registers.AttachmentReg.BOUNCY_FOOT;

public class Rebound implements IAttachment {

    private double currentDelta;
    private double previousDelta;
    private int effectTimer;
    public float setHighestFallPoint;
    public int bounceCount;

    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putInt("effect_timer", this.effectTimer);
        nbt.putInt("bounce", this.bounceCount);
        nbt.putDouble("current_delta", this.currentDelta);
        nbt.putDouble("previous_data", this.previousDelta);
    }

    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        this.effectTimer = nbt.getInt("effect_timer");
        this.bounceCount = nbt.getInt("bounce");
        this.currentDelta = nbt.getDouble("current_delta");
        this.previousDelta = nbt.getDouble("previous_data");
    }

    public static void setBouncyFoot(Player player, int effectTimer){
        player.getData(BOUNCY_FOOT).setEffectTimer(effectTimer);
    }

    public static void staticTickEvent(Player player){
        player.getData(BOUNCY_FOOT).onTick(player);
    }

    public void onTick(Player player){
        if(CasterData.hasSkill(player, SkillReg.REBOUND.get().id())){

            if (player.verticalCollisionBelow && previousDelta != currentDelta) {
                if(setHighestFallPoint > 0.5D){
                    var reducedDelta = Math.abs(previousDelta / 2.5);
                    var volume = (float) reducedDelta - 0.2f;
                    player.playSound(SoundEvents.FROG_TONGUE, volume, 1.2f);
                    player.playSound(SoundReg.SUSPEND.get(), volume, 2f);
                    player.setDeltaMovement(player.getDeltaMovement().add(0, MathHelpers.singleFormattedDouble(Math.min(reducedDelta, 3)), 0));
                }
            }
            
            this.setHighestFallPoint = Math.max(this.setHighestFallPoint, player.fallDistance);
            this.previousDelta = this.currentDelta;
            this.currentDelta = player.getDeltaMovement().y;

            var isJumping = this.currentDelta != this.previousDelta;

            if(!isJumping || player.verticalCollisionBelow) {

                this.setEffectTimer(0);
                if(this.currentDelta > 0) bounceCount++;
                if(bounceCount >= 3) this.currentDelta = 0;

            }

            effectTimer--;
            player.resetFallDistance();
            if (player.isShiftKeyDown()) this.currentDelta = 0;
        }
    }

    public void setEffectTimer(int effectTimer){
        this.effectTimer = effectTimer;
    }

    public void setSetHighestFallPoint(float max){
        this.setHighestFallPoint = max;
    }

    public void setCurrentDelta(double currentDelta) {
        this.currentDelta = currentDelta;
    }

    public void setPreviousDelta(double previousDelta) {
        this.previousDelta = previousDelta;
    }
}
