package org.jahdoo.trial_nexus.attachments.player_abilities;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.common.items.caster_item.CastHelper;
import org.jahdoo.common.networking.client2server.PhantomJumpMagicCircleC2SP;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.common.registers.mod.SkillReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.attachments.IAttachment;
import org.jahdoo.trial_nexus.utils.PositionFinders;

import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.common.registers.AttachmentReg.TRIPLE_JUMP;
import static org.jahdoo.common.registers.mod.ElementReg.fromWand;

public class PhantomJump implements IAttachment {

    public static final String PHANTOM_JUMP = "jump_circle";
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

    public static void jumpTickEvent(Player player){
        var mageFlight = player.getData(TRIPLE_JUMP);
        mageFlight.onClientTick(player);
    }

    private void onClientTick(Player player) {

        if(!(player instanceof LocalPlayer localPlayer) || !CasterData.hasSkill(player, SkillReg.TRIPLE_JUMP.get().id()) || player.isCreative()) return;
        if(player.verticalCollisionBelow) {
            clientJumpCount = 0;
        } else if (localPlayer.input.jumping){
            var attribute = player.getAttribute(AttributeReg.TRIPLE_JUMP);
            if(attribute == null) return;

            if(!clientIsJumpHeld && clientJumpCount <= 3){
                clientJumpCount++;
                var delta = player.getDeltaMovement();
                var playerSpeed = player.getSpeed();
                if(clientJumpCount > 1){
                    var usedItem = CastHelper.hasValidCasterItem(player).getItem();
                    var element = fromWand(usedItem).orElse(ElementReg.random());
                    var type = ParticleHandlers.genericParticle(GENERIC_PARTICLE, element, 2, 2f);
                    var part2 = bakedParticle(element.id(), 2, 2f, false);

                    player.setDeltaMovement(delta.x * (1+playerSpeed), Math.max(delta.y, 0.8D), delta.z * (1+playerSpeed));
                    PacketDistributor.sendToServer(new PhantomJumpMagicCircleC2SP(element.id()));
                    player.playSound(SoundReg.SPELL_SOUND.get(), 1F, 1.2F);
                    PositionFinders.innerRadiusRandom(player.position(), player.getBbWidth() * 1.5, 30,
                        positions -> {
                            ParticleHandlers.sendParticles(player.level(), type, positions, 1, 0, -1f, 0, 10);
                            ParticleHandlers.sendParticles(player.level(), part2, positions, 1, 0, -1f, 0, 10);
                        }
                    );
                }
            }

            clientIsJumpHeld = true;
        } else {
            clientIsJumpHeld = false;
        }
    }

}
