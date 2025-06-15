package org.jahdoo.common.block.loot_pot;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.ticks.ContainerSingleItem;
import org.jahdoo.common.block.SyncedBlockEntity;
import org.jahdoo.common.items.KeyItem;
import org.jahdoo.common.particle.ParticleHandlers;
import org.jahdoo.common.particle.ParticleStore;
import org.jahdoo.common.registers.BlockEntityReg;

import javax.annotation.Nullable;
import java.util.List;

import static org.jahdoo.common.block.loot_pot.LootPotBlock.TEXTURE;
import static org.jahdoo.trial_nexus.utils.Helpers.Random;

public class LootPotBlockEntity extends SyncedBlockEntity implements RandomizableContainer, ContainerSingleItem.BlockContainerSingleItem {

    private PotDecorations decorations;
    private ItemStack item;
    private ResourceKey<LootTable> lootTable;
    private long lootTableSeed;

    public LootPotBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityReg.LOOT_POT_BE.get(), pos, state);
        this.item = ItemStack.EMPTY;
        this.decorations = PotDecorations.EMPTY;
    }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.decorations.save(tag);
        if (!this.trySaveLootTable(tag) && !this.item.isEmpty()) {
            tag.put("item", this.item.save(registries));
        }
    }

    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.decorations = PotDecorations.load(tag);
        if (!this.tryLoadLootTable(tag)) {
            if (tag.contains("item", 10)) {
                this.item = ItemStack.parse(registries, tag.getCompound("item")).orElse(ItemStack.EMPTY);
            } else {
                this.item = ItemStack.EMPTY;
            }
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {

        if (level instanceof ServerLevel serverlevel && Random.nextInt(4) == 0) {
            var green = KeyItem.getLootRarity(state.getValue(TEXTURE)).getColour();
            var dustPlume = ParticleHandlers.genericParticle(ParticleStore.SOFT_PARTICLE, green, green, 10, 1, false, 1);
            var posX = (double) pos.getX() + Random.nextDouble(0.40, 0.60);
            var posY = (double) pos.getY() + 1.2;
            var posZ = (double) pos.getZ() + Random.nextDouble(0.40, 0.60);
            serverlevel.sendParticles(dustPlume, posX, posY, posZ, 0, 0.0F, 1.0F, 0.0F, Random.nextFloat(0.02F, 0.1F));
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    @Nullable
    public ResourceKey<LootTable> getLootTable() {
        return this.lootTable;
    }

    public void setLootTable(@Nullable ResourceKey<LootTable> lootTable) {
        this.lootTable = lootTable;
    }

    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    public void setLootTableSeed(long seed) {
        this.lootTableSeed = seed;
    }

    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.POT_DECORATIONS, this.decorations);
        components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(this.item)));
    }

    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.decorations = componentInput.getOrDefault(DataComponents.POT_DECORATIONS, PotDecorations.EMPTY);
        this.item = componentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyOne();
    }

    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("sherds");
        tag.remove("item");
    }

    public ItemStack getTheItem() {
        this.unpackLootTable(null);
        return this.item;
    }

    public ItemStack splitTheItem(int amount) {
        this.unpackLootTable(null);
        ItemStack itemstack = this.item.split(amount);
        if (this.item.isEmpty()) {
            this.item = ItemStack.EMPTY;
        }

        return itemstack;
    }

    public void setTheItem(ItemStack item) {
        this.unpackLootTable(null);
        this.item = item;
    }

    public BlockEntity getContainerBlockEntity() {
        return this;
    }

}
