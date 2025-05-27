package org.jahdoo.common.entities.custom_villager;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jahdoo.common.registers.EntityReg;

public class CustomVillager extends Villager {

    public CustomVillager(EntityType<? extends Villager> entityType, Level level) {
        super(entityType, level);
    }

    public CustomVillager(Level level) {
        super(EntityReg.CUSTOM_VILLAGER.get(), level);
    }

    @Override
    public void openTradingScreen(Player player, Component displayName, int level) {}

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(player.level().isClientSide){
            player.sendSystemMessage(Component.literal("Look what i have for sale."));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.removeFreeWill();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
    }
}
