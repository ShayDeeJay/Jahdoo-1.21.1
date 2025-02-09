package org.jahdoo.items.wand;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.ability.AbstractElement;
import org.jahdoo.block.wand.WandBlockEntity;
import org.jahdoo.client.SharedUI;
import org.jahdoo.components.ability_holder.WandAbilityHolder;
import org.jahdoo.entities.living.EternalWizard;
import org.jahdoo.registers.*;
import org.jahdoo.utils.ModHelpers;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.sounds.SoundEvents.ALLAY_THROW;
import static net.minecraft.sounds.SoundEvents.EVOKER_PREPARE_SUMMON;
import static net.minecraft.world.InteractionHand.OFF_HAND;
import static org.jahdoo.ability.rarity.JahdooRarity.attachRarityTooltip;
import static org.jahdoo.block.wand.WandBlockEntity.GET_WAND_SLOT;
import static org.jahdoo.particle.ParticleHandlers.bakedParticleOptions;
import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.GENERIC_PARTICLE_SELECTION;
import static org.jahdoo.particle.ParticleStore.rgbToInt;
import static org.jahdoo.registers.DataComponentRegistry.*;
import static org.jahdoo.registers.ElementRegistry.getElementFromWand;
import static org.jahdoo.utils.ColourStore.HEADER_COLOUR;
import static org.jahdoo.utils.ColourStore.SUB_HEADER_COLOUR;
import static org.jahdoo.utils.Maths.roundNonWholeString;
import static org.jahdoo.utils.Maths.singleFormattedDouble;
import static org.jahdoo.utils.ModHelpers.*;
import static org.jahdoo.utils.PositionGetters.getInnerRingOfRadiusRandom;


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

    public static void appendRefinementPotential(List<Component> toolTips, ItemStack wandItem){
        var wandData = wandItem.get(RUNE_HOLDER);
        if(wandData == null) return;
        var slot = ModHelpers.withStyleComponent(String.valueOf(wandData.refinementPotential()), SUB_HEADER_COLOUR);
        toolTips.add(toolTips.size(), withStyleComponent("Potential: ", HEADER_COLOUR).copy().append(slot));
    }

    public static List<Component> getItemModifiers(ItemStack wandItem, int tick){
        var appendComponents = new ArrayList<Component>();
        var abstractElement = ElementRegistry.getElementFromWand(wandItem.getItem());
        if(abstractElement.isPresent()){
            appendComponents.add(attachRarityTooltip(wandItem, tick));
            totalSlots(appendComponents, wandItem, SUB_HEADER_COLOUR);
            appendRefinementPotential(appendComponents, wandItem);
            appendSelectedAbility(wandItem, appendComponents);
            attributeToolTips(wandItem, appendComponents, abstractElement.get());
            if (!getAllSlots(wandItem).isEmpty()) appendComponents.add(Component.empty());
        }
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
        var wandData = itemStack.get(RUNE_HOLDER);
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

    public static @NotNull InteractionResult onPlace(BlockPlaceContext pContext) {
        var clickedPos = pContext.getClickedPos();
        var player = pContext.getPlayer();
        var level = pContext.getLevel();
        var itemStack = pContext.getItemInHand();

        if (player == null || !player.isShiftKeyDown() || !player.onGround()) return InteractionResult.PASS;
        if(!pContext.getLevel().getBlockState(clickedPos).isEmpty()) return InteractionResult.FAIL;

        level.setBlockAndUpdate(clickedPos, BlocksRegister.WAND.get().defaultBlockState());
        var blockEntity = level.getBlockEntity(clickedPos);
        if (!(blockEntity instanceof WandBlockEntity wandBlockEntity)) return InteractionResult.FAIL;
        var copiedWand = itemStack.copyWithCount(1);

        playPlaceSound(level, pContext.getClickedPos());
        player.setItemInHand(pContext.getHand(), ItemStack.EMPTY);
        wandBlockEntity.inputItemHandler.setStackInSlot(GET_WAND_SLOT, copiedWand);
        wandBlockEntity.updateView();
        var getType = getElementFromWand(wandBlockEntity.getWandItemFromSlot().getItem());

        if(getType.isPresent()){
            var element = getType.get();
            var par1 = bakedParticleOptions(element.getTypeId(), 10, 1f, false);
            var par2 = genericParticleOptions(GENERIC_PARTICLE_SELECTION, element, 10, 1f, false, 0.3);
            getInnerRingOfRadiusRandom(clickedPos, 0.1, 20,
                    positions -> placeParticle(level, positions, Random.nextInt(0, 3) == 0 ? par1 : par2)
            );
        }
        return InteractionResult.SUCCESS;
    }

    public static void placeParticle(Level level, Vec3 pos, ParticleOptions par1){
        var randomY = Random.nextDouble(0.1 , 0.2);
        var randomStartY = Random.nextDouble(0.05, 0.5);
        level.addParticle(par1, pos.x, pos.y - randomStartY, pos.z, 0, randomY, 0);
    }


    public static void playPlaceSound(Level level, BlockPos bPos){
        getSoundWithPosition(level, bPos, ALLAY_THROW, 1, 0.8f);
        getSoundWithPosition(level, bPos, EVOKER_PREPARE_SUMMON, 0.4f, 1.6f);
    }

    public static boolean canOffHand(
            LivingEntity entity,
            InteractionHand interactionHand,
            boolean shouldSendMessage
    ){
        var curio = CuriosApi.getCuriosInventory(entity);

        if(interactionHand == OFF_HAND){
            if(curio.isEmpty()) return false;

            var isGauntletEquipped = curio.get().isEquipped(ItemsRegister.BATTLEMAGE_GAUNTLET.get());
            if(isGauntletEquipped) return true;

            if(shouldSendMessage){
                var item = entity.getItemInHand(interactionHand).getItem();
                getElementFromWand(item).ifPresent(element -> sendCantUseMessage(entity, element));
            }

            return false;
        }

        return true;
    }

    static void validateRuneHand(
            ItemStack itemStack,
            Player player,
            Integer interactState,
            boolean isItemInMain,
            boolean isItemInOff
    ) {
        var hand = INTERACTION_HAND;
        if(interactState != null){

            if(isItemInMain){
                if(interactState != 0) itemStack.set(hand, 0);
            } else if (isItemInOff && canOffHand(player, OFF_HAND, false)) {
                if(interactState != 1) itemStack.set(hand, 1);
            } else {
                if(interactState != 2) itemStack.set(hand, 2);
            }

        } else itemStack.set(hand, 2);
    }

    static @NotNull Item.Properties wandInit() {
        return new Item.Properties()
            .stacksTo(1)
            .component(DataComponentRegistry.WAND_ABILITY_HOLDER.get(), WandAbilityHolder.DEFAULT)
            .component(WAND_DATA.get(), WandData.DEFAULT)
            .fireResistant();
    }

    private static void sendCantUseMessage(LivingEntity entity, AbstractElement abstractElement) {
        var text = "You don't have the power to offhand this yet.";
        var colour = abstractElement.textColourPrimary();
        var sendMessage = withStyleComponent(text, colour);
        if (entity instanceof Player player) player.displayClientMessage(sendMessage, true);
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
