package org.jahdoo.trial_nexus.loot;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.block.loot_chest.LootChestEntity;
import org.jahdoo.common.items.KeyItem;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.attachments.RunData;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.Helpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.SoundHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.List;

import static net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA;
import static net.minecraft.sounds.SoundEvents.LODESTONE_COMPASS_LOCK;
import static net.minecraft.sounds.SoundEvents.VAULT_EJECT_ITEM;
import static net.minecraft.world.ItemInteractionResult.FAIL;
import static net.minecraft.world.ItemInteractionResult.SUCCESS;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;
import static org.jahdoo.trial_nexus.loot.RewardLootTables.*;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.*;

public class LootHelpers {


    public static @NotNull ItemInteractionResult coinChestGetter(
        BlockPos pos,
        ServerLevel serverLevel,
        LootChestEntity lootChestEntity,
        Player player
    ) {
        var coinItems = getCoinItems(lootChestEntity.getData(INSTANCE_DATA));
        if(!coinItems.isEmpty()){
            lootChestEntity.setOpen(true);
            lootsplosian(pos.getCenter(), serverLevel, ColourHelpers.getAbsorptionYellow(), coinItems, false, 20, 0);
            openingSoundEffect(pos, serverLevel, false);
        } else {
            player.displayClientMessage(TextHelpers.withStyleComponent("Chest is empty!", ColourHelpers.getNegativeRed()), true);
        }
        return SUCCESS;
    }

    public static ItemInteractionResult lootChestGetter(
        ItemStack stack,
        ServerLevel serverLevel,
        BlockPos pos,
        LootChestEntity lootChestEntity,
        String difficulty,
        Player player
    ) {

        for (var item : player.getInventory().items) {
            var keyData = item.get(CUSTOM_MODEL_DATA);
            if (item.is(ItemReg.LOOT_KEY) && keyData != null && KeyItem.isValidKey(item, player.level())) {
                var value = keyData.value();
                var isValid = value == lootChestEntity.getRarity;
                RunData.incrementChestOpenedExp(serverLevel, player, value);
                var getInstance = serverLevel.getData(INSTANCE_DATA);
                if (isValid) {
                    lootChestEntity.setOpen(true);
                    var getId = new CustomModelData(lootChestEntity.getRarity);
                    var colour = KeyItem.getJahdooRarity(getId).getColour();
                    var getBy = switch (value){
                        case 1 -> getInstance.getRareLootMultiplier();
                        case 2 -> getInstance.getLegendaryLootMultiplier();
                        case 3 -> getInstance.getMythicLootMultiplier();
                        default -> getInstance.getCommonLootMultiplier();
                    };

                    for(int i = 0; i < Math.max(getBy, 1); i++){
                        standAloneLoot(serverLevel, pos.getCenter(), difficulty, value, colour);
                    }

                    openingSoundEffect(pos, serverLevel, true);
                    item.shrink(1);
                    return SUCCESS;
                }
            }
        }

        return FAIL;
    }

    public static void standAloneLoot(ServerLevel serverLevel, Vec3 pos, String difficulty, int keyValue, int colour) {
        var rewards = getCompletionLoot(serverLevel, pos, difficulty, keyValue);
        lootsplosian(pos, serverLevel, colour, rewards, true, 30, keyValue);
    }

    public static ItemStack potLoot(ServerLevel serverLevel, Vec3 pos, String difficulty, int keyValue){
        var rewards = getCompletionLoot(serverLevel, pos, difficulty, keyValue);
        var getItem = Helpers.listRandom(rewards);
        attachItemData(serverLevel, getItem, null, keyValue);
        return getItem;
    }

    public static void lootsplosian(
        Vec3 pos,
        ServerLevel serverLevel,
        int colour,
        List<ItemStack> rewards,
        boolean shouldDropExperience,
        int pickupDelay,
        int chestRarity
    ) {
        for (var reward : rewards) {
            itemBehaviour(pos, serverLevel, colour, shouldDropExperience, pickupDelay, chestRarity, reward);
        }
    }

    public static void itemBehaviour(Vec3 pos, ServerLevel serverLevel, int colour, boolean shouldDropExperience, int pickupDelay, int chestRarity, ItemStack reward) {
        var itemEntity = new ItemEntity(serverLevel, pos.x(), pos.y() + 0.2, pos.z(), reward);
        var angle = Random.nextDouble() * 2 * Math.PI;
        var horizontalOffset = 0.2 + Random.nextDouble() * 0.35;
        var offsetX = Math.cos(angle) * horizontalOffset;
        var offsetZ = Math.sin(angle) * horizontalOffset;
        var velocity = new Vec3(offsetX * (Math.random() - 0.5), Random.nextDouble(0.35, 0.6), offsetZ * (Math.random() - 0.5));
        var itemStackMain= itemEntity.getItem();

        itemEntity.setDeltaMovement(velocity);
        itemEntity.setPickUpDelay(pickupDelay);

        if(shouldDropExperience && Random.nextInt(10) == 0) {
            var exp = ItemReg.EXPERIENCE_ORB.get();
            var itemStack = new ItemStack(exp);

            switch (JahdooRarity.getRarity()) {
                case COMMON, RARE -> itemStack.set(CUSTOM_MODEL_DATA, new CustomModelData(1));
                case EPIC -> itemStack.set(CUSTOM_MODEL_DATA, new CustomModelData(2));
                case LEGENDARY, MYTHIC -> { /*No Data*/ }
            }

            var itemEntity1 = new ItemEntity(serverLevel, pos.x, pos.y, pos.z, itemStack);
            itemEntity1.setDeltaMovement(velocity);
            itemEntity1.setPickUpDelay(pickupDelay);
            serverLevel.addFreshEntity(itemEntity1);
        }

        particleBurst(serverLevel, pos, colour, chestRarity);
        attachItemData(serverLevel, itemStackMain, null, chestRarity);
        serverLevel.addFreshEntity(itemEntity);
    }

    private static void openingSoundEffect(BlockPos pos, ServerLevel serverLevel, boolean isLootChest) {
        SoundHelpers.getSoundWithPosition(serverLevel, pos, VAULT_EJECT_ITEM, SoundSource.BLOCKS, 2F, 0.8F);
        SoundHelpers.getSoundWithPosition(serverLevel, pos, LODESTONE_COMPASS_LOCK, SoundSource.BLOCKS, 2F, 1.4F);
        SoundHelpers.getSoundWithPosition(serverLevel, pos, SoundEvents.VAULT_PLACE, SoundSource.BLOCKS, 2F, 0.4F);

        if(isLootChest) {
            SoundHelpers.getSoundWithPosition(serverLevel, pos, SoundReg.LOOTBOX_OPEN.get(), SoundSource.BLOCKS, 2F, 1F);
        } else {
            SoundHelpers.getSoundWithPosition(serverLevel, pos, SoundReg.COINBOX_OPEN.get(), SoundSource.BLOCKS, 0.6F, 1F);
        }
    }

    public static void particleBurst(ServerLevel serverLevel, Vec3 center, int colour, int multiplier) {
        var fade = getColourDarker(colour, 0.5);
        var randomColouredParticle = getRandomColouredParticle(colour, fade, Random.nextInt(10, 20), 1f, false);
        var pos = center.add(0, 0.3f, 0);

        ParticleHandlers.particleBurst(serverLevel, pos, 2 * multiplier, randomColouredParticle, 0, 0.3, 0, 0.2f, 3);
    }
}
