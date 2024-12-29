package org.jahdoo.registers;

import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jahdoo.JahdooMod;
import org.jahdoo.attachments.AbstractAttachment;
import org.jahdoo.attachments.GenericProvider;
import org.jahdoo.attachments.player_abilities.MageFlight;
import org.jahdoo.attachments.CastingData;
import org.jahdoo.attachments.player_abilities.*;

public class AttachmentRegister {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, JahdooMod.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CastingData>> CASTER_DATA = ATTACHMENT_TYPES.register(
        "caster_data", () -> AttachmentType.builder(CastingData::new).serialize(new GenericProvider<>(CastingData::new)).copyOnDeath().build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MageFlight>> MAGE_FLIGHT = ATTACHMENT_TYPES.register(
        "mage_flight", () -> AttachmentType.builder(MageFlight::new).serialize(new GenericProvider<>(MageFlight::new)).copyOnDeath().build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Static>> STATIC = ATTACHMENT_TYPES.register(
        "static", () -> AttachmentType.builder(Static::new).serialize(new GenericProvider<>(Static::new)).copyOnDeath().build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<DimensionalRecall>> DIMENSIONAL_RECALL = ATTACHMENT_TYPES.register(
        "dimensional_recall", () -> AttachmentType.builder(DimensionalRecall::new).serialize(new GenericProvider<>(DimensionalRecall::new)).copyOnDeath().build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<VitalRejuvenation>> VITAL_REJUVENATION = ATTACHMENT_TYPES.register(
        "vital_rejuvenation", () -> AttachmentType.builder(VitalRejuvenation::new).serialize(new GenericProvider<>(VitalRejuvenation::new)).copyOnDeath().build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SaveData>> SAVE_DATA = ATTACHMENT_TYPES.register(
        "save_data", () -> AttachmentType.builder(SaveData::new).serialize(new GenericProvider<>(SaveData::new)).copyOnDeath().build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<NovaSmash>> NOVA_SMASH = ATTACHMENT_TYPES.register(
        "nova_smash", () -> AttachmentType.builder(NovaSmash::new).serialize(new GenericProvider<>(NovaSmash::new)).copyOnDeath().build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<BouncyFoot>> BOUNCY_FOOT = ATTACHMENT_TYPES.register(
        "bouncy_foot", () -> AttachmentType.builder(BouncyFoot::new).serialize(new GenericProvider<>(BouncyFoot::new)).copyOnDeath().build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerScale>> PLAYER_SCALE = ATTACHMENT_TYPES.register(
        "player_scale", () -> AttachmentType.builder(PlayerScale::new).serialize(new GenericProvider<>(PlayerScale::new)).copyOnDeath().build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ModularChaosCubeProperties>> MODULAR_CHAOS_CUBE = ATTACHMENT_TYPES.register(
        "modular_chaos_cube", () -> AttachmentType.builder(ModularChaosCubeProperties::new).serialize(new GenericProvider<>(ModularChaosCubeProperties::new)).build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ChallengeAltarData>> CHALLENGE_ALTAR = ATTACHMENT_TYPES.register(
        "challenge_altar", () -> AttachmentType.builder(ChallengeAltarData::new).serialize(new GenericProvider<>(ChallengeAltarData::new)).build()
    );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> BOOL = ATTACHMENT_TYPES.register(
        "bool", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build()
    );

    public static <T extends AbstractAttachment> DeferredHolder<AttachmentType<?>, AttachmentType<T>> getHolder(T attachment, String name){
        return ATTACHMENT_TYPES.register(
            name, () -> AttachmentType.builder(() -> attachment)
                .serialize(new GenericProvider<>(() -> attachment))
                .copyOnDeath()
                .build()
        );
    }

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
