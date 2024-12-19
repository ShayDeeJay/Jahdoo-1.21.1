package org.jahdoo.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jahdoo.JahdooMod;
import org.jahdoo.components.ability_holder.AbilityHolder;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.networking.packet.server2client.PlayClientSoundSyncS2CPacket;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.DataComponentRegistry;

import java.awt.*;
import java.text.DecimalFormat;
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

    public static ResourceLocation res(String location) {
        return ResourceLocation.fromNamespaceAndPath(JahdooMod.MOD_ID, location);
    }

    public static <T> T getRandomListElement(List<T> collection){
        Collections.shuffle(collection);
        return collection.getFirst();
    }

    public static void sendClientSound(ServerPlayer serverPlayer, SoundEvent soundEvent, float volume, float pitch){
        PacketDistributor.sendToPlayer(serverPlayer, new PlayClientSoundSyncS2CPacket(soundEvent, volume, pitch, true));
    }

    public static void sendClientSound(ServerPlayer serverPlayer, SoundEvent soundEvent, float volume, float pitch, boolean isBatched){
        PacketDistributor.sendToPlayer(serverPlayer, new PlayClientSoundSyncS2CPacket(soundEvent, volume, pitch, isBatched));
    }

    public static double getTag(Player player, String name, String abilityName) {
        if(player != null){
            var wandAbilityHolder = player.getMainHandItem().get(WAND_ABILITY_HOLDER);
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

    public static String roundNonWholeString(double number) {
        double decimalPart = number - (int) number;
        if (decimalPart == 0) return String.valueOf(Math.round(number));
        return String.valueOf(number);
    }

    public static String roundNonWholeString(String input) {
        StringBuilder result = new StringBuilder();
        StringBuilder numberBuffer = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                numberBuffer.append(c);
            } else {
                if (!numberBuffer.isEmpty()) {
                    result.append(processNumber(numberBuffer.toString()));
                    numberBuffer.setLength(0);
                }
                result.append(c);
            }
        }

        if (!numberBuffer.isEmpty()) {
            result.append(processNumber(numberBuffer.toString()));
        }
        return result.toString();
    }

    private static String processNumber(String number) {
        try {
            double num = Double.parseDouble(number);
            double decimalPart = num - (int) num;
            if (decimalPart == 0) {
                return String.valueOf(Math.round(num));
            }
            return String.valueOf(num);
        } catch (NumberFormatException e) {
            // In case of unexpected parsing errors
            return number;
        }
    }

    public static double roundNonWholeDouble(double number) {
        double decimalPart = number - (int) number;
        if (decimalPart == 0) return Math.round(number);
        return number;
    }

    public static float getFormattedFloat(float value){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return  Float.parseFloat(decimalFormat.format(value));
    }

    public static double singleFormattedDouble(double value){
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return  roundNonWholeDouble(Double.parseDouble(decimalFormat.format(value)));
    }

    public static double doubleFormattedDouble(double value){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return  roundNonWholeDouble(Double.parseDouble(decimalFormat.format(value)));
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

    @SafeVarargs
    public static float attributeModifierCalculator(
        LivingEntity player,
        float initialValue,
        boolean isAddition,
        Holder<Attribute> ... attribute
    ){
        var wandItem = player.getMainHandItem();
        var abilityName = wandItem.get(DataComponentRegistry.WAND_DATA.get());
        if(abilityName == null) return 0;
        float getAttribute = 0;
        var getAbility = AbilityRegister.getFirstSpellByTypeId(abilityName.selectedAbility());
        if(getAbility.isEmpty()) return initialValue;

        float reCalculatedDamage = initialValue;

        for (Holder<Attribute> attributeHolder : attribute) {
            getAttribute += (float) player.getAttributes().getValue(attributeHolder);
        }

        float getPercentageDamage = (initialValue * getAttribute) / 100;

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
