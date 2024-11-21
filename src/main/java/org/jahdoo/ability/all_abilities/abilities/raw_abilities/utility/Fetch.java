package org.jahdoo.ability.all_abilities.abilities.raw_abilities.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.ability.AbstractUtilityProjectile;
import org.jahdoo.ability.DefaultEntityBehaviour;
import org.jahdoo.ability.all_abilities.abilities.Utility.FetchAbility;
import org.jahdoo.block.modular_chaos_cube.ModularChaosCubeEntity;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.utils.ModHelpers;

import java.util.List;

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

    @Override
    public String abilityId() {
        return FetchAbility.abilityId.getPath().intern();
    }
}
