package org.jahdoo.ascension.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractHoldUseAttachment implements IAttachment {

    protected boolean startedUsing;

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putBoolean("started_using", startedUsing);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        this.startedUsing = nbt.getBoolean("started_using");
    }

    public void setStartedUsing(boolean startedUsing) {
        this.startedUsing = startedUsing;
    }

    public void onTickMethod(Player player){
        if(!player.isUsingItem()) {
            startedUsing = false;
        }
    }
}
