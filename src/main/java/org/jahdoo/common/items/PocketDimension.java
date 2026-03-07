package org.jahdoo.common.items;

import kotlin.Pair;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jahdoo.common.registers.ComponentReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.level_manager.PlayerHomeDim;
import org.jetbrains.annotations.NotNull;
import org.shaydee.shaydeeapi.helpers.ClientHelpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.ArrayList;
import java.util.List;

public class PocketDimension extends BaseJahdooItem {

    public static final BlockPos HOME = new BlockPos(0, 90, 1);

    public PocketDimension() {
        super(new Properties());
    }

    @Override
    public Pair<String, List<ItemStack>> getAdditional() {
        var getItems = new ArrayList<ItemStack>();
        JahdooItem.addItems(2, new ItemStack(ItemReg.CHARGED_ADVANCED_AUGMENT_CORE), getItems);
        JahdooItem.addItems(2, new ItemStack(Items.GRASS_BLOCK), getItems);
        JahdooItem.addItems(4, new ItemStack(ItemReg.LISITE_SHARD), getItems);

        return new Pair<>("test test test", getItems);
    }

    @Override
    public Component getName(ItemStack stack) {
        var player = ClientHelpers.getMinecraft().player;
        var prefix = player == null ? "" : player.getName().getString();

        return TextHelpers.withStyleComponentTrans("item.jahdoo.pocket_dimension", -1, prefix);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        var itemInHand = player.getItemInHand(usedHand);

        var serverLevel = onUse(player, itemInHand);
        if (serverLevel != null) return serverLevel;

        return InteractionResultHolder.fail(itemInHand);
    }

    public static InteractionResultHolder<ItemStack> onUse(Player player, ItemStack itemInHand) {
        if(LevelGenerator.isNexus(player.level())) {
            player.displayClientMessage(TextHelpers.withStyleComponentTrans("info.jahdoo.unusable", -1), true);
            return InteractionResultHolder.fail(itemInHand);
        }

        if(player.level() instanceof ServerLevel serverLevel){
            if(player instanceof  ServerPlayer serverPlayer){
                player.getCooldowns().addCooldown(itemInHand.getItem(), 100);
                if(!(serverLevel instanceof CustomLevel)){
                    return findPocketDimension(serverLevel, serverPlayer, itemInHand);
                } else {
                    return findOriginDimension(serverLevel, serverPlayer, itemInHand);
                }
            }
        }
        return null;
    }

    public static @NotNull InteractionResultHolder<ItemStack> findPocketDimension(
        ServerLevel serverLevel,
        ServerPlayer serverPlayer,
        ItemStack itemInHand
    ) {
        var playerHome = PlayerHomeDim.getPlayerHome(serverPlayer);
        var findLevel = LevelGenerator.findLevel(playerHome, serverLevel);
        var hasSpot = itemInHand.get(ComponentReg.BLOCK_POS);

        if(findLevel.isPresent()){
            var correctPost = HOME;
            if(hasSpot != null) correctPost = hasSpot;
            var path = serverPlayer.level().dimension().location().toString();

            itemInHand.set(ComponentReg.BLOCK_POS, serverPlayer.blockPosition());
            itemInHand.set(ComponentReg.ID, path);
            serverPlayer.teleportTo(findLevel.get(), correctPost.getX(), correctPost.getY(), correctPost.getZ(), serverPlayer.yRotO, serverPlayer.xRotO);
            return InteractionResultHolder.success(itemInHand);
        } else {
            serverPlayer.sendSystemMessage(TextHelpers.withStyleComponent("Creating your dimension....", ColourHelpers.getUniqueA()));
            var transition = PlayerHomeDim.generateNewHome(serverLevel, serverPlayer, HOME);

            serverPlayer.setRespawnPosition(transition.newLevel().dimension(), HOME, 0, false, false);
            serverPlayer.sendSystemMessage(TextHelpers.withStyleComponent("Dimension created", ColourHelpers.getUniqueA()));
            return InteractionResultHolder.fail(itemInHand);
        }
    }

    public static @NotNull InteractionResultHolder<ItemStack> findOriginDimension(
        ServerLevel serverLevel,
        ServerPlayer serverPlayer,
        ItemStack itemInHand
    ) {
        var pos = itemInHand.get(ComponentReg.BLOCK_POS);
        if(pos == null) return InteractionResultHolder.fail(itemInHand);

        var playerHome = itemInHand.get(ComponentReg.ID);
        var levels = serverLevel.getServer().getAllLevels();

        ServerLevel levelGet = null;

        for (var level : levels) {
            var isLevel = level.dimension().location().toString().equals(playerHome);
            if(isLevel) levelGet = level;
        }

        itemInHand.set(ComponentReg.BLOCK_POS, serverPlayer.blockPosition());

        if(levelGet != null) {
            serverPlayer.teleportTo(levelGet, pos.getX(), pos.getY(), pos.getZ(), serverPlayer.yRotO, serverPlayer.xRotO);
        }

        return InteractionResultHolder.success(itemInHand);
    }

}
