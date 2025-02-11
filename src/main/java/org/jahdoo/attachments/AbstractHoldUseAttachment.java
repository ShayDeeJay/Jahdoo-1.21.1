package org.jahdoo.attachments;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.registers.DataComponentRegistry.WAND_DATA;

public abstract class AbstractHoldUseAttachment implements AbstractAttachment{

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
        var getValue = ModHelpers.getUsedItem(player).get(WAND_DATA);

        if(!player.isUsingItem() || getValue == null) {
            startedUsing = false;
            return;
        }

        var x = 1;
    }
}
