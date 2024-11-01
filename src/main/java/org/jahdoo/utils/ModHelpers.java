package org.jahdoo.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.JahdooMod;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.client.SharedUI;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.registers.*;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ModHelpers {
    public static final Random Random = ThreadLocalRandom.current();

    public static ResourceLocation modResourceLocation(String location) {
        return ResourceLocation.fromNamespaceAndPath(JahdooMod.MOD_ID, location);
    }

    public static <T> T getRandomListElement(List<T> collection){
        Collections.shuffle(collection);
        return collection.getFirst();
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
        var randomColour = new Color((int)(Math.random() * 0x1000000)).getRGB();
        player.sendSystemMessage(Component.literal(Arrays.toString(info)).withStyle(style -> style.withColor(randomColour)));
    }

    public static void damageEntityWithModifiers(LivingEntity target, LivingEntity player, float currentDamage, AbstractElement getElementType){
        target.hurt(player.damageSources().playerAttack((Player) player), attributeModifierCalculator(player, currentDamage, getElementType, AttributesRegister.MAGIC_DAMAGE_MULTIPLIER, true));
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

    public static void debugComponent(ItemStack itemStack, Player player){
        player.sendSystemMessage(Component.literal("New Request"));
        player.sendSystemMessage(Component.literal("-----------------------------------------------------"));
        for (TypedDataComponent<?> component : itemStack.getComponents()) {
            player.sendSystemMessage(withStyleComponent(component.toString(), new Color((int)(Math.random() * 0x1000000)).getRGB()));
            player.sendSystemMessage(Component.literal(" "));
        }
        player.sendSystemMessage(Component.literal("-----------------------------------------------------"));
        player.sendSystemMessage(Component.literal(" "));
    }

    public static float attributeModifierCalculator(
        LivingEntity player,
        float initialValue,
        AbstractElement getElementType,
        Holder<Attribute> attribute,
        boolean isAddition
    ){
        var wandItem = player.getMainHandItem();
        var abilityName = wandItem.get(DataComponentRegistry.WAND_DATA.get());
        if(abilityName == null) return 0;
        var getAttribute = player.getAttributes().getValue(attribute);
        var getAbility = AbilityRegister.getFirstSpellByTypeId(abilityName.selectedAbility());
        if(getAbility.isEmpty()) return initialValue;

        var element = SharedUI.getElementWithType(getAbility.get(), wandItem);
        float reCalculatedDamage = initialValue;
        if (Objects.equals(getElementType, element)) {
            float getPercentageDamage = (float) (initialValue / 100 * getAttribute);
            if(isAddition){
                reCalculatedDamage = reCalculatedDamage + getPercentageDamage;
            } else {
                reCalculatedDamage = reCalculatedDamage - getPercentageDamage;
            }
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
