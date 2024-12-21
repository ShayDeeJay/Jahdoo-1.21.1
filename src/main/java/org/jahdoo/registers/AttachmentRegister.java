package org.jahdoo.registers;

import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerPlayer;
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

import java.util.function.Supplier;

public class AttachmentRegister {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, JahdooMod.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CastingData>> CASTER_DATA = getHolder(new CastingData(), "caster_data");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MageFlight>> MAGE_FLIGHT = getHolder(new MageFlight(), "mage_flight");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Static>> STATIC = getHolder(new Static(), "static");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<DimensionalRecall>> DIMENSIONAL_RECALL = getHolder(new DimensionalRecall(), "dimensional_recall");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<VitalRejuvenation>> VITAL_REJUVENATION = getHolder(new VitalRejuvenation(), "vital_rejuvenation");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SaveData>> SAVE_DATA = getHolder(new SaveData(), "save_data");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<NovaSmash>> NOVA_SMASH = getHolder(new NovaSmash(), "nova_smash");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<BouncyFoot>> BOUNCY_FOOT = getHolder(new BouncyFoot(), "bouncy_foot");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerScale>> PLAYER_SCALE = getHolder(new PlayerScale(), "player_scale");
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ModularChaosCubeProperties>> MODULAR_CHAOS_CUBE = getHolder(new ModularChaosCubeProperties(), "modular_chaos_cube");

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
