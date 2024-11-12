package org.jahdoo.all_magic.all_abilities.abilities.raw_abilities.utility;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.jahdoo.all_magic.AbstractUtilityProjectile;
import org.jahdoo.all_magic.DefaultEntityBehaviour;
import org.jahdoo.block.automation_block.AutomationBlockEntity;
import org.jahdoo.particle.ParticleHandlers;
import org.jahdoo.utils.ModHelpers;

import java.util.List;

import static org.jahdoo.particle.ParticleHandlers.genericParticleOptions;
import static org.jahdoo.particle.ParticleStore.MAGIC_PARTICLE_SELECTION;
import static org.jahdoo.particle.ParticleStore.SOFT_PARTICLE_SELECTION;

public class Fetch extends AbstractUtilityProjectile {
    ResourceLocation abilityId = ModHelpers.modResourceLocation("fetch_property");

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
        if(this.genericProjectile.level().getBlockEntity(blockHitResult.getBlockPos()) instanceof AutomationBlockEntity) return;
        var player = (Player) genericProjectile.getOwner();
//        if(player == null) return;
//        var replaceBlock = Block.byItem(player.getOffhandItem().getItem());
        var blockPos = blockHitResult.getBlockPos();
        var side = blockHitResult.getDirection();

        List<ItemEntity> items = this.genericProjectile.level().getEntitiesOfClass(
            ItemEntity.class,
            this.genericProjectile.getBoundingBox().inflate(11,3,11),
            entity -> true
        );

        for(ItemEntity itemEntity : items){
            int col1 = this.getElementType().particleColourPrimary();
            int col2 = this.getElementType().particleColourFaded();
            var genericParticle = genericParticleOptions(MAGIC_PARTICLE_SELECTION, 8,2f, col1, col2, false);

            ParticleHandlers.invisibleLight(genericProjectile.level(), itemEntity.position().add(0,0.5,0), genericParticle, 0.03, 0.04, 8);
            var pos = player != null ? player.position() : this.genericProjectile.blockEntityPos.add(0,1,0);
            var itemStack = itemEntity.getItem();
            itemEntity.teleportTo(pos.x, pos.y, pos.z);
        };

        super.onBlockBlockHit(blockHitResult);
        genericProjectile.discard();
    }
}
