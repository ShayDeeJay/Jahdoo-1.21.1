package org.jahdoo.ascension.attachments.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.ascension.attachments.IAttachment;
import org.jahdoo.common.networking.server2client.BouncyFootS2CP;
import org.jahdoo.common.registers.EffectReg;

import static org.jahdoo.common.registers.AttachmentReg.BOUNCY_FOOT;

public class Rebound implements IAttachment {

    private double currentDelta;
    private double previousDelta;
    private int effectTimer;
    public float setHighestFallPoint;

    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putInt("effect_timer", this.effectTimer);
        nbt.putDouble("current_delta", this.currentDelta);
        nbt.putDouble("previous_data", this.previousDelta);
    }

    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        this.effectTimer = nbt.getInt("effect_timer");
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
        if(/*effectTimer > 0*/ player.hasEffect(EffectReg.REBOUND.getDelegate())){
            if(player instanceof ServerPlayer serverPlayer){
                var payload = new BouncyFootS2CP(effectTimer, previousDelta, currentDelta, setHighestFallPoint);
                PacketDistributor.sendToPlayer(serverPlayer, payload);
            }

            this.setHighestFallPoint = Math.max(this.setHighestFallPoint, player.fallDistance);
            this.previousDelta = this.currentDelta;
            this.currentDelta = player.getDeltaMovement().y;

            var isJumping = this.currentDelta != this.previousDelta;
            if(!isJumping ||player.verticalCollisionBelow) {
                this.setEffectTimer(0);
                this.setSetHighestFallPoint(0);
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
