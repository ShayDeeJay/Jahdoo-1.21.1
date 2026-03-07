package org.jahdoo.trial_nexus.attachments.player_abilities;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.items.caster_item.CastHelper;
import org.jahdoo.common.networking.server2client.BlinkS2CP;
import org.jahdoo.common.networking.server2client.MoveClientEntityS2CP;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.common.registers.mod.SkillReg;
import org.jahdoo.trial_nexus.attachments.IAttachment;

import static org.jahdoo.common.registers.AttachmentReg.BLINK;
import static org.jahdoo.trial_nexus.attachments.CasterData.hasSkill;

public class Blink implements IAttachment {

    public static final int MANA_COST = 20;
    public static final int ANIMATION_COOLDOWN = 8;
    private int counter = 0;

    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putInt("counter", counter);
    }

    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        this.counter = nbt.getInt("jumpCount");
    }

    public static void setMovement(Player player) {
        if(!(player instanceof ServerPlayer serverPlayer)) return;

        var blink = serverPlayer.getData(AttachmentReg.BLINK);
        var casterData = serverPlayer.getData(AttachmentReg.CASTER_DATA);
        var hasSkill = hasSkill(player, SkillReg.BLINK.get().id());
        var hasMana = CastHelper.sufficientMana(player, casterData, MANA_COST, null);
        var isReset = blink.getCounter() == 0;
        var isGrounded = serverPlayer.onGround();

        if(hasSkill && hasMana && isReset && isGrounded) {
            PacketDistributor.sendToPlayer(serverPlayer, new BlinkS2CP(serverPlayer.getAttributeValue(AttributeReg.BLINK_RANGE), 0, 0, ANIMATION_COOLDOWN));
        }
    }

    private static void stopPlayer(ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new MoveClientEntityS2CP(0, 0, 0, serverPlayer.getId()));
    }

    public static void blinkTickEvent(Player player){
        player.getData(BLINK).onTick(player);
    }

    public static boolean isActive(LivingEntity player){
        var blink = player.getData(BLINK);
        return blink.isActive();
    }

    public static boolean isComplete(Player player){
        var blink = player.getData(BLINK);
        return blink.isComplete();
    }

    public boolean isActive() {
        return counter > 1;
    }

    public boolean isComplete() {
        return counter == 1;
    }

    public int getCounter() {
        return this.counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void onTick(Player player) {
        if(counter > 0) counter--;

        if(player instanceof ServerPlayer serverPlayer) {
            if(counter == 1){
                stopPlayer(serverPlayer);
                PacketDistributor.sendToPlayer(serverPlayer, new BlinkS2CP(0, 0, 0, 1));
            }
        }
    }

}
