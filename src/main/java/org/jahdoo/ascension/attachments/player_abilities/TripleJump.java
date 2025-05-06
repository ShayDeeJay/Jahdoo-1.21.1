package org.jahdoo.ascension.attachments.player_abilities;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.ascension.attachments.IAttachment;
import org.jahdoo.ascension.utils.PositionFinders;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.EffectReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;

import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.common.registers.AttachmentReg.TRIPLE_JUMP;

public class TripleJump implements IAttachment {

    public static final int MAX_JUMPS = 2;
    private int clientJumpCount = 0;
    private boolean clientIsJumpHeld;
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putInt("jumpCount", clientJumpCount);
        nbt.putBoolean("isJumpHeld", clientIsJumpHeld);
    }

    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        this.clientJumpCount = nbt.getInt("jumpCount");
        this.clientIsJumpHeld = nbt.getBoolean("isJumpHeld");
    }


    public static void tripleJumpTickEvent(Player player){
        var mageFlight = player.getData(TRIPLE_JUMP);
        mageFlight.onClientTick(player);
    }

    private void onClientTick(Player player) {
        if(!(player instanceof LocalPlayer localPlayer) || !player.hasEffect(EffectReg.TRIPLE_JUMP) || player.isCreative()) return;
        if(player.verticalCollisionBelow) {
            clientJumpCount = 0;
        } else if (localPlayer.input.jumping){
            if(!clientIsJumpHeld && clientJumpCount <= MAX_JUMPS){
                clientJumpCount++;
                var delta = player.getDeltaMovement();
                player.setDeltaMovement(delta.x, Math.max(delta.y, 0.54D), delta.z);
                if(clientJumpCount > 1){
                    var type = ParticleHandlers.genericParticle(GENERIC_PARTICLE, ElementReg.frost(), 6, 2f);
                    PositionFinders.innerRadiusRandom(player.position(), player.getBbWidth() * 1.5, 50,
                        positions -> ParticleHandlers.sendParticles(player.level(), type, positions, 1, 0, -0.5f, 0, 10)
                    );
                    player.playSound(SoundReg.SPELL_SOUND.get(), 1F, 1.2F);
                }
            }
            clientIsJumpHeld = true;
        } else {
            clientIsJumpHeld = false;
        }
    }

}
