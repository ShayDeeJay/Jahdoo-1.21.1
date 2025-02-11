package org.jahdoo.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.JahdooMod;
import org.jahdoo.components.ability_holder.AbilityHolder;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.networking.packet.server2client.PlayClientSoundSyncS2CPacket;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.particle.ParticleStore;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.DataComponentRegistry;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;

public class ModHelpers {
    public static final Random Random = ThreadLocalRandom.current();

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

    public static int getColorTransition(int startColor, int endColor, int ticker, double transitionDelay) {
        // Extract RGB components from the start color
        int startRed = (startColor >> 16) & 0xFF;
        int startGreen = (startColor >> 8) & 0xFF;
        int startBlue = startColor & 0xFF;

        // Extract RGB components from the end color
        int endRed = (endColor >> 16) & 0xFF;
        int endGreen = (endColor >> 8) & 0xFF;
        int endBlue = endColor & 0xFF;

        // Calculate progress using a sinusoidal function
        double progress = 0.5 * (1.0 + Math.sin(2 * Math.PI * ticker / transitionDelay));

        // Interpolate each RGB component
        int red = (int) (startRed + (endRed - startRed) * progress);
        int green = (int) (startGreen + (endGreen - startGreen) * progress);
        int blue = (int) (startBlue + (endBlue - startBlue) * progress);

        // Ensure the values are within the valid range [0, 255]
        red = Math.min(Math.max(red, 0), 255);
        green = Math.min(Math.max(green, 0), 255);
        blue = Math.min(Math.max(blue, 0), 255);

        // Pack the interpolated RGB components into an integer
        return (red << 16) | (green << 8) | blue;
    }

    public static double getAttributeValue(Player player, Holder<Attribute> attribute){
        var attributes = player.getAttribute(attribute);
        return attributes != null ? attributes.getValue() : -1;
    }

    public static void addTransientAttribute(Player player, double value, String id, Holder<Attribute> attributeHolder) {
        var modifier = new AttributeModifier(ModHelpers.res(id), value, AttributeModifier.Operation.ADD_VALUE);
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

    public static <T> T getRandomListElement(List<T> collection){
        var index = !collection.isEmpty() ? Random.nextInt(collection.size()) : 0 ;
        return collection.get(index);
    }

    public static void sendClientSound(ServerPlayer serverPlayer, SoundEvent soundEvent, float volume, float pitch){
        PacketDistributor.sendToPlayer(serverPlayer, new PlayClientSoundSyncS2CPacket(soundEvent, volume, pitch, true));
    }

    public static void sendClientSound(ServerPlayer serverPlayer, SoundEvent soundEvent, float volume, float pitch, boolean isBatched){
        PacketDistributor.sendToPlayer(serverPlayer, new PlayClientSoundSyncS2CPacket(soundEvent, volume, pitch, isBatched));
    }

    public static double getTag(Player player, String name, String abilityName) {
        if(player != null){
            var wandAbilityHolder = WandAbilityHolder.getHolderFromWand(player);
            var holder = ModHelpers.getModifierValue(wandAbilityHolder, abilityName).get(name);
            if(holder != null) return holder.setValue();
        }
        return 0;
    }

    public static Map<String, AbilityHolder.AbilityModifiers> getModifierValue(WandAbilityHolder wandAbilityHolder, String tagName) {
        if(wandAbilityHolder != null){
            var allModifiers = wandAbilityHolder.abilityProperties();
            if (allModifiers != null) {
                if(allModifiers.get(tagName) != null){
                    return allModifiers.get(tagName).abilityProperties();
                }
            }
        }
        return Collections.emptyMap();
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
        level.playLocalSound(position.getX(), position.getY(), position.getZ(), audio, SoundSource.BLOCKS,volume, pitch, false); ;
    }

    public static void sendPacketsToPlayer(Level level, CustomPacketPayload payloads) {
        if((level instanceof ServerLevel serverLevel)){
            for (int j = 0; j < serverLevel.players().size(); ++j) {
                var serverplayer = serverLevel.players().get(j);
                PacketDistributor.sendToPlayer(serverplayer, payloads);
            }
        }
    }

    public static void sendPacketsToPlayerDistance(Vec3 pos, int distance, Level level, CustomPacketPayload payloads) {
        if((level instanceof ServerLevel serverLevel)){
            for (int j = 0; j < serverLevel.players().size(); ++j) {
                var serverplayer = serverLevel.players().get(j);
                if (pos.closerThan(serverplayer.position(), distance)) {
                    PacketDistributor.sendToPlayer(serverplayer, payloads);
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

    public static ParticleOptions getRandomColouredParticle(int colourA, int colourB, int lifetime, float size, boolean staticSize){
        var generic = ParticleHandlers.genericParticleOptions(ParticleStore.GENERIC_PARTICLE_SELECTION, colourA, colourB,lifetime,size, staticSize, size);
        var magic = ParticleHandlers.genericParticleOptions(ParticleStore.MAGIC_PARTICLE_SELECTION, colourA, colourB,lifetime,size, staticSize, size);
        var soft = ParticleHandlers.genericParticleOptions(ParticleStore.SOFT_PARTICLE_SELECTION, colourA, colourB,lifetime,size, staticSize, size);
        var collectTypes = List.of(generic, magic, soft);
        return collectTypes.get(Random.nextInt(collectTypes.size()));
    }

    public static int stackDurability(ItemStack itemStack){
        return itemStack.getItem().getMaxDamage(itemStack) - itemStack.getItem().getDamage(itemStack);
    }

    public static void hurtAndKeepItem(ItemStack itemStack, int damage, Level level, LivingEntity livingEntity) {
        var damageChance = Random.nextInt(10) == 0;
        if (damageChance && itemStack.isDamageableItem()) {
            damage = itemStack.getItem().damageItem(itemStack, damage, livingEntity, (item) -> { });

            if (damage > 0) {
                if(level instanceof ServerLevel serverLevel){
                    damage = EnchantmentHelper.processDurabilityChange(serverLevel, itemStack, damage);
                }

                if (damage <= 0) return;
            }

            if (livingEntity instanceof ServerPlayer sp) {
                if (damage != 0) {
                    CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(sp, itemStack, itemStack.getDamageValue() + damage);
                }
            }

            var i = itemStack.getDamageValue() + damage;
            itemStack.setDamageValue(i);

            if (stackDurability(itemStack) == 0) {
                livingEntity.playSound(SoundEvents.ITEM_BREAK);
            }
        }
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

    @SafeVarargs
    public static float attributeModifierCalculator(
        LivingEntity player,
        float initialValue,
        boolean isAddition,
        Holder<Attribute> ... attribute
    ){
        var wandItem = ModHelpers.getUsedItem(player);
        var abilityName = wandItem.get(DataComponentRegistry.WAND_DATA.get());
        if(abilityName == null) return initialValue;
        float getAttribute = 0;
        var getAbility = AbilityRegister.getFirstSpellByTypeId(abilityName.selectedAbility());
        if(getAbility.isEmpty()) return initialValue;

        var reCalculatedDamage = initialValue;

        for (var attributeHolder : attribute) {
            getAttribute += (float) player.getAttributes().getValue(attributeHolder);
        }

        float getPercentageDamage = (float) Maths.getPercentage(initialValue, getAttribute);

        if(isAddition){
            reCalculatedDamage = reCalculatedDamage + getPercentageDamage;
        } else {
            reCalculatedDamage = reCalculatedDamage - getPercentageDamage;
        }

        return reCalculatedDamage;
    }

    public static Vec3 getRandomParticleVelocity(Entity entity, double speed) {
        double theta = Random.nextDouble() * 2 * Math.PI; // Angle around the y-axis
        double phi = Random.nextDouble() * Math.PI; // Angle from the y-axis

        // Convert spherical coordinates to Cartesian coordinates
        double x = Math.sin(phi) * Math.cos(theta);
        double y = Math.cos(phi);
        double z = Math.sin(phi) * Math.sin(theta);

        // Scale the velocity vector by the desired speed
        return new Vec3(x, y, z).normalize().scale(speed);
    }

    public static String stringIdToName(String input) {
        String[] words = input.split("_");
        StringBuilder result = new StringBuilder();
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

}
