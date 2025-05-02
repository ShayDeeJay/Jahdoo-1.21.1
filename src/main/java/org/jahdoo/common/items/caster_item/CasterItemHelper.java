package org.jahdoo.common.items.caster_item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jahdoo.ascension.element.AbstractElement;
import org.jahdoo.ascension.rarity.JahdooRarity;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.entities.eternal_wizard.EternalWizard;
import org.jahdoo.common.items.caster_item.elemental_wand.ElementalWandItemRenderer;
import org.jahdoo.common.items.runes.rune_data.RuneHelpers;
import org.jahdoo.common.registers.ItemReg;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.world.InteractionHand.OFF_HAND;
import static org.jahdoo.ascension.utils.ColourStore.*;
import static org.jahdoo.ascension.utils.Helpers.*;
import static org.jahdoo.common.particle.ParticleStore.rgbToInt;
import static org.jahdoo.common.registers.ComponentReg.*;
import static org.jahdoo.common.registers.mod.ElementReg.fromWand;


public class CasterItemHelper {

    public static final String PREFIX = "wandHelper.jahdoo.";
    public static Component defence = withStyleComponentTrans(PREFIX + "set_wizard_mode.defence", rgbToInt(102, 178, 255));
    public static Component attack = withStyleComponentTrans(PREFIX + "set_wizard_mode.attack", rgbToInt(255, 102, 102));

//    public static void appendPotentialComponent(List<Component> toolTips, ItemStack gearItem){
//        var wandData = gearItem.get(JAHDOO_GEAR_DATA);
//        if(wandData == null) return;
//
//        getPotentialComponent(gearItem, (s) -> toolTips.add(toolTips.size(), s));
//    }

    public static void getPotentialComponent(ItemStack gearItem, Consumer<Component> render){
        var wandData = gearItem.get(JAHDOO_GEAR_DATA);
        if(wandData == null) return;

        var slot = withStyleComponent(String.valueOf(wandData.refinementPotential()), DIAMOND_BOX);
        var append = withStyleComponent("Potential: ", SUB_HEADER_COLOUR).copy().append(slot);
        render.accept(append);
    }

    public static Component appendDurability(ItemStack wandItem) {
        var maxDamage = wandItem.get(DataComponents.MAX_DAMAGE);
        var damageTaken = wandItem.get(DataComponents.DAMAGE);
        if(maxDamage != null && damageTaken != null){
            var split = maxDamage/3;
            var durabilityColourIndicator = damageTaken <= split ? PERK_GREEN : damageTaken <= split * 2.5 ? ABSORPTION_YELLOW : NEGATIVE_RED;
            var prefix = Helpers.withStyleComponent("Durability: ", SUB_HEADER_COLOUR);
            var currentDurability = Helpers.withStyleComponent(durabilityDamageCount(wandItem) + "", durabilityColourIndicator);
            var maxDurability = Helpers.withStyleComponent("/" + maxDamage, BORDER_COLOUR);
            return prefix.copy().append(currentDurability).copy().append(maxDurability);
        }
        return Component.empty();
    }

    public static void getRepairSlotsComponent(ItemStack gearItem, Consumer<Component> render) {
        var wandData = gearItem.get(JAHDOO_GEAR_DATA);
        if(wandData == null) return;

        var repairSlots = wandData.repairSlots();
        if(!repairSlots.isEmpty()){
            var comp = withStyleComponent("Repair Slots: ", SUB_HEADER_COLOUR);
            for (var repairSlot : repairSlots) {
                var empty = repairSlot == 1;
                var text = empty ? "⭘" : "◎";
                var colour = empty ? GOLD_COIN : NETHERITE_BOX;
                comp = comp.copy().append(withStyleComponent(text, colour));
                comp =  comp.copy().append(" ");
            }
            render.accept(comp);
        }
    }


    public static JahdooRarity getRarity(Item item){
        var wandData = item.components().get(JAHDOO_RARITY.get());
        var getWandData = wandData == null ? 0 : wandData;
        return JahdooRarity.getAllRarities().get(getWandData);
    }

    public static List<ItemStack> getAllSlots(ItemStack itemStack){
        var wandData = itemStack.get(JAHDOO_GEAR_DATA);
        if(wandData != null) return wandData.runeSlots();
        return List.of();
    }

    private static void sendCantUseMessage(LivingEntity entity, AbstractElement abstractElement) {
        var getOffhand = getGauntlet(entity);
        var getDura = Helpers.durabilityDamageCount(getOffhand);
        var equipped = !getOffhand.isEmpty() && getDura == 0;
        var text = equipped ? "Gauntlet is broken." : "You unable to offhand this.";
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
;
        if(!attributes.isEmpty()){
            appendComponents.add(Component.literal(" "));
            appendComponents.add(withStyleComponentTrans("Implicit Modifiers", colourPre, type));
            appendComponents.addAll(standAloneAttributes(itemStack, abstractElement));
        }
    }

    public static void wandItemRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(
            new GeoRenderProvider() {
                private ElementalWandItemRenderer renderer;
                @Override
                public BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                    if (this.renderer == null) this.renderer = new ElementalWandItemRenderer();
                    return this.renderer;
                }
            }
        );
    }

    public static List<Component> getItemModifiers(ItemStack wandItem, Level level){
        var appendComponents = new ArrayList<Component>();
        var abstractElement = fromWand(wandItem.getItem());
        abstractElement.ifPresent(element -> attributeToolTips(wandItem, appendComponents, element));
        return appendComponents;
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
            } else if (isItemInOff && canOffHand(player, false)) {
                if(interactState != 1) itemStack.set(hand, 1);
            } else {
                if(interactState != 2) itemStack.set(hand, 2);
            }

        } else itemStack.set(hand, 2);
    }

    public static boolean canOffHand(
        LivingEntity entity,
        boolean shouldSendMessage
    ){
        var curio = CuriosApi.getCuriosInventory(entity);
        var offHand = entity.getItemInHand(OFF_HAND);

        if(offHand.getItem() instanceof CasterItem){
            if(curio.isEmpty()) return false;

            var isGauntletEquipped = curio.get().isEquipped(ItemReg.BATTLEMAGE_GAUNTLET.get());
            var getDura = Helpers.durabilityDamageCount(getGauntlet(entity));
            if(isGauntletEquipped && getDura > 0) return true;

            if(shouldSendMessage){
                fromWand(offHand.getItem()).ifPresent(element -> sendCantUseMessage(entity, element));
            }

            return false;
        }

        return true;
    }

    public static ItemStack getGauntlet(LivingEntity entity){
        var empty = ItemStack.EMPTY;
        var curio = CuriosApi.getCuriosInventory(entity);
        var offHand = entity.getItemInHand(OFF_HAND);

        if(offHand.getItem() instanceof CasterItem){
            if(curio.isEmpty()) return empty;
            var isGauntletEquipped = curio.get().isEquipped(ItemReg.BATTLEMAGE_GAUNTLET.get());
            if(isGauntletEquipped) return curio.get().getEquippedCurios().getStackInSlot(1);

        }

        return empty;
    }

    public static List<Component> standAloneAttributes(ItemStack itemStack, AbstractElement element) {
        var appendComponents = new ArrayList<Component>();
        var wandOnlyAttributes = itemStack
            .getAttributeModifiers()
            .modifiers()
            .stream()
            .toList();
        var newAttributes = wandOnlyAttributes.subList(0, Math.max(wandOnlyAttributes.size(), 1));

        if(!newAttributes.isEmpty()){
            for (ItemAttributeModifiers.Entry entry : newAttributes) {
                if(entry.modifier().id().getPath().contains("wand")){
                    var component = RuneHelpers.standAloneAttributes(entry);
                    var x = withStyleComponent(component.getString(), element.textColourA());

                    appendComponents.add(x);
                }
            }
        }
        return appendComponents;
    }
}
