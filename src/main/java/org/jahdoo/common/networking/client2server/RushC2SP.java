package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.items.caster_item.CastHelper;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.AttributeReg;
import org.jahdoo.common.registers.mod.SkillReg;
import org.jahdoo.trial_nexus.magic.skills.RushSkill;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.shaydee.shaydeeapi.helpers.ClientHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import static org.jahdoo.common.registers.SoundReg.DASH_EFFECT_INSTANT;
import static org.jahdoo.trial_nexus.attachments.CasterData.hasSkill;

public class RushC2SP implements CustomPacketPayload {

    public static final Type<RushC2SP> TYPE = new Type<>(JahdooHelpers.res("rush_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RushC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(RushC2SP::toBytes, RushC2SP::new);

    public RushC2SP() {}

    public RushC2SP(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf bug) {}

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                var player = ctx.player();
                if(!(player instanceof ServerPlayer serverPlayer)) return;

                var casterData = serverPlayer.getData(AttachmentReg.CASTER_DATA);
                casterData.subtractMana(RushSkill.MANA_COST, serverPlayer);

                casterData.addCooldown(serverPlayer, RushSkill.RUSH, 20);
            }
        );
    }

    public static void clientRush() {
        //Add mana
        var mc = ClientHelpers.getMinecraft();
        var level = mc.level;
        var player = mc.player;
        if(player == null || level == null || player.onGround()) return;

        var casterData = player.getData(AttachmentReg.CASTER_DATA);
        var hasSkill = hasSkill(player, SkillReg.RUSH.get().id());
        var hasMana = CastHelper.sufficientMana(player, casterData, RushSkill.MANA_COST, null);
        var isOnCooldown = casterData.isAbilityOnCooldown(RushSkill.RUSH);

        if(hasSkill && hasMana && !isOnCooldown){
            PacketDistributor.sendToServer(new RushC2SP());
            var attribute = player.getAttribute(AttributeReg.RUSH);
            if (attribute == null) return;

            var lookVector = player.getLookAngle().scale(attribute.getValue());

            player.setDeltaMovement(lookVector.x, lookVector.y, lookVector.z);
            player.playSound(DASH_EFFECT_INSTANT.get(), 1, 1.4F);

            SoundHelpers.getSoundWithPosition(level, player.blockPosition(), DASH_EFFECT_INSTANT.get(), SoundSource.NEUTRAL, 2f);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
