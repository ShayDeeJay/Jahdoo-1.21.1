package org.jahdoo.trial_nexus.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.components.AbilityData;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.networking.client2server.AbilityHolderC2SP;
import org.jahdoo.common.networking.client2server.SelectAbilityC2SP;
import org.jahdoo.common.networking.server2client.ClientSoundS2CP;
import org.jahdoo.common.networking.server2client.QuestTrackerS2CP;
import org.jahdoo.common.networking.server2client.RunDataS2CP;
import org.jahdoo.common.networking.server2client.WalletSyncS2CP;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.particle.particle_options.GenericParticleOptions;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.QuestTracker;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.MathHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static com.github.L_Ender.cataclysm.init.ModEntities.*;
import static java.util.Collections.emptyMap;
import static net.minecraft.advancements.CriteriaTriggers.ITEM_DURABILITY_CHANGED;
import static net.minecraft.sounds.SoundEvents.ITEM_BREAK;
import static net.minecraft.world.item.enchantment.EnchantmentHelper.processDurabilityChange;
import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;
import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.particle.ParticleStore.SOFT_PARTICLE;
import static org.jahdoo.common.registers.AttachmentReg.PLAYER_WALLET_DATA;
import static org.jahdoo.common.registers.AttachmentReg.RUN_DATA;
import static org.jahdoo.common.registers.mod.ElementReg.utility;

public class JahdooHelpers {
    public static final String EASY = "novice";
    public static final String MEDIUM = "expert";
    public static final String HARD = "master";

    public static final Random Random = ThreadLocalRandom.current();

    public static void syncAbilities(){
        sendToServer(new AbilityHolderC2SP(AbilityHolder.DEFAULT, 0));
    }


    public static void syncSelectedAbility(Player player, String updateAbility) {
        player.getData(AttachmentReg.CASTER_DATA).setSelectedAbility(updateAbility);
        sendToServer(new SelectAbilityC2SP(updateAbility));
    }

    public static void syncClientData(Entity player) {
        if(player instanceof ServerPlayer serverPlayer){
            var casterData = serverPlayer.getData(AttachmentReg.CASTER_DATA);
            var wallet = serverPlayer.getData(PLAYER_WALLET_DATA).getWallet();
            var runData = serverPlayer.getData(RUN_DATA);
            sendToPlayer(serverPlayer, new WalletSyncS2CP(wallet));
            CasterData.sharedPackets(serverPlayer, casterData);
            PlayerTrialData.updateClientData(serverPlayer);

            if(LevelGenerator.isNexus(player.level())){
                sendToPlayer(serverPlayer, new RunDataS2CP(runData));
                sendToPlayer(serverPlayer, new QuestTrackerS2CP(QuestTracker.getQuestTracker(serverPlayer)));
            }
        }
    }

    public static void itemOverlay(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, Consumer<ItemStack> runnable) {
//        var handContextRight = displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
//        var handContextLeft = displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;
//        var groundContext = displayContext == ItemDisplayContext.GROUND;
//        if(itemStack.getItem() instanceof Augment){
//            var key = DataComponentHelper.getKeyFromAugment(itemStack);
//            if(!key.isEmpty()){
//                var ability = AbilityRegister.getFirstSpellByTypeId(key);
//                if(ability.isPresent()){
//                    var itemOverlay = ability.get().getItemOverlay();
//                    if(itemOverlay != null){
//                        poseStack.pushPose();
//                        float z = 0.45f;
//
//                        poseStack.scale(z, z, handContextRight || handContextLeft ?0.5f : 1.01f);
//                        poseStack.translate( handContextRight ? 0.0603 : handContextLeft ? -0.2014 : 0, groundContext ? 0.365 : handContextRight || handContextLeft ? 0.50 : 0.43, handContextRight || handContextLeft? -0.05 : 0);
//                        if(handContextRight || handContextLeft){
//                            poseStack.rotateAround(Axis.YN.rotationDegrees(0), 0, 0, 0);
//                            poseStack.rotateAround(Axis.XN.rotationDegrees(0), 0, 0, 0);
//                            poseStack.rotateAround(Axis.ZN.rotationDegrees(0), 0, 0, 0);
//                        }
//                        runnable.accept(new ItemStack(itemOverlay));
//                        poseStack.popPose();
//                    };
//                }
//            }
//        }
    }

    public static void  getSoundWithPositionV(Level level, Vec3 position, SoundEvent audio, float volume, float pitch){
        level.playSound(null, position.x, position.y, position.z, audio, SoundSource.PLAYERS, volume, pitch) ;
    }

    public static void getLocalSound(Level level, BlockPos position, SoundEvent audio, float volume, float pitch){
        level.playLocalSound(position.getX(), position.getY(), position.getZ(), audio, SoundSource.BLOCKS,volume, pitch, false);
    }

    public static ItemStack getUsedItem(LivingEntity player){
        return player.getItemInHand(player.getUsedItemHand());
    }


    private static double getX(Vec3 position, double size, double scale) {
        return position.x + size * scale;
    }

    public static double getRandomX(Vec3 position, double size,double scale) {
        return getX(position,size, ((double)2.0F * Random.nextDouble() - (double)1.0F) * scale);
    }

    private static double getY(Vec3 position, double size, double scale) {
        return position.y + size * scale;
    }

    public static  double getRandomY(Vec3 position, double size) {
        return getY(position, size, Random.nextDouble());
    }

    private static double getZ(Vec3 position, double size,double scale) {
        return position.z + size * scale;
    }


    public static double getAttributeValue(Player player, Holder<Attribute> attribute){
        var attributes = player.getAttribute(attribute);
        return attributes != null ? attributes.getValue() : -1;
    }

    public static void addTransientAttribute(Player player, double value, String id, Holder<Attribute> attributeHolder) {
        var modifier = new AttributeModifier(JahdooHelpers.res(id), value, AttributeModifier.Operation.ADD_VALUE);
        Multimap<Holder<Attribute>, AttributeModifier> multiMap = HashMultimap.create();
        multiMap.put(attributeHolder, modifier);
        player.getAttributes().addTransientAttributeModifiers(multiMap);
    }

    public static List<Component> filterList(List<Component> collection, String... item){
        var filter = Set.of(item);
        return collection.stream()
            .filter(component -> filter.stream().anyMatch(component.getString()::contains))
            .toList();
    }

    public static ResourceLocation res(String location) {
        return ResourceLocation.fromNamespaceAndPath(JahdooMod.MOD_ID, location);
    }

    public static ResourceLocation texture(String location) {
        return ResourceLocation.fromNamespaceAndPath(JahdooMod.MOD_ID, "textures/" + location + ".png");
    }

    public static void sendClientSound(ServerPlayer serverPlayer, SoundEvent soundEvent, float volume, float pitch){
        sendToPlayer(serverPlayer, new ClientSoundS2CP(soundEvent, volume, pitch, false));
    }

    public static void sendClientSound(ServerPlayer serverPlayer, SoundEvent soundEvent, float volume, float pitch, boolean isLooping){
        sendToPlayer(serverPlayer, new ClientSoundS2CP(soundEvent, volume, pitch, isLooping));
    }

    public static Map<String, AbilityData.AbilityModifiers> getModifierValue(Player player, String abilityName) {
        var data = CasterData.entityHolder(player, abilityName);
        return data != null ? data.data().abilityProperties(): emptyMap();
    }

    public static int getColourDarker(int color, double darkValue) {
        // Extract ARGB components from the integer
        int alpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        red = Math.min((int) (red / darkValue), 255);
        green = Math.min((int) (green / darkValue), 255);
        blue = Math.min((int) (blue / darkValue), 255);

        // Combine the components back into an integer
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static int getColourLight(int color, double lightValue) {
        // Extract ARGB components from the integer
        int alpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        red = Math.min((int) (red * lightValue), 255);
        green = Math.min((int) (green * lightValue), 255);
        blue = Math.min((int) (blue * lightValue), 255);

        // Combine the components back into an integer
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static ListTag nbtDoubleList(double... pNumbers) {
        ListTag listtag = new ListTag();
        for(double d0 : pNumbers) listtag.add(DoubleTag.valueOf(d0));
        return listtag;
    }

    public static void playDebugMessage(Player player, Object... info){
        var randomColour = ColourHelpers.getRgb();
        player.sendSystemMessage(Component.literal(Arrays.toString(info)).withStyle(style -> style.withColor(randomColour)));
    }

    public static void sendPacketsToPlayerDistance(Vec3 pos, int distance, Level level, Consumer<ServerPlayer> serverPlayerConsumer) {
        if((level instanceof ServerLevel serverLevel)){
            for (int j = 0; j < serverLevel.players().size(); ++j) {
                var serverplayer = serverLevel.players().get(j);
                if (pos.closerThan(serverplayer.position(), distance)) {
                    serverPlayerConsumer.accept(serverplayer);
                }
            }
        }
    }

    public static void sendEffectPacketsToPlayerDistance(Vec3 pos, int distance, Level level, int entityId, MobEffectInstance effectInstance) {
        if((level instanceof ServerLevel serverLevel)){
            for (int j = 0; j < serverLevel.players().size(); ++j) {
                var serverplayer = serverLevel.players().get(j);
                if (pos.closerThan(serverplayer.position(), distance)) {
                    serverplayer.connection.send(new ClientboundUpdateMobEffectPacket(entityId, effectInstance, true));
                }
            }
        }
    }

    public static void sendEffectPacketsToPlayer(Level level, int entityId, MobEffectInstance effectInstance) {
        if((level instanceof ServerLevel serverLevel)){
            for (int j = 0; j < serverLevel.players().size(); ++j) {
                var serverplayer = serverLevel.players().get(j);
                serverplayer.connection.send(new ClientboundUpdateMobEffectPacket(entityId, effectInstance, true));
            }
        }
    }

    public static ParticleOptions getRandomColouredParticle(int colourA, int colourB, int lifetime, float size, boolean staticSize){
        var generic = genericParticle(ParticleStore.GENERIC_PARTICLE, colourA, colourB,lifetime,size, staticSize, size);
        var magic = genericParticle(ParticleStore.MAGIC_PARTICLE, colourA, colourB,lifetime,size, staticSize, size);
        var soft = genericParticle(SOFT_PARTICLE, colourA, colourB,lifetime,size, staticSize, size);
        var collectTypes = List.of(generic, magic, soft);
        return collectTypes.get(Random.nextInt(collectTypes.size()));
    }

    public static int stackDurability(ItemStack itemStack){
        return itemStack.getItem().getMaxDamage(itemStack) - itemStack.getItem().getDamage(itemStack);
    }

    public static void setDurability (ItemStack itemStack, int maxDamage) {
        itemStack.set(DataComponents.MAX_DAMAGE, maxDamage);
        itemStack.set(DataComponents.MAX_STACK_SIZE, 1);
        itemStack.set(DataComponents.DAMAGE, 0);
    }

    public static void debugComponent(ItemStack itemStack, Player player){
        player.sendSystemMessage(Component.literal("New Request"));
        player.sendSystemMessage(Component.literal("-----------------------------------------------------"));
        for (TypedDataComponent<?> component : itemStack.getComponents()) {
            player.sendSystemMessage(TextHelpers.withStyleComponent(component.toString(), ColourHelpers.getRgb()));
            player.sendSystemMessage(Component.literal(" "));
        }
        player.sendSystemMessage(Component.literal("-----------------------------------------------------"));
        player.sendSystemMessage(Component.literal(" "));
    }

    public static Vec3 getRandomParticleVelocity(Entity entity, double speed) {
        var theta = Random.nextDouble() * 2 * Math.PI; // Angle around the y-axis
        var phi = Random.nextDouble() * Math.PI; // Angle from the y-axis
        var x = Math.sin(phi) * Math.cos(theta);
        var y = Math.cos(phi);
        var z = Math.sin(phi) * Math.sin(theta);

        return new Vec3(x, y, z).normalize().scale(speed);
    }

    public static MutableComponent highlightTextComponent(
        Level level,
        String text,
        int c1,
        int c2,
        double speed,
        double dura
    ) {
        var component = Component.empty();
        var split = text.split("");
        var nonSpaceIndices = new ArrayList<Integer>();
        for (var i = 0; i < split.length; i++) {
            if (!split[i].equals(" ")) nonSpaceIndices.add(i);
        }

        var totalCycleLength = (nonSpaceIndices.size() * speed) + dura;
        if (level != null) {
            var cyclePosition = level.getGameTime() % totalCycleLength;
            var isHighlighting = cyclePosition < (long) nonSpaceIndices.size() * speed;
            var highlightNonSpaceIndex = isHighlighting ? (int) Math.floor(cyclePosition / speed) : -1;
            var highlightIndex = highlightNonSpaceIndex >= 0 ? nonSpaceIndices.get(highlightNonSpaceIndex) : -1;

            for (var i = 0; i < split.length; i++) {
                var colour = i == highlightIndex ? c2 : c1;
                component.append(TextHelpers.withStyleComponent(split[i], colour));
            }
        }
        return component;
    }

    public static int repairDurability(ItemStack itemStack){
        var maxDamage = itemStack.get(DataComponents.MAX_DAMAGE);
        itemStack.set(DataComponents.DAMAGE, 0);
        return 0;
    }

    public static int durabilityDamageCount(ItemStack itemStack){
        var maxDamage = itemStack.get(DataComponents.MAX_DAMAGE);
        var damageTaken = itemStack.get(DataComponents.DAMAGE);
        if(maxDamage != null && damageTaken != null){
            return maxDamage - damageTaken;
        }
        return 0;
    }

    public static void hurtAndKeepItem(ItemStack itemStack, int damage, ServerLevel level, LivingEntity livingEntity) {
        hurtAndKeepItemChanced(itemStack, damage, level, livingEntity, 10);
    }

    public static void hurtAndKeepItemChanced(ItemStack itemStack, int damage, Level level, LivingEntity livingEntity, int chance) {
        var damageChance = Random.nextInt(chance) == 0;
        if (damageChance && itemStack.isDamageableItem()) {
            damage = itemStack.getItem().damageItem(itemStack, damage, livingEntity, (item) -> { });

            if (damage > 0) {
                if(level instanceof ServerLevel serverLevel)
                    damage = processDurabilityChange(serverLevel, itemStack, damage);

                if (damage <= 0) return;
            }

            if (livingEntity instanceof ServerPlayer sp) {
                if (damage != 0) ITEM_DURABILITY_CHANGED.trigger(sp, itemStack, itemStack.getDamageValue() + damage);
            }

            var i = itemStack.getDamageValue() + damage;
            itemStack.setDamageValue(i);

            if (stackDurability(itemStack) == 0) {
                JahdooHelpers.getSoundWithPositionV(level, livingEntity.position(), ITEM_BREAK, 1, 1);
            }
        }
    }

    public static int getColorTransition(int startColor, int endColor, int ticker, double transitionDelay) {
        var startRed = (startColor >> 16) & 0xFF;
        var startGreen = (startColor >> 8) & 0xFF;
        var startBlue = startColor & 0xFF;
        var endRed = (endColor >> 16) & 0xFF;
        var endGreen = (endColor >> 8) & 0xFF;
        var endBlue = endColor & 0xFF;
        var progress = 0.5 * (1.0 + Math.sin(2 * Math.PI * ticker / transitionDelay));
        var red = (int) (startRed + (endRed - startRed) * progress);
        var green = (int) (startGreen + (endGreen - startGreen) * progress);
        var blue = (int) (startBlue + (endBlue - startBlue) * progress);

        red = Math.min(Math.max(red, 0), 255);
        green = Math.min(Math.max(green, 0), 255);
        blue = Math.min(Math.max(blue, 0), 255);

        return (red << 16) | (green << 8) | blue;
    }

    @SafeVarargs
    public static float attributeModifierCalculator(
        LivingEntity player,
        float initialValue,
        boolean isAddition,
        Holder<Attribute> ... attribute
    ){
        var abilityName = player.getData(AttachmentReg.CASTER_DATA.get());
        float getAttribute = 0;
        var getAbility = AbilityReg.getFirstSpellByTypeId(abilityName.getSelectedAbility());
        if(getAbility.isEmpty()) return initialValue;


        var reCalculatedDamage = initialValue;

        for (var attributeHolder : attribute) {
            var attributes = player.getAttributes();
            if(attributes.hasAttribute(attributeHolder)) {
                var value = (float) attributes.getValue(attributeHolder);
                getAttribute += value;
            }
        }

        float getPercentageDamage = (float) MathHelpers.getPercentage(initialValue, getAttribute);

        if(isAddition){
            reCalculatedDamage = reCalculatedDamage + getPercentageDamage;
        } else {
            reCalculatedDamage = reCalculatedDamage - getPercentageDamage;
        }

        return reCalculatedDamage;
    }


    public static Biome newBiome(){
        return new Biome.BiomeBuilder()
            .hasPrecipitation(true)
            .temperature(0.7F)
            .downfall(0.8F)
            .specialEffects(
                new BiomeSpecialEffects.Builder()
                    .waterColor(4159204)
                    .waterFogColor(329011)
                    .fogColor(ColourHelpers.getPerkGreen())
                    .skyColor(calculateSkyColor(0.7F))
                    .grassColorModifier(BiomeSpecialEffects.GrassColorModifier.DARK_FOREST)
                    .ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
                    .ambientParticle(new AmbientParticleSettings(ParticleTypes.SPORE_BLOSSOM_AIR, 1.118093334F))
                    .build()
            )
            .mobSpawnSettings(MobSpawnSettings.EMPTY)
            .generationSettings(BiomeGenerationSettings.EMPTY)
            .build();
    }

    static int calculateSkyColor(float temperature) {
        float $$1 = temperature / 3.0F;
        $$1 = Mth.clamp($$1, -1.0F, 1.0F);
        return Mth.hsvToRgb(0.62222224F - $$1 * 0.05F, 0.5F + $$1 * 0.1F, 1.0F);
    }

    public static GenericParticleOptions processingParticle(
        int lifetime,
        float size,
        boolean staticSize,
        double speed
    ) {
        return genericParticle(SOFT_PARTICLE, utility(), lifetime, size, staticSize, speed);
    }

    public static void spawnAllTestEntities(Player player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) return;

        BlockPos origin = player.blockPosition().offset(0, 0, 6);

        int spacing = 8;      // blocks between entities
        int perRow = 4;       // grid width

        int index = 0;

        for (DeferredHolder<EntityType<?>, ? extends EntityType<? extends Entity>> supplier : SPAWN_TEST_ENTITIES) {
            EntityType<? extends Entity> type = supplier.get();

            Entity entity = type.create(serverLevel);
            if (entity == null) continue;

            int xOffset = (index % perRow) * spacing;
            int zOffset = (index / perRow) * spacing;

            entity.moveTo(
                origin.getX() + xOffset + 0.5,
                origin.getY(),
                origin.getZ() + zOffset + 0.5,
                player.getYRot(),
                0
            );

            if (entity instanceof Mob mob) {
                mob.setNoAi(true);
                mob.setPersistenceRequired();
            }

            serverLevel.addFreshEntity(entity);
            index++;
        }
    }

    public static final List<DeferredHolder<EntityType<?>, ? extends EntityType<? extends Entity>>> SPAWN_TEST_ENTITIES = List.of(

        // ===== BOSSES / LARGE MOBS =====
        ENDER_GOLEM,
        ENDER_GUARDIAN,
        NETHERITE_MONSTROSITY,
        IGNIS,
        THE_HARBINGER,
        THE_PROWLER,
        THE_LEVIATHAN,
        ANCIENT_REMNANT,
        MALEDICTUS,
        CLAWDIAN,
        SCYLLA,
        WADJET,
        KOBOLEDIATOR,
        APTRGANGR,

        // ===== MEDIUM MOBS =====
        CORAL_GOLEM,
        CORALSSUS,
        IGNITED_REVENANT,
        IGNITED_BERSERKER,
        AMETHYST_CRAB,
        HIPPOCAMTUS,
        CINDARIA,
        DRAUGR,
        ROYAL_DRAUGR,
        ELITE_DRAUGR,
        DEEPLING_BRUTE,
        DEEPLING_ANGLER,
        DEEPLING_PRIEST,
        DEEPLING_WARLOCK,

        // ===== SMALL / NORMAL MOBS =====
        DEEPLING,
        ENDERMAPTERA,
        LIONFISH,
        URCHINKIN,
        KOBOLETON,
        THE_WATCHER,
        SYMBIOCTO,
        DROWNED_HOST,
        MODERN_REMNANT,

        // ===== CREATURES / PASSIVES =====
        NETHERITE_MINISTROSITY,
        THE_BABY_LEVIATHAN,

        // ===== SOLID MISC ENTITIES (SAFE TO SPAWN) =====
        VOID_RUNE,
        ABYSS_MINE,
        CM_FALLING_BLOCK,
        VOID_VORTEX,
        DIMENSIONAL_RIFT,
        ABYSS_PORTAL,
        ABYSS_BLAST_PORTAL,
        ACCRETION,
        EYE_OF_DUNGEON,
        SANDSTORM,
        CURSED_SANDSTORM,
        ANCIENT_DESERT_STELE,
        WITHER_SMOKE_EFFECT,
        LIGHTNING_AREA_EFFECT,
        FLAME_STRIKE,
        EARTHQUAKE
    );
}
