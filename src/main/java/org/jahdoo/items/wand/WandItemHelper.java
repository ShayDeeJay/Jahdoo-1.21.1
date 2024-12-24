package org.jahdoo.items.wand;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.client.SharedUI;
import org.jahdoo.entities.EternalWizard;
import org.jahdoo.registers.AbilityRegister;
import org.jahdoo.registers.ElementRegistry;
import org.jahdoo.utils.ModHelpers;

import java.util.ArrayList;
import java.util.List;

import static org.jahdoo.ability.rarity.JahdooRarity.attachRarityTooltip;
import static org.jahdoo.particle.ParticleStore.rgbToInt;
import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;
import static org.jahdoo.utils.ColourStore.HEADER_COLOUR;
import static org.jahdoo.utils.ColourStore.SUB_HEADER_COLOUR;
import static org.jahdoo.utils.ModHelpers.*;


public class WandItemHelper {

    public static Component defence = ModHelpers.withStyleComponentTrans("wandHelper.jahdoo.set_wizard_mode.defence", rgbToInt(102, 178, 255));
    public static Component attack = ModHelpers.withStyleComponentTrans("wandHelper.jahdoo.set_wizard_mode.attack", rgbToInt(255, 102, 102));

    public static Component getItemName(ItemStack wandType){
        var abstractElement = ElementRegistry.getElementByWandType(wandType.getItem()).getFirst();
        if(abstractElement != null){
            return ModHelpers.withStyleComponentTrans(
                "wandHelper.jahdoo.type",
                abstractElement.particleColourSecondary(),
                abstractElement.getElementName()
            );
        }
        return Component.empty();
    }

    public static void setWizardMode(LivingEntity pInteractionTarget, Player pPlayer){
        if(pInteractionTarget instanceof EternalWizard eternalWizard){
            var mode = eternalWizard.getMode();
            eternalWizard.setMode(!eternalWizard.getMode());
            var getType = mode ? defence : attack;
            pPlayer.displayClientMessage(Component.translatable("wandHelper.jahdoo.set_wizard_mode", getType), true);

        }
    }

    public static void totalSlots(List<Component> toolTips, ItemStack wandItem, int colour){
        var wandData = wandItem.get(WAND_DATA);
        if(wandData == null) return;
        var slot = ModHelpers.withStyleComponent(String.valueOf(wandData.abilitySlots()), colour);
        toolTips.add(withStyleComponentTrans("wandHelper.jahdoo.ability_slots", HEADER_COLOUR, slot));
    }

    public static void appendRefinementPotential(List<Component> toolTips, ItemStack wandItem, int colour){
        var wandData = wandItem.get(WAND_DATA);
        if(wandData == null) return;
        var slot = ModHelpers.withStyleComponent(String.valueOf(wandData.refinementPotential()), colour);
        toolTips.add(withStyleComponent("Potential: ", HEADER_COLOUR).copy().append(slot));
    }

    public static List<Component> getItemModifiers(ItemStack wandItem){
        var appendComponents = new ArrayList<Component>();
        var abstractElement = ElementRegistry.getElementByWandType(wandItem.getItem()).getFirst();
        appendComponents.add(attachRarityTooltip(wandItem));
        totalSlots(appendComponents, wandItem, SUB_HEADER_COLOUR);
        appendRefinementPotential(appendComponents, wandItem, SUB_HEADER_COLOUR);
        appendSelectedAbility(wandItem, appendComponents);
        attributeToolTips(wandItem, appendComponents, abstractElement);
        if(!getAllSlots(wandItem).isEmpty()) appendComponents.add(Component.empty());
        return appendComponents;
    }


    public static void attributeToolTips(ItemStack itemStack, List<Component> appendComponents, AbstractElement abstractElement) {
        var type = withStyleComponent(abstractElement.getElementName(), abstractElement.textColourPrimary());
        var colourPre = rgbToInt(198, 198, 198);
        var attributes = itemStack.getAttributeModifiers().modifiers().stream().toList();
        if(!attributes.isEmpty()){
            appendComponents.add(Component.empty());
            appendComponents.add(ModHelpers.withStyleComponentTrans("wandHelper.jahdoo.get_modifiers", colourPre, type));
            appendComponents.addAll(standAloneAttributes(itemStack, abstractElement));
        }
    }

    public static List<Component> standAloneAttributes(ItemStack itemStack, AbstractElement element) {
        var appendComponents = new ArrayList<Component>();
        var wandOnlyAttributes = itemStack.getAttributeModifiers().modifiers().stream().toList().subList(0,3);
        if(!wandOnlyAttributes.isEmpty()){
            var colourSuf = rgbToInt(145, 145, 145);
            for (ItemAttributeModifiers.Entry entry : wandOnlyAttributes) {
                Component translatable;
                var value = roundNonWholeString(singleFormattedDouble(entry.modifier().amount()));
                var valueWithFix = "+" + value + "%";
                var descriptionId = entry.attribute().value().getDescriptionId();
                translatable = withStyleComponent(valueWithFix, colourSuf)
                    .copy()
                    .append(withStyleComponentTrans(descriptionId, colourSuf).getString().replace(element.getElementName(), ""));
                appendComponents.add(translatable);
            }
        }
        return appendComponents;
    }

    public static List<ItemStack> getAllSlots(ItemStack itemStack){
        var wandData = itemStack.get(WAND_DATA);
        if(wandData != null) return wandData.runeSlots();
        return List.of();
    }

    private static void appendSelectedAbility(ItemStack wandItem, ArrayList<Component> appendComponents) {
        var abilityId = wandItem.get(WAND_DATA).selectedAbility();
        if(wandItem.has(WAND_DATA) && !abilityId.isEmpty()){
            var getFullName = AbilityRegister.getFirstSpellByTypeId(abilityId);
            getFullName.ifPresent(
                abilityRegistrar -> {
                    var prefix = withStyleComponent("Selected: ", HEADER_COLOUR);
                    var element = SharedUI.getElementWithType(abilityRegistrar, wandItem);
                        if(element != null){
                        var text = abilityRegistrar.getAbilityName();
                        var component = prefix.copy().append(withStyleComponent(text, element.textColourSecondary()));
                        appendComponents.add(component);
                    }
                }
            );
        }
    }


    public static void storeBlockType(ItemStack itemStack, BlockState state, Player player, BlockPos pos){
        var compound = new CompoundTag();
        compound.put("block", NbtUtils.writeBlockState(state));
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
        player.displayClientMessage(Component.literal("Assigned: ").append(state.getBlock().getName()), true);
    }

    public static Block getStoredBlock(Level level, ItemStack itemStack){
        var holder = level.holderLookup(Registries.BLOCK);
        var component = itemStack.get(DataComponents.CUSTOM_DATA);
        if(component == null) return Blocks.AIR;
        var getFromComp = component.copyTag().getCompound("block");
        var state = NbtUtils.readBlockState(holder, getFromComp);
        return state.getBlock();
    }
}
