package org.jahdoo.common.items.paxel;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jahdoo.common.items.JahdooItem;
import org.shaydee.shaydeeapi.Helpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static net.minecraft.core.BlockPos.betweenClosed;
import static net.minecraft.core.BlockPos.containing;
import static net.minecraft.core.registries.Registries.BLOCK;
import static net.minecraft.world.InteractionResult.PASS;
import static net.minecraft.world.InteractionResult.sidedSuccess;
import static net.minecraft.world.entity.LivingEntity.getSlotForHand;
import static org.jahdoo.common.registers.BlockReg.RAW_NEXITE_BLOCK;

public class DivinityPaxel extends DiggerItem implements JahdooItem {

    public DivinityPaxel() {
        super(
            Tiers.NETHERITE,
            BlockTags.MINEABLE_WITH_PICKAXE,
            new Item.Properties()
                .attributes(PickaxeItem.createAttributes(Tiers.NETHERITE, 1.0F, -3.0F))
                .attributes(ShovelItem.createAttributes(Tiers.NETHERITE, 1.0F, -3.0F))
                .attributes(AxeItem.createAttributes(Tiers.NETHERITE, 1.0F, -3.0F))
        );
    }

    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        var containsPick = ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(itemAbility);
        var containsShovel = ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(itemAbility);
        var containsAxe = ItemAbilities.DEFAULT_AXE_ACTIONS.contains(itemAbility);

        return containsPick || containsShovel || containsAxe;
    }

    @Override
    public Component getName(ItemStack stack) {
        return TextHelpers.withStyleComponent(super.getName(stack).getString(), ColourHelpers.getExperienceGreen());
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 1000F;
    }

    private void canDoThing(Level level, BlockState state, BlockPos pos) {
        if(state.is(RAW_NEXITE_BLOCK)){
            var value = 5;
            var box  = expandFromCentre(pos, value);
            var blockBounding = betweenClosed(containing(box.getMinPosition()), containing(box.getMaxPosition()));

            if (level instanceof ServerLevel serverLevel) {
                for (var blockPos : blockBounding) {
                    var state2 = serverLevel.getBlockState(blockPos);
                    if(state2.is(RAW_NEXITE_BLOCK)){
                        var getRandomOre = serverLevel.registryAccess().lookupOrThrow(BLOCK);
                        var x = getRandomOre
                            .listElements()
                            .map(Holder::value)
                            .filter(block -> block instanceof DropExperienceBlock)
                            .toList();

                        if(!x.isEmpty()){
                            var block = Helpers.listRandom(x);
                            serverLevel.setBlockAndUpdate(blockPos, block.defaultBlockState());
                            serverLevel.updateNeighborsAt(blockPos, block);
                        }
                    }
                }
            }
        }
    }

    public static AABB expandFromCentre(BlockPos pos1, int inflate) {
        AABB aabb = new AABB(pos1);

        var inflate1 = inflate - 0.5;
        return aabb
            .setMaxX(aabb.maxX + inflate1)
            .setMinY(aabb.minY - inflate1)
            .setMinX(aabb.minX - inflate1)
            .setMaxY(aabb.maxY + inflate1)
            .setMinZ(aabb.minZ - inflate1)
            .setMaxZ(aabb.maxZ + inflate1);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var blockpos = context.getClickedPos();
        var player = context.getPlayer();
        var blockState = level.getBlockState(blockpos);
        var optional = this.evaluateNewBlockState(level, blockpos, player, level.getBlockState(blockpos), context);

        var pass = shovelAction(context, blockState, level, blockpos, player);
        if (pass != null) return pass;

        var pass1 = axeAction(context, optional, player, blockpos, level);
        if (pass1 != null) return pass1;

        return PASS;
    }

    private static @Nullable InteractionResult axeAction(UseOnContext context, Optional<BlockState> optional, Player player, BlockPos blockpos, Level level) {
        if (optional.isEmpty()) return PASS;

        var itemstack = context.getItemInHand();
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) player, blockpos, itemstack);
        }

        level.setBlock(blockpos, optional.get(), 11);
        level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, optional.get()));
        if (player != null) {
            itemstack.hurtAndBreak(1, player, getSlotForHand(context.getHand()));
            return sidedSuccess(level.isClientSide);
        }
        return null;
    }

    private static @org.jetbrains.annotations.Nullable InteractionResult shovelAction(UseOnContext context, BlockState blockState, Level level, BlockPos blockpos, Player player) {
        if (context.getClickedFace() == Direction.DOWN) return PASS;

        var bState1 = blockState.getToolModifiedState(context, ItemAbilities.SHOVEL_FLATTEN, false);
        var bState2 = blockState.getToolModifiedState(context, ItemAbilities.SHOVEL_DOUSE, false);

        if (bState1 != null && level.getBlockState(blockpos.above()).isAir()) {
            level.playSound(player, blockpos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            bState2 = bState1;
        }

        if (bState2 != null) {
            if (!level.isClientSide) {
                level.levelEvent(null, 1009, blockpos, 0);
                level.setBlock(blockpos, bState2, 11);
                level.gameEvent(GameEvent.BLOCK_CHANGE, blockpos, GameEvent.Context.of(player, bState2));
                if (player != null) {
                    context.getItemInHand().hurtAndBreak(1, player, getSlotForHand(context.getHand()));
                }
            }
            return sidedSuccess(level.isClientSide);
        }
        return null;
    }

    private Optional<BlockState> evaluateNewBlockState(Level level, BlockPos pos, @Nullable Player player, BlockState state, UseOnContext p_40529_) {
        var optional = Optional.ofNullable(state.getToolModifiedState(p_40529_, ItemAbilities.AXE_STRIP, false));
        if (optional.isPresent()) {
            level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            return optional;
        }

        var optional1 = Optional.ofNullable(state.getToolModifiedState(p_40529_, ItemAbilities.AXE_SCRAPE, false));
        if (optional1.isPresent()) {
            level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3005, pos, 0);
            return optional1;
        }

        var optional2 = Optional.ofNullable(state.getToolModifiedState(p_40529_, ItemAbilities.AXE_WAX_OFF, false));
        if (optional2.isPresent()) {
            level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, pos, 0);
            return optional2;
        }

        return Optional.empty();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        appendItemToolTips(stack, context, tooltipComponents, false);
        if(isItemBroken(stack)){
            brokenGearMessage(tooltipComponents, stack);
            return;
        }
        enchantmentTooltip(stack, tooltipComponents, true, context.level());
        appendWeaponToolTip(stack, context, tooltipComponents);
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment != Enchantments.MENDING;
    }

    @Override
    public ItemStack applyEnchantments(ItemStack stack, List<EnchantmentInstance> enchantments) {
        enchantments.removeIf(s -> s.enchantment.equals(Enchantments.MENDING));
        return super.applyEnchantments(stack, enchantments);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canGrindstoneRepair(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public float getXpRepairRatio(ItemStack stack) {
        return -1;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        for (var holderEntry : book.get(DataComponents.STORED_ENCHANTMENTS).entrySet()) {
            if(holderEntry.getKey().value().description().getString().contains("Mending")){
                return false;
            }
        }
        return true;
    }

    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return false;
    }

}
