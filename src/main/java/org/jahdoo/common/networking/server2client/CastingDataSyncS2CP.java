package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CastingDataSyncS2CP implements CustomPacketPayload {

    public static final Type<CastingDataSyncS2CP> TYPE = new Type<>(JahdooHelpers.res("selected_ability_s2c"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CastingDataSyncS2CP> STREAM_CODEC =
        CustomPacketPayload.codec(CastingDataSyncS2CP::toBytes, CastingDataSyncS2CP::new);

    private final CasterData data;

    public CastingDataSyncS2CP(CasterData data) {
        this.data = data;
    }

    public CastingDataSyncS2CP(FriendlyByteBuf buf) {
        var xp = buf.readVarInt();
        var slots = buf.readVarInt();
        var abilityPoints = buf.readVarInt();
        var refundPoints = buf.readVarInt();
        var multiKill = buf.readVarInt();
        var loadoutIndex = buf.readVarInt();
        var manaPool = buf.readDouble();

        String selectedAbility = buf.readUtf();

        Map<String, Integer> cooldowns = new HashMap<>();
        var cooldownCount = buf.readVarInt();
        for (int i = 0; i < cooldownCount; i++) {
            cooldowns.put(
                buf.readUtf(),
                buf.readVarInt()
            );
        }

        Map<String, Integer> staticCooldowns = new HashMap<>();
        var staticCount = buf.readVarInt();
        for (int i = 0; i < staticCount; i++) {
            staticCooldowns.put(
                buf.readUtf(),
                buf.readVarInt()
            );
        }

        List<String> abilitySlots = new ArrayList<>();
        var slotCount = buf.readVarInt();
        for (int i = 0; i < slotCount; i++) {
            abilitySlots.add(buf.readUtf());
        }

        List<String> activeSkills = new ArrayList<>();
        var skillCount = buf.readVarInt();
        for (int i = 0; i < skillCount; i++) {
            activeSkills.add(buf.readUtf());
        }

        this.data = new CasterData(
            xp,
            slots,
            abilityPoints,
            refundPoints,
            multiKill,
            loadoutIndex,
            manaPool,
            selectedAbility,
            cooldowns,
            staticCooldowns,
            List.of(),          // unlocked abilities (sync separately)
            abilitySlots,
            List.of(),          // unlocked skills (sync separately)
            activeSkills,
            Map.of()            // loadouts (sync separately)
        );
    }

    public void toBytes(FriendlyByteBuf buf) {
        // ===== Core stats =====
        buf.writeVarInt(data.getExp());
        buf.writeVarInt(data.getAllowedSlots());
        buf.writeVarInt(data.getAbilityPoints());
        buf.writeVarInt(data.getRefundableSkillPoints());
        buf.writeVarInt(data.getMultiKill());
        buf.writeVarInt(data.getLoadoutIndex());
        buf.writeDouble(data.getManaPool());
        buf.writeUtf(data.getSelectedAbility());

        var cooldowns = data.getAllCooldowns();
        buf.writeVarInt(cooldowns.size());
        cooldowns.forEach((id, cd) -> {
            buf.writeUtf(id);
            buf.writeVarInt(cd);
        });

        var staticCooldowns = data.getAllCooldownsStatic();
        buf.writeVarInt(staticCooldowns.size());
        staticCooldowns.forEach((id, cd) -> {
            buf.writeUtf(id);
            buf.writeVarInt(cd);
        });

        var slots = data.getAbilitySlots();
        buf.writeVarInt(slots.size());
        for (String slot : slots) {
            buf.writeUtf(slot);
        }

        var activeSkills = data.getActiveSkills();
        buf.writeVarInt(activeSkills.size());
        for (String skill : activeSkills) {
            buf.writeUtf(skill);
        }
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(
            () -> {
                if(ctx.player() instanceof LocalPlayer localPlayer){
                    localPlayer.setData(AttachmentReg.CASTER_DATA, data);
                }
            }
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
