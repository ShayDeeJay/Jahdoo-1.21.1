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
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.JahdooMod;
import org.jahdoo.common.components.AbilityData;
import org.jahdoo.common.components.AbilityHolder;
import org.jahdoo.common.networking.client2server.AbilityHolderC2SP;
import org.jahdoo.common.networking.client2server.PlayerTrialDataC2SP;
import org.jahdoo.common.networking.client2server.SelectAbilityC2SP;
import org.jahdoo.common.networking.server2client.*;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.mod.AbilityReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.attachments.PlayerTrialData;
import org.jahdoo.trial_nexus.attachments.QuestTracker;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static java.util.Collections.emptyMap;
import static net.minecraft.advancements.CriteriaTriggers.ITEM_DURABILITY_CHANGED;
import static net.minecraft.sounds.SoundEvents.ITEM_BREAK;
import static net.minecraft.world.item.enchantment.EnchantmentHelper.processDurabilityChange;
import static net.neoforged.neoforge.network.PacketDistributor.sendToPlayer;
import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.common.registers.AttachmentReg.PLAYER_WALLET_DATA;
import static org.jahdoo.trial_nexus.utils.ColourStore.*;

public class Helpers {
    public static final String EASY = "novice";
    public static final String MEDIUM = "expert";
    public static final String HARD = "master";

    public static final Random Random = ThreadLocalRandom.current();

    public static void syncAbilities(){
        sendToServer(new AbilityHolderC2SP(AbilityHolder.DEFAULT, 0));
    }

    public static void syncPlayerTrialData(int index){
        sendToServer(new PlayerTrialDataC2SP(index));
    }

    public static void syncSelectedAbility(Player player, String updateAbility) {
        player.getData(AttachmentReg.CASTER_DATA).setSelectedAbility(updateAbility);
        sendToServer(new SelectAbilityC2SP(updateAbility));
    }

    public static void syncAbilitiesServer(Entity player) {
        if(player instanceof ServerPlayer serverPlayer){
            var casterData = serverPlayer.getData(AttachmentReg.CASTER_DATA);
            sendToPlayer(serverPlayer, new AbilityHolderS2CP(casterData.getUnlockedAbilities()));
        }
    }

    public static void syncClientData(Entity player) {
        if(player instanceof ServerPlayer serverPlayer){
            var casterData = serverPlayer.getData(AttachmentReg.CASTER_DATA);
            var wallet = player.getData(PLAYER_WALLET_DATA).getWallet();
            sendToPlayer(serverPlayer, new WalletSyncS2CP(wallet));
            sendToPlayer(serverPlayer, new CastingDataSyncS2CP(casterData));
            sendToPlayer(serverPlayer, new QuestTrackerS2CP(QuestTracker.getQuestTracker(serverPlayer)));
            PlayerTrialData.updateClientData(serverPlayer);
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

    public static String capitaliseFirst(String name){
        return name.substring(0,1).toUpperCase() + name.substring(1);
    }

    public static void getSoundWithPosition(Level level, BlockPos position, SoundEvent audio){
        level.playSound(null, position.getX(), position.getY(), position.getZ(), audio, SoundSource.BLOCKS,1,1) ;
    }

    public static void getSoundWithPosition(Level level, BlockPos position, SoundEvent audio, float volume){
        level.playSound(null, position.getX(), position.getY(), position.getZ(), audio, SoundSource.BLOCKS,volume,1) ;
    }

    public static void  getSoundWithPosition(Level level, BlockPos position, SoundEvent audio, float volume, float pitch){
        level.playSound(null, position.getX(), position.getY(), position.getZ(), audio, SoundSource.BLOCKS,volume, pitch) ;
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

    public static Color getCyclicColorVariant(int baseColor, int ticker, double range, double transitionDelay) {
        int red = (baseColor >> 16) & 0xFF;
        int green = (baseColor >> 8) & 0xFF;
        int blue = baseColor & 0xFF;

        double phase = Math.sin(ticker / transitionDelay);
        int variantRed = (int) Math.min(Math.max(red + phase * range, 0.0), 255.0);
        int variantGreen = (int) Math.min(Math.max(green + phase * range, 0.0), 255.0);
        int variantBlue = (int) Math.min(Math.max(blue + phase * range, 0.0), 255.0);

        return new Color(variantRed, variantGreen, variantBlue);
    }

    public static double getAttributeValue(Player player, Holder<Attribute> attribute){
        var attributes = player.getAttribute(attribute);
        return attributes != null ? attributes.getValue() : -1;
    }

    public static void addTransientAttribute(Player player, double value, String id, Holder<Attribute> attributeHolder) {
        var modifier = new AttributeModifier(Helpers.res(id), value, AttributeModifier.Operation.ADD_VALUE);
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

    public static <T> T listRandom(List<T> collection){
        var index = collection.size() > 1 ? Random.nextInt(collection.size()) : 0 ;
        return collection.get(index);
    }

    public static <T> T listRandom(List<T> collection, long seed){
        var index = collection.size() > 1 ? new Random(seed).nextInt(collection.size()) : 0 ;
        return collection.get(index);
    }

    public static void sendClientSound(ServerPlayer serverPlayer, SoundEvent soundEvent, float volume, float pitch){
        sendToPlayer(serverPlayer, new ClientSoundS2CP(soundEvent, volume, pitch, true));
    }

    public static void sendClientSound(ServerPlayer serverPlayer, SoundEvent soundEvent, float volume, float pitch, boolean isBatched){
        sendToPlayer(serverPlayer, new ClientSoundS2CP(soundEvent, volume, pitch, isBatched));
    }

    public static Map<String, AbilityData.AbilityModifiers> getModifierValue(AbilityHolder abilityHolder, String tagName) {
        if(abilityHolder != null){
            var allModifiers = abilityHolder.data().abilityProperties();

            if(allModifiers.get(tagName) != null) return allModifiers;
        }
        return emptyMap();
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

    public static Component withStyleComponent(String text, int colour){
        return Component.literal(text).withStyle(style -> style.withColor(colour));
    }

    public static Component withStyleComponentTrans(String text, int colour, Object... args){
        return Component.translatable(text,args).withStyle(style -> style.withColor(colour));
    }

    public static int colourByPercent(int targetNumber, int currentNumber, boolean reversed) {
        var split = targetNumber / 3;
        return currentNumber <= split ? reversed ? NEGATIVE_RED : PERK_GREEN : currentNumber <= split * 2.5 ? ABSORPTION_YELLOW : reversed ? PERK_GREEN : NEGATIVE_RED;
    }

    public static void playDebugMessage(Player player, Object... info){
        var randomColour = getRgb();
        player.sendSystemMessage(Component.literal(Arrays.toString(info)).withStyle(style -> style.withColor(randomColour)));
    }

    public static int getRgb() {
        return new Color((int) (Math.random() * 0x1000000)).getRGB();
    }

    public static void playDebugMessageComp(Player player, String... info){
        var randomColour = getRgb();
        for (String o : Arrays.stream(info).toList()) {
            player.sendSystemMessage(withStyleComponentTrans(o,randomColour));
        }
    }

    public static boolean canPathfindToTarget(Mob finder, LivingEntity target) {
        Path path = finder.getNavigation().createPath(target, 0);
        return path != null /*&& path.getDistToTarget() < distance*/;
    }

    public static void sendPacketsToPlayer(Level level, CustomPacketPayload payloads) {
        if((level instanceof ServerLevel serverLevel)){
            for (int j = 0; j < serverLevel.players().size(); ++j) {
                var serverplayer = serverLevel.players().get(j);
                sendToPlayer(serverplayer, payloads);
            }
        }
    }

    public static void sendPacketsToPlayerDistance(Vec3 pos, int distance, Level level, CustomPacketPayload payloads) {
        if((level instanceof ServerLevel serverLevel)){
            for (int j = 0; j < serverLevel.players().size(); ++j) {
                var serverplayer = serverLevel.players().get(j);
                if (pos.closerThan(serverplayer.position(), distance)) {
                    sendToPlayer(serverplayer, payloads);
                }
            }
        }
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
        var generic = ParticleHandlers.genericParticle(ParticleStore.GENERIC_PARTICLE, colourA, colourB,lifetime,size, staticSize, size);
        var magic = ParticleHandlers.genericParticle(ParticleStore.MAGIC_PARTICLE, colourA, colourB,lifetime,size, staticSize, size);
        var soft = ParticleHandlers.genericParticle(ParticleStore.SOFT_PARTICLE, colourA, colourB,lifetime,size, staticSize, size);
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
            player.sendSystemMessage(withStyleComponent(component.toString(), getRgb()));
            player.sendSystemMessage(Component.literal(" "));
        }
        player.sendSystemMessage(Component.literal("-----------------------------------------------------"));
        player.sendSystemMessage(Component.literal(" "));
    }

    public static boolean hasLineOfSight(Entity pathfinder, Entity target) {
        if (target.level() != pathfinder.level()) {
            return false;
        } else {
            var vec3 = new Vec3(pathfinder.getX(), pathfinder.getEyeY(), pathfinder.getZ());
            var vec31 = new Vec3(target.getX(), target.getEyeY(), target.getZ());
            var context = new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, pathfinder);
            return !(vec31.distanceTo(vec3) > (double) 128.0F) && pathfinder.level().clip(context).getType() == HitResult.Type.MISS;
        }
    }

    public static boolean hasLineOfSightPos(Entity pathfinder, Vec3 target) {
        var vec3 = new Vec3(pathfinder.getX(), pathfinder.getEyeY(), pathfinder.getZ());
        var vec31 = new Vec3(target.x, target.y, target.z);
        var context = new ClipContext(vec3, vec31, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, pathfinder);
        return !(vec31.distanceTo(vec3) > (double) 128.0F) && pathfinder.level().clip(context).getType() == HitResult.Type.MISS;
    }

    public static Vec3 getRandomParticleVelocity(Entity entity, double speed) {
        var theta = Random.nextDouble() * 2 * Math.PI; // Angle around the y-axis
        var phi = Random.nextDouble() * Math.PI; // Angle from the y-axis

        // Convert spherical coordinates to Cartesian coordinates
        var x = Math.sin(phi) * Math.cos(theta);
        var y = Math.cos(phi);
        var z = Math.sin(phi) * Math.sin(theta);

        // Scale the velocity vector by the desired speed
        return new Vec3(x, y, z).normalize().scale(speed);
    }

    public static String stringIdToName(String input) {
        if(input == null) return "";
        var words = input.split("_");
        var result = new StringBuilder();
        for (String word : words) {
            word = word.trim();
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
            }
        }
        return result.toString().trim();
    }

    public static String nameToId(String input) {
        var lowercaseId = new StringBuilder();
        for (var s : input.split(" ")) {
            lowercaseId.append(s.toLowerCase()).append("_");
        }
        lowercaseId.deleteCharAt(lowercaseId.length()-1);
        return lowercaseId.toString();
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
//        var holderColour = ColourStore.CHAMPION_GOLD;
//        component.append(withStyleComponent("❖ ", holderColour));
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
                component.append(withStyleComponent(split[i], colour));
            }
        }
//        component.append(withStyleComponent(" ❖", holderColour));
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
                Helpers.getSoundWithPositionV(level, livingEntity.position(), ITEM_BREAK, 1, 1);
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

        float getPercentageDamage = (float) Maths.getPercentage(initialValue, getAttribute);

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
                    .fogColor(ColourStore.PERK_GREEN)
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

    public static void throwOrAddItem(Player player, ItemStack newItem){
        var isValidSlot = player.getInventory().getFreeSlot() != -1;
        if(isValidSlot) player.addItem(newItem); else throwNewItem(player, newItem);
    }

    public static void throwNewItem(LivingEntity livingEntity, ItemStack itemStack){
        var offsetX = -Math.sin(Math.toRadians(livingEntity.yRotO)) * 2;
        var offsetZ = Math.cos(Math.toRadians(livingEntity.yRotO)) * 2;
        var spawnX = livingEntity.getX() + offsetX;
        var spawnY = livingEntity.getY() + livingEntity.getEyeHeight() -0.7 ; // No vertical offset
        var spawnZ = livingEntity.getZ() + offsetZ;
        BehaviorUtils.throwItem(livingEntity, itemStack, new Vec3(spawnX, spawnY, spawnZ));
    }

    public static void throwItem(LivingEntity livingEntity, ItemStack stack) {
        // Calculate spawn position in front of the entity
        var yaw = Math.toRadians(livingEntity.yRotO);
        var offsetX = -Math.sin(yaw) * 0.5;
        var offsetZ = Math.cos(yaw) * 0.5;

        var spawnX = livingEntity.getX() + offsetX;
        var spawnY = livingEntity.getY() + 0.5;
        var spawnZ = livingEntity.getZ() + offsetZ;

        var spawnPos = new Vec3(spawnX, spawnY, spawnZ);
        var x = 0.4;
        var speedMultiplier = new Vec3(x, x, x); // Tweak as needed

        throwItem(livingEntity.position(), stack, spawnPos, speedMultiplier, livingEntity.level());
    }

    public static void throwItem(Vec3 pos, ItemStack stack, Vec3 offset, Vec3 speedMultiplier, Level level) {
        var itementity = new ItemEntity(level, pos.x, pos.y, pos.z, stack);
        var direction = offset.subtract(pos).normalize();
        var v = 0.4;
        var randX = (Random.nextDouble() - 0.5) * v;
        var randY = (Random.nextDouble() - 0.5) * v;
        var randZ = (Random.nextDouble() - 0.5) * v;

        var finalVelocity = new Vec3(
            (direction.x + randX) * speedMultiplier.x,
            (direction.y + randY) * speedMultiplier.y,
            (direction.z + randZ) * speedMultiplier.z
        );

        itementity.setDeltaMovement(finalVelocity);
        itementity.setDefaultPickUpDelay();
        level.addFreshEntity(itementity);
    }
}
