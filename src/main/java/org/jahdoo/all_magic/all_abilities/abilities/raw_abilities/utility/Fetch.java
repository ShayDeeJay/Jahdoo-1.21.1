package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.networking.packet.server2client.PlayClientSoundSyncS2CPacket;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.utils.ModHelpers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.MAGIC_PARTICLE_SELECTION;

public class Fetch extends AbstractUtilityProjectile {
     ResourceLocation abilityId = ModHelpers.res("fetch_property");

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Fetch();
    }

    @Override
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        if(this.genericProjectile.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ModularChaosCubeEntity) return;
        var player = (Player) genericProjectile.getOwner();
        var pickedUpItem = false;

        List<ItemEntity> items = this.genericProjectile.level().getEntitiesOfClass(
            ItemEntity.class,
            this.genericProjectile.getBoundingBox().inflate(11,11,11),
            entity -> true
        );

        for(ItemEntity itemEntity : items){
            int col1 = this.getElementType().particleColourPrimary();
            int col2 = this.getElementType().particleColourFaded();
            var genericParticle = genericParticleOptions(MAGIC_PARTICLE_SELECTION, 8,2f, col1, col2, false);

            ParticleHandlers.invisibleLight(genericProjectile.level(), itemEntity.position().add(0,0.5,0), genericParticle, 0.03, 0.04, 8);
            if (player != null) {
                var isPicked = handlePlayerPickup(itemEntity, player.getInventory(), player);
                if(isPicked){
                    if(!pickedUpItem) pickedUpItem = true;
                }
            } else {
                var entity = this.genericProjectile;
                var pos = entity.blockEntityPos;
                if(pos == null) return;
                var level = entity.level();
                var bE = level.getBlockEntity(BlockPos.containing(pos));
                if(bE instanceof ModularChaosCubeEntity autoEntity){
                    autoEntity.externalOutputInventory(level, itemEntity);
                }
            }
        };

        if(pickedUpItem && player instanceof ServerPlayer player1) {
            ModHelpers.sendClientSound(player1, SoundEvents.ITEM_PICKUP, 0.5f, 1);
        }
        super.onBlockBlockHit(blockHitResult);
        genericProjectile.discard();
    }

    private static boolean handlePlayerPickup(ItemEntity itemEntity, Container inv, Player player) {
        if(inv == null) return false;
        var entityStack = itemEntity.getItem();
        int maxStackSize = 64;
        int remainingAmount = entityStack.getCount();
        var availSlots = inv.getContainerSize() - 5;
        var pickedUpItems = false;
        for (int i = 0; i < availSlots; i++) {
            if (remainingAmount <= 0) break;
            var slotStack = inv.getItem(i);
            int slotSpace = maxStackSize - slotStack.getCount(); // Use maxStackSize variable
            boolean isStack = slotStack.getItem() == entityStack.getItem() && slotStack.getCount() < maxStackSize;
            boolean emptySlot = slotStack.isEmpty();

            if (isStack) {
                int addAmount = Math.min(remainingAmount, slotSpace);
                slotStack.grow(addAmount);
                remainingAmount -= addAmount;
                entityStack.shrink(addAmount);
                pickedUpItems = true;
            } else if (emptySlot) {
                int addAmount = Math.min(remainingAmount, maxStackSize);
                inv.setItem(i, entityStack.copy().split(addAmount));
                remainingAmount -= addAmount;
                entityStack.shrink(addAmount);
                pickedUpItems = true;
            } else if (player.isCreative()) {
                player.addItem(entityStack);
                pickedUpItems = true;
            }
        }

        return pickedUpItems;
    }

    public void moveItems(Level level, ItemEntity itemEntity, BlockPos getPos){
        var handler = getItemHandlerAt(level, getPos.getX(), getPos.getY(), getPos.getZ(), Direction.UP);
        handler.ifPresent(
            iItemHandlerObjectPair -> {
                var entityStack = itemEntity.getItem();
                int remainingAmount = entityStack.getCount();
                var itemHandler = iItemHandlerObjectPair.getKey();
                for(int i = 0; i < itemHandler.getSlots(); i++){
                    int maxStackSize = itemHandler.getSlotLimit(i);
                    if (remainingAmount <= 0) break;
                    var slotStack = itemHandler.getStackInSlot(i);
                    int slotSpace = maxStackSize - slotStack.getCount(); // Use maxStackSize variable
                    boolean isStack = slotStack.getItem() == entityStack.getItem() && slotStack.getCount() < maxStackSize;
                    boolean emptySlot = slotStack.isEmpty();

                    if (isStack) {
                        int addAmount = Math.min(remainingAmount, slotSpace);
                        slotStack.grow(addAmount);
                        remainingAmount -= addAmount;
                        entityStack.shrink(addAmount);
                    } else if (emptySlot) {
                        int addAmount = Math.min(remainingAmount, maxStackSize);
                        itemHandler.insertItem(i, entityStack.copy().split(addAmount), false);
                        remainingAmount -= addAmount;
                        entityStack.shrink(addAmount);
                    }
                }
            }
        );
    }

    public static Optional<Pair<IItemHandler, Object>> getItemHandlerAt(Level worldIn, double x, double y, double z, Direction side) {
        BlockPos blockpos = BlockPos.containing(x, y, z);
        BlockState state = worldIn.getBlockState(blockpos);
        BlockEntity blockEntity = state.hasBlockEntity() ? worldIn.getBlockEntity(blockpos) : null;
        IItemHandler blockCap = worldIn.getCapability(Capabilities.ItemHandler.BLOCK, blockpos, state, blockEntity, side);
        if (blockCap != null) {
            return Optional.of(ImmutablePair.of(blockCap, blockEntity));
        } else {
            List<Entity> list = worldIn.getEntities((Entity)null, new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5), EntitySelector.ENTITY_STILL_ALIVE);
            if (!list.isEmpty()) {
                Collections.shuffle(list);
                for (Entity entity : list) {
                    IItemHandler entityCap = entity.getCapability(Capabilities.ItemHandler.ENTITY_AUTOMATION, side);
                    if (entityCap != null) {
                        return Optional.of(ImmutablePair.of(entityCap, entity));
                    }
                }
            }
            return Optional.empty();
        }
    }
}
