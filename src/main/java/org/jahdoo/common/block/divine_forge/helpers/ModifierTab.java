package org.jahdoo.common.block.divine_forge.helpers;

import net.minecraft.world.item.ItemStack;
import org.jahdoo.common.block.divine_forge.DivineForgeScreen;
import org.jahdoo.common.items.runes.rune_data.JahdooGearData;
import org.jahdoo.common.networking.client2server.ItemInBlockC2SP;
import org.jahdoo.common.networking.client2server.JahdooGearDataC2SP;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.SoundReg;
import org.jetbrains.annotations.Nullable;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;

import java.util.ArrayList;
import java.util.function.Predicate;

import static net.neoforged.neoforge.network.PacketDistributor.sendToServer;
import static org.jahdoo.common.block.divine_forge.DivineForgeEntity.DEFAULT_SLOTS;

public class ModifierTab {

    public static void onModifyClick(int index, ItemStack itemStack, DivineForgeScreen screen) {
        var gearData = itemStack.get(ComponentReg.JAHDOO_GEAR_DATA);
        var correctIndex = index + DEFAULT_SLOTS;

        switch(index){

            case 0 -> {
                sharedClicks(
                    itemStack,
                    screen,
                    (gData) -> increasePotential(itemStack, gData),
                    correctIndex,
                    gearData
                );
            }

            case 1 -> {
                sharedClicks(
                    itemStack,
                    screen,
                    (gData) -> repairSlots(itemStack, gData),
                    correctIndex,
                    gearData
                );
            }

            case 2 -> {
                sharedClicks(
                    itemStack,
                    screen,
                    (gData) -> increaseRuneSlots(itemStack, screen, gData),
                    correctIndex,
                    gearData
                );

            }
            case 3 -> {

            }
        }


    }

    private static boolean increasePotential(ItemStack itemStack, JahdooGearData gData) {
        JahdooGearData.updateRefinementPotential(itemStack, gData.refinementPotential() + 50);
        return true;
    }

    private static boolean repairSlots(ItemStack itemStack, JahdooGearData gData) {
        var slots = gData.repairSlots();
        var copy = new ArrayList<>(slots);

        var x = copy.lastIndexOf(0);
        if (x >= 0) {
            copy.set(x, 1);
            JahdooGearData.updateRepairSlots(itemStack, copy);
            return true;
        }

        return false;
    }

    private static boolean increaseRuneSlots(ItemStack itemStack, DivineForgeScreen screen, JahdooGearData gData) {
        var slots = gData.runeSlots();
        if (slots.size() < 10) {

            var copy = new ArrayList<>(slots);
            copy.add(ItemStack.EMPTY);

            JahdooGearData.updateRuneSlots(itemStack, copy);
            screen.getRuneMenu().insertRuneSlots();

            return true;
        }
        return false;
    }

    public static void sharedClicks(
        ItemStack itemStack,
        DivineForgeScreen screen,
        Predicate<JahdooGearData> dataConsumer,
        int correctIndex,
        @Nullable JahdooGearData gearData
    ) {
        if (gearData != null && dataConsumer.test(gearData)) {

            var blockPos = screen.entity().getBlockPos();

            SoundHelpers.uiSound(SoundReg.UNLOCK_NOTIFICATION.get(), 1F, 0.6F);
            SoundHelpers.uiSound(SoundReg.REJECT.get(), 1F, 1.8F);

            sendToServer(new ItemInBlockC2SP(ItemStack.EMPTY, blockPos, correctIndex));
            sendToServer(new JahdooGearDataC2SP(itemStack.get(ComponentReg.JAHDOO_GEAR_DATA), blockPos, 0));

        } else {
            SoundHelpers.uiSound(SoundReg.REJECT.get(), 1F, 0.6F);
        }
    }

}
