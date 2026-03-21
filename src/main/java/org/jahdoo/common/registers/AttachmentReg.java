package org.jahdoo.common.registers;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jahdoo.JahdooMod;
import org.jahdoo.trial_nexus.attachments.*;
import org.jahdoo.trial_nexus.attachments.effects.*;
import org.jahdoo.trial_nexus.attachments.player_abilities.Blink;
import org.jahdoo.trial_nexus.attachments.player_abilities.MageFlight;
import org.jahdoo.trial_nexus.attachments.player_abilities.PhantomJump;
import org.jahdoo.trial_nexus.attachments.player_abilities.Rebound;
import org.jahdoo.trial_nexus.element.*;
import org.jahdoo.trial_nexus.magic.abilities_combat.dimensional_recall.DimensionalRecall;
import org.jahdoo.trial_nexus.magic.abilities_combat.nova_smash.NovaSmash;
import org.jahdoo.trial_nexus.magic.abilities_combat.vital_rejuvenation.VitalRejuvenation;

import java.util.function.Supplier;

import static net.neoforged.neoforge.attachment.AttachmentType.builder;

public class AttachmentReg {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, JahdooMod.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<MageFlight>> MAGE_FLIGHT =
        withProvider("mage_flight",MageFlight::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PhantomJump>> TRIPLE_JUMP =
        withProvider("triple_jump", PhantomJump::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Blink>> BLINK =
        withProvider("blink", Blink::new);

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


    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AbstractEntityEffect>> MYSTIC_EFFECT =
        effectWithType(MysticEffect::new, Mystic::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AbstractEntityEffect>> INFERNO_EFFECT =
        effectWithType(InfernoEffect::new, Inferno::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AbstractEntityEffect>> VITALITY_EFFECT =
        effectWithType(VitalityEffect::new, Vitality::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AbstractEntityEffect>> FROST_EFFECT =
        effectWithType(FrostEffect::new, Frost::new);


    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> BOOL =
        regAttachment("bool", builder(() -> false).serialize(Codec.BOOL));

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CasterData>> CASTER_DATA =
        withProviderCopyDeath("caster_data", CasterData::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SaveItemData>> SAVE_ITEM_DATA =
        withProviderCopyDeath("save_item_data", SaveItemData::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerWallet>> PLAYER_WALLET_DATA =
        withProviderCopyDeathSync("player_wallet", PlayerWallet::new, PlayerWallet.WALLET_STREAM_CODEC);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<RunData>> RUN_DATA =
        withProviderCopyDeath("run_data", RunData::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerTrialData>> PLAYER_TRIAL_DATA =
        withProviderCopyDeath("player_trial_data", PlayerTrialData::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<QuestTracker>> QUEST_TRACKER_DATA=
        withProviderCopyDeath("quest_tracker_data", QuestTracker::new);

    //HELPERS
    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }

    public static <T> DeferredHolder<AttachmentType<?>, AttachmentType<T>> regAttachment(String name, AttachmentType.Builder<T> supplier) {
        return ATTACHMENT_TYPES.register(name, supplier::build);
    }

    public static DeferredHolder<AttachmentType<?>, AttachmentType<AbstractEntityEffect>> effectWithType(Supplier<AbstractEntityEffect> supplier, Supplier<AbstractElement> element){
        return withProviderSync(element.get().setAbilityId() + "_effect", supplier, AbstractEntityEffect.streamCodec(supplier));
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

    public static  <T extends IAttachment> DeferredHolder<AttachmentType<?>, AttachmentType<T>> withProviderCopyDeathSync(
        String name,
        Supplier<T> defaultValueSupplier,
        StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec
    ){
        var serializer = new AttachmentProvider<>(defaultValueSupplier);
        var supplier = builder(defaultValueSupplier).sync(streamCodec).serialize(serializer).copyOnDeath();
        return regAttachment(name, supplier);
    }

    public static  <T extends IAttachment> DeferredHolder<AttachmentType<?>, AttachmentType<T>> withProviderSync(
        String name,
        Supplier<T> defaultValueSupplier,
        StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec
    ){
        var serializer = new AttachmentProvider<>(defaultValueSupplier);
        var supplier = builder(defaultValueSupplier).sync(streamCodec).serialize(serializer);
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
