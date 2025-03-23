package org.jahdoo.common.items.wand;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.common.block.wand.WandBlockEntity;
import org.jahdoo.common.client.SharedUI;
import org.jahdoo.common.entities.eternal_wizard.EternalWizard;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import org.jahdoo.common.registers.AbilityReg;
import org.jahdoo.common.registers.BlockReg;
import org.jahdoo.common.registers.ItemReg;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.sounds.SoundEvents.ALLAY_THROW;
import static net.minecraft.sounds.SoundEvents.EVOKER_PREPARE_SUMMON;
import static net.minecraft.util.FastColor.ARGB32.color;
import static net.minecraft.world.InteractionHand.OFF_HAND;
import static org.jahdoo.ascension.rarity.JahdooRarity.attachRarityTooltip;
import static org.jahdoo.ascension.utils.ColourStore.HEADER_COLOUR;
import static org.jahdoo.ascension.utils.ColourStore.SUB_HEADER_COLOUR;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.ascension.utils.Maths.roundNonWholeString;
import static org.jahdoo.ascension.utils.Maths.singleFormattedDouble;
import static org.jahdoo.ascension.utils.PositionFinders.innerRadiusRandom;
import static org.jahdoo.common.block.wand.WandBlockEntity.GET_WAND_SLOT;
import static org.jahdoo.common.particle.ParticleHandlers.bakedParticle;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.particle.ParticleStore.GENERIC_PARTICLE;
import static org.jahdoo.common.particle.ParticleStore.rgbToInt;
import static org.jahdoo.common.registers.ComponentReg.*;
import static org.jahdoo.common.registers.ElementReg.fromWand;


public class WandItemHelper {

    public static final String PREFIX = "wandHelper.jahdoo.";
    public static Component defence = withStyleComponentTrans(PREFIX + "set_wizard_mode.defence", rgbToInt(102, 178, 255));
    public static Component attack = withStyleComponentTrans(PREFIX + "set_wizard_mode.attack", rgbToInt(255, 102, 102));

    public static void totalSlots(List<Component> toolTips, ItemStack wandItem, int colour){
        var wandData = wandItem.get(WAND_DATA);
        if(wandData == null) return;
        var slot = withStyleComponent(String.valueOf(wandData.abilitySlots()), colour);
        toolTips.add(withStyleComponentTrans(PREFIX + "ability_slots", HEADER_COLOUR, slot));
    }

    public static void appendRefinementPotential(List<Component> toolTips, ItemStack wandItem){
        var wandData = wandItem.get(RUNE_HOLDER);
        if(wandData == null) return;
        var slot = withStyleComponent(String.valueOf(wandData.refinementPotential()), SUB_HEADER_COLOUR);
        toolTips.add(toolTips.size(), withStyleComponent("Potential: ", HEADER_COLOUR).copy().append(slot));
    }

    public static JahdooRarity getRarity(Item item){
        var wandData = item.components().get(JAHDOO_RARITY.get());
        var getWandData = wandData == null ? 0 : wandData;
        return JahdooRarity.getAllRarities().get(getWandData);
    }

    public static List<ItemStack> getAllSlots(ItemStack itemStack){
        var wandData = itemStack.get(RUNE_HOLDER);
        if(wandData != null) return wandData.runeSlots();
        return List.of();
    }

    public static void placeParticle(Level level, Vec3 pos, ParticleOptions particle){
        var randomY = Random.nextDouble(0.1 , 0.2);
        var randomStartY = Random.nextDouble(0.05, 0.5);
        level.addParticle(particle, pos.x, pos.y - randomStartY, pos.z, 0, randomY, 0);
    }

    public static void playPlaceSound(Level level, BlockPos pos){
        getSoundWithPosition(level, pos, ALLAY_THROW, 1, 0.8f);
        getSoundWithPosition(level, pos, EVOKER_PREPARE_SUMMON, 0.4f, 1.6f);
    }

    private static void sendCantUseMessage(LivingEntity entity, AbstractElement abstractElement) {
        var text = "You don't have the power to offhand this yet.";
        var colour = abstractElement.textColourA();
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

    public static void setWizardMode(LivingEntity target, Player player){
        if(target instanceof EternalWizard eternalWizard){
            var mode = eternalWizard.getMode();
            eternalWizard.setMode(!eternalWizard.getMode());
            var getType = mode ? defence : attack;
            player.displayClientMessage(Component.translatable(PREFIX + "set_wizard_mode", getType), true);

        }
    }

    public static Component getItemName(ItemStack wandType){
        var getElement = fromWand(wandType.getItem());
        return getElement.map(
            element -> withStyleComponentTrans(
                PREFIX + "type",
                element.partColourB(),
                element.name()
            )
        ).orElseGet(Component::empty);
    }

    public static void attributeToolTips(ItemStack itemStack, List<Component> appendComponents, AbstractElement abstractElement) {
        var type = withStyleComponent(abstractElement.name(), abstractElement.textColourA());
        var colourPre = rgbToInt(198, 198, 198);
        var attributes = itemStack.getAttributeModifiers().modifiers().stream().toList();
        if(!attributes.isEmpty()){
            appendComponents.add(Component.empty());
            appendComponents.add(withStyleComponentTrans(PREFIX + "get_modifiers", colourPre, type));
            appendComponents.addAll(standAloneAttributes(itemStack, abstractElement));
        }
    }

    static void wandItemRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(
            new GeoRenderProvider() {
                private WandItemRenderer renderer;
                @Override
                public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                    if (this.renderer == null) this.renderer = new WandItemRenderer();
                    return this.renderer;
                }
            }
        );
    }

    public static List<Component> getItemModifiers(ItemStack wandItem, Level level){
        var appendComponents = new ArrayList<Component>();
        var abstractElement = fromWand(wandItem.getItem());
        if(abstractElement.isPresent()){
            var e = attachRarityTooltip(wandItem, level);
            if(e != null) appendComponents.add(e);
            totalSlots(appendComponents, wandItem, SUB_HEADER_COLOUR);
            appendRefinementPotential(appendComponents, wandItem);
            appendSelectedAbility(wandItem, appendComponents);
            attributeToolTips(wandItem, appendComponents, abstractElement.get());
            if (!getAllSlots(wandItem).isEmpty()) appendComponents.add(Component.empty());
        }
        return appendComponents;
    }

    private static void appendSelectedAbility(ItemStack wandItem, ArrayList<Component> appendComponents) {
        var abilityId = wandItem.get(WAND_DATA).selectedAbility();
        if(wandItem.has(WAND_DATA) && !abilityId.isEmpty()){
            var getFullName = AbilityReg.getFirstSpellByTypeId(abilityId);
            getFullName.ifPresent(
                abilityRegistrar -> {
                    var prefix = withStyleComponent("Selected: ", HEADER_COLOUR);
                    var element = SharedUI.getElementWithType(abilityRegistrar, wandItem);
                    if(element != null){
                        var text = abilityRegistrar.getAbilityName();
                        var component = prefix.copy().append(withStyleComponent(text, element.textColourB()));
                        appendComponents.add(component);
                    }
                }
            );
        }
    }

    static void canOffhandWand(
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

    static void bonusModifierTooltip(ItemStack stack, List<Component> toolTip, Item.TooltipContext context) {
        var list = stack.getAttributeModifiers().modifiers().stream().toList();
        if(list.size() > 3){
            var text = "Bonus Modifiers";
            var gold = color(255, 215, 0);
            var comp = highlightTextComponent(context.level(), text, HEADER_COLOUR, gold, 2, 20);
            toolTip.add(comp);
            var runeHolder = stack.get(RUNE_HOLDER);
            var maxSize = runeHolder != null ? runeHolder.runeSlots().size() : list.size();
            for (var entry : list.subList(3, Math.max(maxSize, 4))) {
                toolTip.add(RuneHelpers.standAloneAttributes(entry));
            }
            toolTip.add(Component.empty());
        }
    }

    public static boolean canOffHand(
        LivingEntity entity,
        InteractionHand interactionHand,
        boolean shouldSendMessage
    ){
        var curio = CuriosApi.getCuriosInventory(entity);

        if(interactionHand == OFF_HAND){
            if(curio.isEmpty()) return false;

            var isGauntletEquipped = curio.get().isEquipped(ItemReg.BATTLEMAGE_GAUNTLET.get());
            if(isGauntletEquipped) return true;

            if(shouldSendMessage){
                var item = entity.getItemInHand(interactionHand).getItem();
                fromWand(item).ifPresent(element -> sendCantUseMessage(entity, element));
            }

            return false;
        }

        return true;
    }

    public static List<Component> standAloneAttributes(ItemStack itemStack, AbstractElement element) {
        var appendComponents = new ArrayList<Component>();
        var wandOnlyAttributes = itemStack
            .getAttributeModifiers()
            .modifiers()
            .stream()
            .toList()
            .subList(0,3);

        if(!wandOnlyAttributes.isEmpty()){
            var colourSuf = rgbToInt(145, 145, 145);
            for (ItemAttributeModifiers.Entry entry : wandOnlyAttributes) {
                Component translatable;
                var value = roundNonWholeString(singleFormattedDouble(entry.modifier().amount()));
                var valueWithFix = "+" + value + "%";
                var descriptionId = entry.attribute().value().getDescriptionId();
                translatable = withStyleComponent(valueWithFix, colourSuf)
                    .copy()
                    .append(withStyleComponentTrans(descriptionId, colourSuf).getString().replace(element.name(), ""));
                appendComponents.add(translatable);
            }
        }
        return appendComponents;
    }

    public static InteractionResult onPlace(BlockPlaceContext context) {
        var clickedPos = context.getClickedPos();
        var player = context.getPlayer();
        var level = context.getLevel();
        var itemStack = context.getItemInHand();

        if (player == null || !player.isShiftKeyDown() || !player.onGround()) return InteractionResult.PASS;
        if(!context.getLevel().getBlockState(clickedPos).isEmpty()) return InteractionResult.FAIL;

        level.setBlockAndUpdate(clickedPos, BlockReg.WAND.get().defaultBlockState());
        var blockEntity = level.getBlockEntity(clickedPos);
        if (!(blockEntity instanceof WandBlockEntity wandBlockEntity)) return InteractionResult.FAIL;
        var copiedWand = itemStack.copyWithCount(1);

        playPlaceSound(level, context.getClickedPos());
        player.setItemInHand(context.getHand(), ItemStack.EMPTY);
        wandBlockEntity.inputItemHandler.setStackInSlot(GET_WAND_SLOT, copiedWand);
        wandBlockEntity.updateView();
        var getType = fromWand(wandBlockEntity.getWandItemFromSlot().getItem());

        if(getType.isPresent()){
            var element = getType.get();
            var par1 = bakedParticle(element.id(), 10, 1f, false);
            var par2 = genericParticle(GENERIC_PARTICLE, element, 10, 1f, false, 0.3);
            innerRadiusRandom(clickedPos, 0.1, 20,
                positions -> placeParticle(level, positions, Random.nextInt(0, 3) == 0 ? par1 : par2)
            );
        }
        return InteractionResult.SUCCESS;
    }
}
