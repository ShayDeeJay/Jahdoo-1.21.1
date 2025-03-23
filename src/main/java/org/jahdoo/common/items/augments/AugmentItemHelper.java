package org.jahdoo.common.items.augments;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ascension.ability.AbilityBuilder;
import org.jahdoo.ascension.ability.AbilityRegistrar;
import org.jahdoo.ascension.ability.abilities.elemental_shooter.ElementalShooterAbility;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.ascension.utils.LocalLootBeamData;
import org.jahdoo.common.client.gui.AugmentScreen;
import org.jahdoo.common.components.DataComponentHelper;
import org.jahdoo.common.components.WandAbilityHolder;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.AbilityReg;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ElementReg;
import org.jahdoo.common.registers.ItemReg;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.ascension.utils.Maths.roundNonWholeString;
import static org.jahdoo.ascension.utils.Maths.ticksToTime;
import static org.jahdoo.common.items.augments.AugmentRatingSystem.*;
import static org.jahdoo.common.registers.ComponentReg.*;

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
        if(!(player.level() instanceof ServerLevel)) return;
        var component = itemStack.get(NUMBER);
        if(component == null) return;
        var numLoops = 3;
        var numDistance = 6;
        if(component <= numLoops){
            if(player.tickCount % numDistance == 0){
                itemStack.set(NUMBER, component + 1);
                if(player instanceof ServerPlayer serverPlayer){
                    Helpers.sendClientSound(serverPlayer, SoundEvents.EXPERIENCE_ORB_PICKUP, 0.4f, 0.6F);
                }
                itemStack.set(CUSTOM_MODEL_DATA, new CustomModelData(Helpers.Random.nextInt(1, 7)));
                augmentIdentifierShared(itemStack, player);
                if(component == numLoops){
                    AugmentItemHelper.setDiscoveryTheme(player.level(), player);
                }
            }
        }
    }

    public static void setDiscoveryTheme(Level level, Player player){
        ParticleHandlers.sendParticles(
            level,
            ParticleTypes.TOTEM_OF_UNDYING,
            player.position().add(0, player.getBbHeight()/2, 0),
            50, 0,0.8,0,0.5
        );

        if(player instanceof ServerPlayer serverPlayer){
            Helpers.sendClientSound(serverPlayer, SoundEvents.BEACON_ACTIVATE, 0.7f, 2F);
            Helpers.sendClientSound(serverPlayer, SoundEvents.PARROT_IMITATE_EVOKER, 1f, 0.8F);
        }
    }

    public static void augmentIdentifierShared(ItemStack itemStack, @Nullable Player player){
        var abstractAbilities = AbilityReg.REGISTRY.stream().toList();
        var ability = abstractAbilities.get(Helpers.Random.nextInt(0, abstractAbilities.size()));
        ability.setModifiers(itemStack);
        var wandAbilityHolder = itemStack.get(ComponentReg.WAND_ABILITY_HOLDER.get());
        if(player != null){
            if(!player.level().isClientSide) setAbilityToAugment(itemStack, ability, wandAbilityHolder);
        } else {
            setAbilityToAugment(itemStack, ability, wandAbilityHolder);
        }
        itemStack.set(JAHDOO_RARITY, ability.rarity().getId());
    }

    public static ItemStack getAugmentWithAbility(AbilityRegistrar ability) {
        var itemStack = new ItemStack(ItemReg.AUGMENT);
        LocalLootBeamData.attachLootBeamComponent(itemStack, ability.rarity());
        ability.setModifiers(itemStack);
        itemStack.set(ComponentReg.JAHDOO_RARITY, ability.rarity().getId());
        var wandAbilityHolder = itemStack.get(ComponentReg.WAND_ABILITY_HOLDER.get());
        setAbilityToAugment(itemStack, ability, wandAbilityHolder);
        return itemStack;
    }

    public static @NotNull ElementalShooterAbility elementalWithType(int elementId) {
        var ability = new ElementalShooterAbility(){
            @Override
            public void setModifiers(ItemStack itemStack) {
                new AbilityBuilder(itemStack, abilityId.getPath().intern())
                    .setStaticMana(15)
                    .setStaticCooldown(0)
                    .setDamage(10, 5, 1)
                    .setEffectChance(50, 10, 10)
                    .setEffectStrength(10, 1, 1)
                    .setEffectDuration(300, 100, 50)
                    .setAbilityTagModifiersRandom(SHOT_MULTIPLIER, 3, 1, true, 1)
                    .setAbilityTagModifiersRandom(NUMBER_OF_RICOCHET, 6, 1, true, 1)
                    .setModifier(SET_ELEMENT_TYPE, 0, 0, false, elementId)
                    .build();
            }
        };
        getAugmentWithAbility(ability);
        return ability;
    }

    public static void augmentIdentifierSharedRarity(ItemStack itemStack, boolean withUtil, @Nullable JahdooRarity rarity){
        var ability = JahdooRarity.getAbilityWithRarity(withUtil, rarity);

        LocalLootBeamData.attachLootBeamComponent(itemStack, ability.rarity());
        ability.setModifiers(itemStack);
        var wandAbilityHolder = itemStack.get(ComponentReg.WAND_ABILITY_HOLDER.get());
        setAbilityToAugment(itemStack, ability, wandAbilityHolder);
    }

    public static void augmentIdentifierSharedUtil(ItemStack itemStack, @Nullable JahdooRarity rarity){
        var ability = JahdooRarity.getAbilityUtil(rarity);
        LocalLootBeamData.attachLootBeamComponent(itemStack, ability.rarity());
        ability.setModifiers(itemStack);
        var wandAbilityHolder = itemStack.get(ComponentReg.WAND_ABILITY_HOLDER.get());
        setAbilityToAugment(itemStack, ability, wandAbilityHolder);
    }

    public static void setAbilityToAugment(ItemStack itemStack, AbilityRegistrar ability, WandAbilityHolder wandAbilityHolder){
        int type;

        DataComponentHelper.setAbilityTypeItemStack(itemStack, ability.setAbilityId());

        if (ability.isMultiType()) {
            var abilityModifiers = wandAbilityHolder
                .abilityProperties()
                .get(ability.setAbilityId())
                .abilityProperties()
                .get(SET_ELEMENT_TYPE);
            type = (int) abilityModifiers.actualValue();
        } else {
            type = ability.getElemenType().id();
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
        int colour,
        boolean hide
    ){
        var component = getCurrentModifierRating(itemStack, itemStack1, keys, abilityLocation);
        var modifier = getModifier(itemStack, abilityLocation, keys);

        if(colour == 0){
            toolTips.add(component);
        } else {
            toolTips.add(component.copy().withStyle(style -> style.withColor(colour)));
        }

        if(modifier.highestValue() != -1){
            if (hide || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 73)) {
                toolTips.add(displayRating(itemStack, keys, abilityLocation));
            }
        }
    }

    public static Component getModifierContextSingle(String keys, String current, int getComparison){
        return getModifierContext(keys, current, getComparison, false, "", "");
    }

    public static Component getModifierContextRange(String keys, String min, String max){
        return getModifierContext(keys, "", 0, true, min, max);
    }

    public static Component getFormattedModifiers(String keys, ItemStack itemStack, String format, int comparison){
        var type = itemStack.get(DataComponents.CUSTOM_MODEL_DATA);
        if(type == null) return Component.empty();
        var element = ElementReg.fromId(type.value()).orElse(ElementReg.mystic());

        return Component.literal(keys)
            .withStyle(style -> style.withColor(element.partColourB()))
            .append(Component.literal(" | ")
                .withStyle(ChatFormatting.GRAY)
                .append(getModifierContextSingle(keys, format, comparison)));
    }


    public static boolean shiftForDetails(List<Component> toolTips){
        if(!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 73)){
//            toolTips.add(Component.literal(" "));
            var hotkey = Helpers.withStyleComponentTrans("augmentHelper.jahdoo.hotkey",-2631721);
            var holdToDiscover = Helpers.withStyleComponentTrans("augmentHelper.jahdoo.hold_details",-10066330, hotkey);
            toolTips.add(holdToDiscover);
            return true;
        }
        return false;
    }

    public static void getHoverText(ItemStack itemStack, List<Component> toolTips, boolean hide, Level level){
        if(itemStack.getComponents().has(ComponentReg.WAND_ABILITY_HOLDER.get())){
            var wandAbilityHolder = itemStack.get(ComponentReg.WAND_ABILITY_HOLDER.get());
            if(wandAbilityHolder == null) return;
            var abilityLocation = wandAbilityHolder.abilityProperties().keySet().stream().findAny().get();
            toolTips.addAll(getAllAbilityModifiers(itemStack, null, abilityLocation, hide, level));
            toolTips.add(Component.empty());
            shiftForDetails(toolTips);
            toolTips.add(Helpers.withStyleComponentTrans("augmentHelper.jahdoo.place", -12368570));
        } else {
            toolTips.add(Helpers.withStyleComponentTrans("augmentHelper.jahdoo.discover", -5131855));
        }
    }

    public static Component getAbilityName(ItemStack itemStack, AbstractElement info){
        var wandAbilityHolder = itemStack.get(WAND_ABILITY_HOLDER.get());
        var component = new AtomicReference<>(Component.empty());

        if(wandAbilityHolder != null){
            wandAbilityHolder.abilityProperties().keySet().stream().findFirst().ifPresent(
                s -> {
                    var location = Helpers.res(s);
                    if (location.getPath().isEmpty()) return;
                    var abilityRegistrars = AbilityReg.REGISTRY.get(Helpers.res(s));
                    if (abilityRegistrars != null) {
                        component.set(
                            Component.literal(abilityRegistrars.getAbilityName())
                                .withStyle((style) -> style.withColor(info.textColourA()))
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
            var abstractElement = ElementReg.REGISTRY
                .stream()
                .filter(ability -> ability.id() == modelData.value())
                .toList();

            if (!abstractElement.isEmpty()) {
                if (itemStack.getComponents().has(WAND_ABILITY_HOLDER.get())) {
                    return AugmentItemHelper.getAbilityName(itemStack, abstractElement.getFirst());
                }
                var elementName = abstractElement.getFirst().name() + " Augment";
                var elementColour = abstractElement.getFirst().textColourA();
                return Component.literal(elementName).withStyle(style -> style.withColor(elementColour));
            }
        }
        return Component.literal("Unidentified Augment").withStyle(style -> style.withColor(-9013642));
    }

    public static void setAugmentModificationScreen(ItemStack itemStack, @Nullable Screen previousScreen){
        Minecraft.getInstance().setScreen(getAugmentModificationScreen(itemStack, previousScreen));
    }

    public static Screen getAugmentModificationScreen(ItemStack itemStack, @Nullable Screen previousScreen) {
        var itemStacks = itemStack.get(ComponentReg.WAND_ABILITY_HOLDER.get());
        if(itemStacks != null){
            var item = itemStacks.abilityProperties().keySet().stream().findFirst();
            if(item.isPresent()){
                var ability = AbilityReg.getFirstSpellByTypeId(item.get());
                if(ability.isPresent()){
                    if (isConfigAbility(ability.get(), item.get(), itemStack)) {
                        return new AugmentScreen(itemStack, item.get(), previousScreen);
                    }
                }
            }
        }
        return null;
    }

    public static Screen getAugmentModificationScreenWand(ItemStack itemStack, @org.jetbrains.annotations.Nullable Screen previousScreen) {
        var itemStacks = itemStack.get(ComponentReg.WAND_ABILITY_HOLDER.get());
        var selected = itemStack.get(WAND_DATA);
        if(itemStacks != null && selected != null){
            var item = selected.selectedAbility();
            var ability = AbilityReg.getFirstSpellByTypeId(item);
            if(ability.isPresent()){
                if (isConfigAbility(ability.get(), item, itemStack)) {
                    return new AugmentScreen(itemStack, item, previousScreen);
                }
            }
        }
        return null;
    }

    public static Optional<String> isValidAugmentUtil(ItemStack itemStack) {
        var itemStacks = itemStack.get(ComponentReg.WAND_ABILITY_HOLDER.get());
        if(itemStacks == null) return Optional.empty();
        var item = itemStacks.abilityProperties().keySet().stream().findFirst();
        if(item.isPresent()){
            var ability = AbilityReg.getFirstSpellByTypeId(item.get());
            if(ability.isPresent()){
                if (isConfigAbility(ability.get(), item.get(), itemStack)) {
                    return item;
                }
            }
        }
        return Optional.empty();
    }

    public static boolean isConfigAbility(AbilityRegistrar selectedAbility, String ability, ItemStack itemStack) {
        var wandAbilityHolder = itemStack.get(WAND_ABILITY_HOLDER);
        if(wandAbilityHolder == null) return false;
        var abilityHolder = wandAbilityHolder.abilityProperties().get(ability);
        var filterOutBase = abilityHolder.abilityProperties()
            .keySet()
            .stream()
            .filter(name -> !name.equals(MANA_COST) && !name.equals(COOLDOWN));
        return /*selectedAbility.getElemenType() == ElementRegistry.UTILITY.get() && */!filterOutBase.toList().isEmpty();
//        return selectedAbility.getElemenType() == ElementRegistry.UTILITY.get() && !filterOutBase.toList().isEmpty();
    }

    public static Component getCurrentModifierRating(ItemStack itemStack, ItemStack itemStack1, String keys, String abilityLocation) {
        var hoveredTag = itemStack.get(ComponentReg.WAND_ABILITY_HOLDER.get());
        var getHoveredHolder = hoveredTag.abilityProperties().get(abilityLocation);
        var abilityModifier = getHoveredHolder.abilityProperties().get(keys);
        if(abilityModifier == null) return Component.empty();
        var format = FORMAT.format(abilityModifier.actualValue());
        var type = itemStack.get(DataComponents.CUSTOM_MODEL_DATA);
        if(type == null) return Component.empty();

        if (itemStack1 != null) {
            int comparisonResult;
            var matchedTag = itemStack1.get(ComponentReg.WAND_ABILITY_HOLDER.get());
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

        return Component.empty();
    }

    public static Component getModifierContext(String keys, String current, int getComparison, boolean isRange, String min, String max) {
        String displayValue;
        var time = List.of("Duration", "Speed", "Delay", "Time");
        var probability = List.of("Chance");
        var distance = List.of("Radius", "Distance", "Range");
        var multiplier = List.of("Multiplier");
        var by = List.of("Block Size");

        if (time.stream().anyMatch(keys::contains)) {
            displayValue = isRange
                  ? rangeString(ticksToTime(min), ticksToTime(max))
                  : ticksToTime(current);
        } else if (probability.stream().anyMatch(keys::contains)) {
            displayValue = isRange
                  ? rangeString(
                  convertToPercentage(Double.parseDouble(min)),
                  convertToPercentage(Double.parseDouble(max))
            ) + "%"
                  : convertToPercentage(Double.parseDouble(current)) + "%";
        } else if (distance.stream().anyMatch(keys::contains)) {
            displayValue = isRange
                  ? rangeString(min, max) + " Blocks"
                  : current + " Blocks";
        } else if (multiplier.stream().anyMatch(keys::contains)) {
            displayValue = isRange
                  ? rangeString(min, max) + "x"
                  : current + "x";
        } else if (by.stream().anyMatch(keys::contains)) {
            displayValue = isRange
                  ? rangeString(min + "x" + min, max + "x" + max)
                  : current + " x " + current;
        } else {
            displayValue = isRange ? rangeString(min, max) : current;
        }

        var matchesStat = 5987164;
        var betterThanStat = -12988840;
        var worseThanStat = -47032;
        var colour = isRange ? matchesStat : getComparison == 1 ? matchesStat : getComparison == 2 ? betterThanStat : worseThanStat;

        return Component
              .literal(roundNonWholeString(displayValue))
              .withStyle(style -> style.withColor(colour));
    }

    public static List<Component> getAllAbilityModifiers(
          ItemStack itemStack,
          ItemStack itemStack1,
          String abilityLocation,
          boolean hide,
          Level level
    ){
        var toolTips = new ArrayList<Component>();
        if(itemStack.getComponents().isEmpty()) return toolTips;
        var exceptions = List.of(COOLDOWN, MANA_COST, SET_ELEMENT_TYPE, "index", OFFSET);
        var wandAbilityHolder = itemStack.get(ComponentReg.WAND_ABILITY_HOLDER.get());
        if(wandAbilityHolder == null) return toolTips;
        var abilityHolder = wandAbilityHolder.abilityProperties().get(abilityLocation);
        var ability = AbilityReg.getSpellsByTypeId(abilityLocation);
        var index = itemStack.get(JAHDOO_RARITY);

        if(!ability.isEmpty() && index != null){
            toolTips.add(JahdooRarity.addRarityTooltip(JahdooRarity.getAllRarities().get(index), level));
        }

        toolTips.add(Component.empty());

        int subHeaderColour = -2434342;
        var curlyStart = String.valueOf((char) 171);
        var curlyEnd = String.valueOf((char) 187);
        if(abilityHolder == null) return toolTips;

        List<String> filteredSuffix = abilityHolder.abilityProperties().keySet()
              .stream()
              .filter(abilityModifiers -> !exceptions.contains(abilityModifiers))
              .toList();

        if(abilityHolder.abilityProperties().containsKey(MANA_COST)){
            toolTipBase(toolTips, itemStack, itemStack1, MANA_COST, abilityLocation, -6829330, hide);
        }

        if(abilityHolder.abilityProperties().containsKey(COOLDOWN)){
            toolTipBase(toolTips, itemStack, itemStack1, COOLDOWN, abilityLocation, -7471171, hide);
        }

        if(!filteredSuffix.isEmpty()){
            toolTips.add(Component.literal(" "));
            toolTips.add(Helpers.withStyleComponentTrans("augmentHelper.jahdoo.attributes", subHeaderColour, curlyStart, curlyEnd));
            filteredSuffix.forEach(keys -> toolTipBase(toolTips, itemStack, itemStack1, keys, abilityLocation, 0, hide));
        }

        return toolTips;
    }

}
