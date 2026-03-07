package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.items.caster_item.CastHelper;
import org.jahdoo.common.networking.client2server.BlinkManaAndCounterC2SP;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.attachments.player_abilities.Blink;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jahdoo.common.particle.ParticleHandlers.getAllParticleTypes;
import static org.jahdoo.common.registers.mod.ElementReg.fromWand;
import static org.jahdoo.trial_nexus.magic.abilities_combat.ancient_golem.SummonAncientGolem.clientDiggingParticles;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;

public class BlinkS2CP implements CustomPacketPayload {
    public static final Type<BlinkS2CP> TYPE = new Type<>(JahdooHelpers.res("blink_s2cp"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlinkS2CP> STREAM_CODEC = CustomPacketPayload.codec(BlinkS2CP::toBytes, BlinkS2CP::new);
    private static final Logger log = LoggerFactory.getLogger(BlinkS2CP.class);

    private final double x;
    private final double y;
    private final double z;
    private final int id;

    public BlinkS2CP(
        double x,
        double y,
        double z,
        int id
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
    }

    public BlinkS2CP(FriendlyByteBuf buf) {
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
        this.id = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf bug) {
        bug.writeDouble(this.x);
        bug.writeDouble(this.y);
        bug.writeDouble(this.z);
        bug.writeInt(this.id);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                var player = ctx.player();
                if (!(player instanceof LocalPlayer localPlayer)) return;

                if (id == Blink.ANIMATION_COOLDOWN) {
                    if (localPlayer.xxa == 0.0 && localPlayer.zza == 0.0) return;
                    PacketDistributor.sendToServer(new BlinkManaAndCounterC2SP());
                    applyHorizontalMomentum(localPlayer);
                }

                var blink = localPlayer.getData(AttachmentReg.BLINK);

                blink.setCounter(id);
                handleBlinkEffects(localPlayer, blink.getCounter());
            }
        );
    }

    private void applyHorizontalMomentum(LocalPlayer player) {
        var direction = player.getDeltaMovement().normalize().scale(x);
        player.setDeltaMovement(direction.x, 0, direction.z);
    }

    private void handleBlinkEffects(LocalPlayer player, int counter) {
        final float volume = 0.6F;
        final int particleCount = 20;

        var isStart = counter != 1;
        var yOffset = isStart ? 1.0 : -1.0;
        var yVelocity = isStart
            ? Random.nextDouble(0.2, 0.5) - Random.nextDouble(15, 25)
            : Random.nextDouble(0.2, 0.5) + Random.nextDouble(15, 25);

        for (int i = 0; i < particleCount; i++) {
            spawnBlinkParticle(player, yOffset, yVelocity);
        }

        if (!isStart) {
            player.playSound(SoundReg.THUD_B.get(), volume, 1F);
            player.playSound(SoundReg.LEVITATE.get(), volume, 1F);
        } else {
            player.playSound(SoundReg.BLINK_A.get(), volume, 1.6F);
            player.playSound(SoundReg.THUD_A.get(), volume, 1F);
        }

        clientDiggingParticles(player, player.level());
    }

    private void spawnBlinkParticle(LocalPlayer player, double yOffset, double yVelocity) {
        var usedItem = CastHelper.hasValidCasterItem(player).getItem();
        var element = fromWand(usedItem).orElse(ElementReg.random());
        var particle = getAllParticleTypes(element, 5, 1.5f);

        var positions = new Vec3(
            player.getRandomX(1.5),
            player.getRandomY() + yOffset,
            player.getRandomZ(1.5)
        );

        ParticleHandlers.sendParticles(
            player.level(), particle, positions, 0,
            Random.nextDouble(0.1, 0.3) - 0.2,
            yVelocity,
            Random.nextDouble(0.1, 0.3) - 0.2,
            2F
        );
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
