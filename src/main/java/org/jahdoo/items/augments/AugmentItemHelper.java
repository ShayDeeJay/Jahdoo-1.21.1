package org.jahdoo.items.augments;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.all_magic.JahdooRarity;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.all_magic.AbstractAbility;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.DataComponentRegistry;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.DataComponentHelper;
import org.jahdoo.utils.GeneralHelpers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static org.jahdoo.items.augments.AugmentRatingSystem.*;
import static org.jahdoo.all_magic.AbilityBuilder.*;
import static org.jahdoo.registers.DataComponentRegistry.NUMBER;
import static org.jahdoo.registers.DataComponentRegistry.WAND_ABILITY_HOLDER;
import static org.jahdoo.utils.GeneralHelpers.withStyleComponent;

public class AugmentItemHelper {

    public static void discoverUse(ItemStack itemStack, Player player){
        var component = itemStack.get(NUMBER);
        if(component != null) return;

        if(itemStack.getCount() > 1) {
            ItemStack copyItem = itemStack.copyWithCount(1);
            itemStack.shrink(1);
            copyItem.set(NUMBER, 1);
            augmentIdentifierShared(copyItem, player);
            throwOrAddItem(player, copyItem);
        } else {
            itemStack.set(NUMBER, 1);
            augmentIdentifierShared(itemStack, player);
        }

        player.startUsingItem(player.getUsedItemHand());
    }

    public static void discoverTick(Entity entity, ItemStack itemStack){
        if(!(entity instanceof Player player)) return;
        if(!(player.level() instanceof ServerLevel serverLevel)) return;
        var component = itemStack.get(NUMBER);
        if(component == null) return;
        var numLoops = 3;
        var numDistance = 6;
        if(component <= numLoops){
            if(player.tickCount % numDistance == 0){
                itemStack.set(NUMBER, component + 1);
                GeneralHelpers.getSoundWithPosition(serverLevel, player.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, 0.4f, 0.6F);
                itemStack.set(CUSTOM_MODEL_DATA, new CustomModelData(GeneralHelpers.Random.nextInt(1, 7)));
                augmentIdentifierShared(itemStack, player);
                if(component == numLoops){
                    AugmentItemHelper.setDiscoveryTheme(serverLevel, player);
                }
            }
        }
    }

    public static void setDiscoveryTheme(ServerLevel serverLevel, Player player){
        GeneralHelpers.generalHelpers.sendParticles(
            serverLevel,
            ParticleTypes.TOTEM_OF_UNDYING,
            player.position().add(0, player.getBbHeight()/2, 0),
            50, 0,0.8,0,0.5
        );
        GeneralHelpers.getSoundWithPosition(serverLevel, player.blockPosition(), SoundEvents.BEACON_ACTIVATE,0.7f,2F);
        GeneralHelpers.getSoundWithPosition(serverLevel, player.blockPosition(), SoundEvents.PARROT_IMITATE_EVOKER,1f,0.8F);
    }

    public static void augmentIdentifierShared(ItemStack itemStack, @Nullable Player player){
        var abstractAbilities = AbilityRegister.REGISTRY.stream().toList();
        var ability = abstractAbilities.get(GeneralHelpers.Random.nextInt(0, abstractAbilities.size()));

        ability.setModifiers(itemStack);
        var wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());

        if(player != null){
            if(!player.level().isClientSide){
                setAbilityToAugment(itemStack, ability, wandAbilityHolder);
            }
        } else {
            setAbilityToAugment(itemStack, ability, wandAbilityHolder);
        }
    }

    public static void augmentIdentifierSharedRarity(ItemStack itemStack){
        var ability = JahdooRarity.getAbilityWithRarity();
        ability.setModifiers(itemStack);
        var wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        setAbilityToAugment(itemStack, ability, wandAbilityHolder);
    }

    public static void setAbilityToAugment(ItemStack itemStack, AbstractAbility ability, WandAbilityHolder wandAbilityHolder){
        int type;
        DataComponentHelper.setAbilityTypeItemStack(itemStack, ability.setAbilityId());
        if (ability.isMultiType()) {
            AbilityHolder.AbilityModifiers abilityModifiers = wandAbilityHolder
                .abilityProperties()
                .get(ability.setAbilityId())
                .abilityProperties()
                .get(SET_ELEMENT_TYPE);
            type = (int) abilityModifiers.actualValue();
        } else {
            type = ability.getElemenType().getTypeId();
        }

        itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(type));
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

    public static void toolTipBase(
        List<Component> toolTips,
        ItemStack itemStack,
        ItemStack itemStack1,
        String keys,
        String abilityLocation,
        int colour
    ){
        var component = getCurrentModifierRating(itemStack, itemStack1, keys, abilityLocation);

        if(colour == 0){
            toolTips.add(component);
        } else {
            toolTips.add(component.copy().withStyle(style -> style.withColor(colour)));
        }

        if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 73)) {
            toolTips.add(displayRating(itemStack, keys, abilityLocation));
        }
    }

    public static Component getModifierContext(String keys, String current, int getComparison){
        String converter;

        if(keys.contains("Duration") || keys.contains("Speed") || keys.contains("Delay") || keys.contains("Time")){
            converter = Double.parseDouble(current) / 20 + "s";
        } else if (keys.contains("Chance")) {
            converter = convertToPercentage(Integer.parseInt(current)) + "%";
        } else if (keys.contains("Radius") || keys.contains("Distance") || keys.contains("Range")) {
            converter = current + " Blocks";
        } else if (keys.contains("Multiplier")) {
            converter = current + "x";
        }else {
            converter = current;
        }

        var matchesStat = 5987164;
        var betterThanStat = -12988840;
        var worseThanStat = -47032;

        return Component
            .literal(converter)
            .withStyle(style -> style.withColor( getComparison == 1 ? matchesStat : getComparison == 2 ?  betterThanStat : worseThanStat));
    }

    public static Component getCurrentModifierRating(ItemStack itemStack, ItemStack itemStack1, String keys, String abilityLocation) {
        var hoveredTag = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        var getHoveredHolder = hoveredTag.abilityProperties().get(abilityLocation);
        var abilityModifier = getHoveredHolder.abilityProperties().get(keys);
        var format = FORMAT.format(abilityModifier.actualValue());
        var type = itemStack.get(DataComponents.CUSTOM_MODEL_DATA);
        var colour = ElementRegistry.getElementByTypeId(type.value());
        if(!colour.isEmpty()){
            if (itemStack1 != null) {
                int comparisonResult;
                var matchedTag = itemStack1.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
                if (matchedTag != null) {
                    var getMatchedHolder = matchedTag.abilityProperties().get(abilityLocation);
                    var matchedModifier = getMatchedHolder.abilityProperties().get(keys);
                    if (matchedModifier != null) {
                        var getMatchedEntry = matchedModifier.actualValue();
                        var getHoveredEntry = abilityModifier.actualValue();
                        var isHigherBetter = abilityModifier.isHigherBetter();

                        var isEven = getHoveredEntry == getMatchedEntry;
                        var isBetter = getHoveredEntry > getMatchedEntry;
                        var isWorse = getHoveredEntry < getMatchedEntry;

                        var higherNumber = isBetter ? 2 : isEven ? 1 : 3;
                        var lowerNumber = isWorse ? 2 : isEven ? 1 : 3;

                        comparisonResult = isHigherBetter ? higherNumber : lowerNumber;

                        return getFormattedModifiers(keys, itemStack, format, comparisonResult);
                    }
                }
            } else {
                return getFormattedModifiers(keys, itemStack, format, 1);
            }
        }
        return Component.empty();
    }

    public static Component getFormattedModifiers(String keys, ItemStack itemStack, String format, int comparison){
        var type = itemStack.get(DataComponents.CUSTOM_MODEL_DATA);
        var colour = ElementRegistry.getElementByTypeId(type.value());
        return Component.literal(keys)
            .withStyle(style -> style.withColor(colour.getFirst().particleColourSecondary()))
            .append(Component.literal(" | ")
                .withStyle(ChatFormatting.GRAY)
                .append(getModifierContext(keys, format, comparison)));
    }

    public static List<Component> getAllAbilityModifiers(
        ItemStack itemStack,
        ItemStack itemStack1,
        String abilityLocation
    ){
        var toolTips = new ArrayList<Component>();
        if(itemStack.getComponents().isEmpty()) return toolTips;
        var exceptions = List.of(COOLDOWN, MANA_COST, SET_ELEMENT_TYPE, "index");
        var wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
        var abilityHolder = wandAbilityHolder.abilityProperties().get(abilityLocation);
        var ability = AbilityRegister.getSpellsByTypeId(abilityLocation);
        var rarity = ability.getFirst().rarity();

        toolTips.add(withStyleComponent("Rarity: ", -9013642).copy().append(withStyleComponent(rarity.getSerializedName(), rarity.getColour())));
        toolTips.add(Component.empty());

        int subHeaderColour = -2434342;
        var curlyStart = (char) 171;
        var curlyEnd = (char) 187;
        String unique = curlyStart + " Unique Attributes " + curlyEnd;
        if(abilityHolder == null) return toolTips;

        List<String> filteredSuffix = abilityHolder.abilityProperties().keySet()
            .stream()
            .filter(abilityModifiers -> !exceptions.contains(abilityModifiers))
            .toList();

        toolTipBase(toolTips, itemStack, itemStack1, MANA_COST, abilityLocation, -6829330);
        toolTipBase(toolTips, itemStack, itemStack1, COOLDOWN, abilityLocation, -7471171);

        if(!filteredSuffix.isEmpty()){
            toolTips.add(Component.literal(" "));
            toolTips.add(Component.literal(unique).withStyle(style -> style.withColor(subHeaderColour)));
            filteredSuffix.forEach(keys -> toolTipBase(toolTips, itemStack, itemStack1, keys, abilityLocation, 0));
        }

        return toolTips;
    }

    public static void shiftForDetails(List<Component> toolTips){
        if(!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 73)){
            var componentI = Component.literal("[i]").withStyle(style -> style.withColor(-2631721));
            toolTips.add(Component.literal(" "));

            var prefix = "Hold ";
            var suffix = " for details";

            MutableComponent message = Component.literal(prefix)
                .withStyle(ChatFormatting.GRAY)
                .append(componentI)
                .append(Component.literal(suffix).withStyle(ChatFormatting.GRAY));

            toolTips.add(message);
        }
    }

    public static void getHoverText(ItemStack itemStack, List<Component> toolTips){
        if(itemStack.getComponents().has(DataComponentRegistry.WAND_ABILITY_HOLDER.get())){
            var wandAbilityHolder = itemStack.get(DataComponentRegistry.WAND_ABILITY_HOLDER.get());
            var abilityLocation = wandAbilityHolder.abilityProperties().keySet().stream().findAny().get();
            toolTips.addAll(getAllAbilityModifiers(itemStack, null, abilityLocation));
            shiftForDetails(toolTips);
            toolTips.add(GeneralHelpers.withStyleComponent("Placeable in wands", -12368570));
        } else {
            toolTips.add(Component.literal("Right-click to discover").withStyle(ChatFormatting.GRAY));

        }

    }

    public static Component getAbilityName(ItemStack itemStack, AbstractElement info){
        var wandAbilityHolder = itemStack.get(WAND_ABILITY_HOLDER.get());
        var component = new AtomicReference<>(Component.empty());

        if(wandAbilityHolder != null){
            wandAbilityHolder.abilityProperties().keySet().stream().findFirst().ifPresent(
                s -> {
                    var abstractAbility = AbilityRegister.REGISTRY.get(GeneralHelpers.modResourceLocation(s));
                    if(abstractAbility != null){
                        component.set(
                            Component.literal(abstractAbility.getAbilityName())
                                .withStyle((style) -> style.withColor(info.textColourPrimary()))
                        );
                    }
                }
            );
        }

        return component.get();
    }

    public static Component getHoverName(ItemStack itemStack){
        var modelData = itemStack.getComponents().get(CUSTOM_MODEL_DATA);
        if(modelData != null){
            var abstractElement = ElementRegistry.REGISTRY
                .stream()
                .filter(ability -> ability.getTypeId() == modelData.value())
                .toList();

            if (!abstractElement.isEmpty()) {
                if (itemStack.getComponents().has(WAND_ABILITY_HOLDER.get())) {
                    return AugmentItemHelper.getAbilityName(itemStack, abstractElement.getFirst());
                }
                var elementName = abstractElement.getFirst().getElementName() + " Augment";
                var elementColour = abstractElement.getFirst().textColourPrimary();
                return Component.literal(elementName).withStyle(style -> style.withColor(elementColour));
            }
        }
        return Component.literal("Unidentified Augment").withStyle(style -> style.withColor(-9013642));
    }

}
