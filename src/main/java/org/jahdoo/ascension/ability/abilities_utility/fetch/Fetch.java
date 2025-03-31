package org.jahdoo.ascension.ability.abilities_utility.fetch;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.ascension.ability.AbstractUtilityProjectile;
import org.jahdoo.ascension.ability.DefaultEntityBehaviour;
import org.jahdoo.common.block.chaos_cube.ChaosCubeEntity;
import org.jahdoo.common.entities.generic_projectile.GenericProjectile;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.ascension.utils.Helpers;

import java.util.List;

import static org.jahdoo.ascension.ability.AbilityBuilder.*;
import static org.jahdoo.common.particle.ParticleHandlers.genericParticle;
import static org.jahdoo.common.particle.ParticleStore.MAGIC_PARTICLE;

public class Fetch extends AbstractUtilityProjectile {

     private static final ResourceLocation abilityId = Helpers.res("fetch_property");
     private int range;

    @Override
    public ResourceLocation getAbilityResource() {
        return abilityId;
    }

    @Override
    public DefaultEntityBehaviour getEntityProperty() {
        return new Fetch();
    }

    @Override
    public String abilityId() {
        return FetchAbility.abilityId.getPath().intern();
    }

    @Override
    public void getGenericProjectile(GenericProjectile genericProjectile) {
        super.getGenericProjectile(genericProjectile);
        this.range = (int) this.getTagUtility(RANGE);
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
    public void onBlockBlockHit(BlockHitResult blockHitResult) {
        super.onBlockBlockHit(blockHitResult);
        if(this.generic.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof ChaosCubeEntity) return;
        var player = (Player) generic.getOwner();
        var pickedUpItem = false;

        List<ItemEntity> items = this.generic.level().getEntitiesOfClass(
            ItemEntity.class,
            this.generic.getBoundingBox().inflate(range, range, range),
            entity -> true
        );

        for(ItemEntity itemEntity : items){
            int col1 = this.getElementType().partColourA();
            int col2 = this.getElementType().partColourFade();
            var genericParticle = ParticleHandlers.genericParticle(MAGIC_PARTICLE, 8,2f, col1, col2, false);

            ParticleHandlers.invisibleLight(generic.level(), itemEntity.position().add(0,0.5,0), genericParticle, 0.03, 0.04, 8);
            if (player != null) {
                var isPicked = handlePlayerPickup(itemEntity, player.getInventory(), player);
                if(isPicked){
                    if(!pickedUpItem) pickedUpItem = true;
                }
            } else {
                var entity = this.generic;
                var pos = entity.blockEntityPos;
                if(pos == null) return;
                var level = entity.level();
                var bE = level.getBlockEntity(BlockPos.containing(pos));
                if(bE instanceof ChaosCubeEntity autoEntity){
                    autoEntity.externalOutputInventory(level, itemEntity);
                }
            }
        };

        if(pickedUpItem && player instanceof ServerPlayer player1) {
            Helpers.sendClientSound(player1, SoundEvents.ITEM_PICKUP, 1.15f, 1);
        }
        generic.discard();
    }

}
