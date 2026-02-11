package org.jahdoo.common.networking.server2client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CastingUnlocksSyncS2CP implements CustomPacketPayload {

    public static final Type<CastingUnlocksSyncS2CP> TYPE =
        new Type<>(JahdooHelpers.res("casting_unlocks_s2c"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CastingUnlocksSyncS2CP> STREAM_CODEC =
        CustomPacketPayload.codec(CastingUnlocksSyncS2CP::toBytes, CastingUnlocksSyncS2CP::new);

    private final List<AbilityHolder> unlockedAbilities;
    private final List<String> unlockedSkills;
    private final Map<Integer, CasterData.LoadoutObj> loadouts;

    // ================= CONSTRUCTORS =================

    public CastingUnlocksSyncS2CP(
        CasterData casterData
    ) {
        this.unlockedAbilities = casterData.getUnlockedAbilities();
        this.unlockedSkills = casterData.getUnlockedSkills();
        this.loadouts = casterData.getLoadouts();
    }

    public CastingUnlocksSyncS2CP(FriendlyByteBuf buf) {

        // ===== Unlocked abilities =====
        int abilityCount = buf.readVarInt();
        List<AbilityHolder> abilities = new ArrayList<>(abilityCount);
        for (int i = 0; i < abilityCount; i++) {
            abilities.add(buf.readJsonWithCodec(AbilityHolder.CODEC));
        }

        // ===== Unlocked skills =====
        int skillCount = buf.readVarInt();
        List<String> skills = new ArrayList<>(skillCount);
        for (int i = 0; i < skillCount; i++) {
            skills.add(buf.readUtf());
        }

        // ===== Loadouts =====
        int loadoutCount = buf.readVarInt();
        Map<Integer, CasterData.LoadoutObj> loadouts = new HashMap<>();
        for (int i = 0; i < loadoutCount; i++) {
            int index = buf.readVarInt();
            loadouts.put(index, buf.readJsonWithCodec(CasterData.LoadoutObj.CODEC));
        }

        this.unlockedAbilities = abilities;
        this.unlockedSkills = skills;
        this.loadouts = loadouts;
    }

    // ================= ENCODE =================

    public void toBytes(FriendlyByteBuf buf) {

        // ===== Unlocked abilities =====
        buf.writeVarInt(unlockedAbilities.size());
        for (AbilityHolder ability : unlockedAbilities) {
            buf.writeJsonWithCodec(AbilityHolder.CODEC, ability);
        }

        // ===== Unlocked skills =====
        buf.writeVarInt(unlockedSkills.size());
        for (String skill : unlockedSkills) {
            buf.writeUtf(skill);
        }

        // ===== Loadouts =====
        buf.writeVarInt(loadouts.size());
        loadouts.forEach((index, loadout) -> {
            buf.writeVarInt(index);
            buf .writeJsonWithCodec(CasterData.LoadoutObj.CODEC, loadout);
        });
    }

    // ================= HANDLE =================

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof LocalPlayer player) {

                CasterData data = player.getData(AttachmentReg.CASTER_DATA);

                if (data != null) {
                    data.setUnlockedAbilities(unlockedAbilities);
                    data.setUnlockedSkills(unlockedSkills);
                    data.setLoadouts(loadouts);
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
