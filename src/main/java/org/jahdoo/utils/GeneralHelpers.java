package org.jahdoo.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import java.util.function.Consumer;

public class GeneralHelpers {
    public static final Random Random = ThreadLocalRandom.current();
    public static GeneralHelpers generalHelpers = new GeneralHelpers();

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

    public static String roundNonWhole(double number) {
        double decimalPart = number - (int) number;
        if (decimalPart == 0) return String.valueOf(Math.round(number));
        return String.valueOf(number);
    }

    public static float getFormattedFloat(float value){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return  Float.parseFloat(decimalFormat.format(value));
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

    public static void playDebugMessage(Player player, Object... info){
        var randomColour = new Color((int)(Math.random() * 0x1000000)).getRGB();
        player.sendSystemMessage(Component.literal(Arrays.toString(info)).withStyle(style -> style.withColor(randomColour)));
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

        if(getAbility.isPresent()){
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

        return initialValue;
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

    public static void moveEntitiesRelativeToPlayer(Entity entity, double radius, Consumer<Vec3> method) {
        for (int i = 0; i < radius; i++) {
            double angle = 2 * Math.PI * i / radius;
            Direction direction = entity.getDirection();

            double x = direction.getStepX() + radius * Math.cos(angle);
            double y = direction.getStepY();
            double z = direction.getStepZ() + radius * Math.sin(angle);

            method.accept(new Vec3(x, y, z));
        }
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

    public <T extends ParticleOptions> int sendParticles(ServerLevel serverLevel, T pType, Vec3 positions, int pParticleCount, double pXOffset, double pYOffset, double pZOffset, double pSpeed) {
        if(serverLevel != null){
            var clientboundlevelparticlespacket = new ClientboundLevelParticlesPacket(pType, false, positions.x, positions.y, positions.z, (float) pXOffset, (float) pYOffset, (float) pZOffset, (float) pSpeed, pParticleCount);
            var i = 0;
            for (int j = 0; j < serverLevel.players().size(); ++j) {
                var serverplayer = serverLevel.players().get(j);
                if (sendParticles(serverplayer, positions.x, positions.y, positions.z, clientboundlevelparticlespacket)) ++i;
            }
            return i;
        }
        return 0;
    }

    private static boolean sendParticles(ServerPlayer pPlayer, double pPosX, double pPosY, double pPosZ, Packet<?> pPacket) {
        if (pPlayer.level().isClientSide) {
            return false;
        } else {
            var blockpos = pPlayer.blockPosition();
            if (blockpos.closerToCenterThan(new Vec3(pPosX, pPosY, pPosZ), 64.0D)) {
                pPlayer.connection.send(pPacket);
                return true;
            } else {
                return false;
            }
        }
    }


}
