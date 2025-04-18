package org.jahdoo.common.registers;

import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jahdoo.JahdooMod;
import org.jahdoo.ascension.ability.abilities_combat.dimensional_recall.DimensionalRecall;
import org.jahdoo.ascension.ability.abilities_combat.nova_smash.NovaSmash;
import org.jahdoo.ascension.ability.abilities_combat.vital_rejuvenation.VitalRejuvenation;
import org.jahdoo.ascension.attachments.*;
import org.jahdoo.ascension.attachments.player_abilities.MageFlight;
import org.jahdoo.ascension.attachments.player_abilities.*;

import java.util.function.Supplier;

import static net.neoforged.neoforge.attachment.AttachmentType.*;

public class AttachmentReg {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, JahdooMod.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MageFlight>> MAGE_FLIGHT =
        withProvider("mage_flight",MageFlight::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TripleJump>> TRIPLE_JUMP =
        withProvider("triple_jump", TripleJump::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<DimensionalRecall>> DIMENSIONAL_RECALL =
        withProvider("dimensional_recall", DimensionalRecall::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<VitalRejuvenation>> VITAL_REJUVENATION =
        withProvider("vital_rejuvenation", VitalRejuvenation::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<NovaSmash>> NOVA_SMASH =
        withProvider("nova_smash", NovaSmash::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Rebound>> BOUNCY_FOOT =
        withProvider("bouncy_foot", Rebound::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<ChaosCubeData>> MODULAR_CHAOS_CUBE =
        withProvider("modular_chaos_cube", ChaosCubeData::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<InstanceData>> INSTANCE_DATA =
        withProvider("instance_data", InstanceData::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> BOOL =
        regAttachment("bool", builder(() -> false).serialize(Codec.BOOL));

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CasterData>> CASTER_DATA =
        withProviderCopyDeath("caster_data", CasterData::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SaveItemData>> SAVE_ITEM_DATA =
        withProviderCopyDeath("save_item_data", SaveItemData::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerWallet>> PLAYER_WALLET =
        withProviderCopyDeath("player_wallet", PlayerWallet::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<RunData>> RUN_DATA =
        withProviderCopyDeath("run_data", RunData::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerTrialData>> PLAYER_TRIAL_DATA =
        withProviderCopyDeath("player_trial_data", PlayerTrialData::new);

    //HELPERS
    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }

    public static <T> DeferredHolder<AttachmentType<?>, AttachmentType<T>> regAttachment(String name, AttachmentType.Builder<T> supplier) {
        return ATTACHMENT_TYPES.register(name, supplier::build);
    }

    public static  <T extends IAttachment> DeferredHolder<AttachmentType<?>, AttachmentType<T>> withProvider(
        String name,
        Supplier<T> defaultValueSupplier
    ){
        var serializer = new AttachmentProvider<T>(defaultValueSupplier);
        var supplier = builder(defaultValueSupplier).serialize(serializer);
        return regAttachment(name, supplier);
    }

    public static  <T extends IAttachment> DeferredHolder<AttachmentType<?>, AttachmentType<T>> withProviderCopyDeath(
        String name,
        Supplier<T> defaultValueSupplier
    ){
        var serializer = new AttachmentProvider<>(defaultValueSupplier);
        var supplier = builder(defaultValueSupplier).serialize(serializer).copyOnDeath();
        return regAttachment(name, supplier);
    }

    public static <T extends IAttachment> DeferredHolder<AttachmentType<?>, AttachmentType<T>> getHolder(T attachment, String name){
        return ATTACHMENT_TYPES.register(
            name, () -> builder(() -> attachment)
                .serialize(new AttachmentProvider<>(() -> attachment))
                .copyOnDeath()
                .build()
        );
    }

}
