package org.jahdoo.common.networking.client2server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.networking.server2client.CastingDataSyncS2CP;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

public class UnlockedSkillsC2SP implements CustomPacketPayload {

    public static final Type<UnlockedSkillsC2SP> TYPE = new Type<>(JahdooHelpers.res("unlocked_skills"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UnlockedSkillsC2SP> STREAM_CODEC =
        CustomPacketPayload.codec(UnlockedSkillsC2SP::toBytes, UnlockedSkillsC2SP::new);

    private final String skillId;
    private final int skillCost;

    public UnlockedSkillsC2SP(String skillId, int skillCost) {
        this.skillId = skillId;
        this.skillCost = skillCost;
    }

    public UnlockedSkillsC2SP(FriendlyByteBuf buf) {
        this.skillId = buf.readUtf();
        this.skillCost = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(skillId);
        buf.writeInt(skillCost);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof ServerPlayer serverPlayer){
                    var casterData = serverPlayer.getData(AttachmentReg.CASTER_DATA);
                    if(casterData.hasUnlockedSkill(skillId)){
                        casterData.decrementAbilityPoints(skillCost);
                        casterData.addNewSkill(skillId);
                    } else {
                        casterData.toggleSkill(skillId);
                    }
                    PacketDistributor.sendToPlayer(serverPlayer, new CastingDataSyncS2CP(casterData));
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
