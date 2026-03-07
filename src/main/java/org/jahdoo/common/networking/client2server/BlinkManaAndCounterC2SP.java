package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.trial_nexus.magic.skills.BlinkSkill;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import static org.jahdoo.trial_nexus.attachments.player_abilities.Blink.ANIMATION_COOLDOWN;
import static org.jahdoo.trial_nexus.attachments.player_abilities.Blink.MANA_COST;

public class BlinkManaAndCounterC2SP implements CustomPacketPayload {

    public static final Type<BlinkManaAndCounterC2SP> TYPE = new Type<>(JahdooHelpers.res("blink_mana_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlinkManaAndCounterC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(BlinkManaAndCounterC2SP::toBytes, BlinkManaAndCounterC2SP::new);

    public BlinkManaAndCounterC2SP() {}

    public BlinkManaAndCounterC2SP(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf bug) {}

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                var player = ctx.player();
                if(!(player instanceof ServerPlayer serverPlayer)) return;

                var blink = serverPlayer.getData(AttachmentReg.BLINK);
                var casterData = serverPlayer.getData(AttachmentReg.CASTER_DATA);

                casterData.subtractMana(MANA_COST, serverPlayer);
                casterData.addCooldown(serverPlayer, BlinkSkill.BLINK, 20);

                blink.setCounter(ANIMATION_COOLDOWN);
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
