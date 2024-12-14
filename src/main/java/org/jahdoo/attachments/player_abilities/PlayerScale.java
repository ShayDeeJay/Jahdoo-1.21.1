package org.jahdoo.attachments.player_abilities;

import com.google.common.collect.HashMultimap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jahdoo.attachments.AbstractAttachment;
import org.jahdoo.utils.ModHelpers;

import static org.jahdoo.registers.AttachmentRegister.PLAYER_SCALE;

public class PlayerScale implements AbstractAttachment {

    private ServerPlayer serverPlayer;
    private float scaledValue;
    private final ResourceLocation effectId = ModHelpers.res("jahdoo_scale_player");
    private final HashMultimap<Holder<Attribute>, AttributeModifier> multiMap = HashMultimap.create();

    public PlayerScale(ServerPlayer serverPlayer){
        this.serverPlayer = serverPlayer;
    }

    public PlayerScale(){}

    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putFloat(effectId.getPath().intern(), this.scaledValue);
    }

    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        var num = nbt.getFloat(effectId.getPath().intern());
        setToggleEffect(serverPlayer, num);
    }

    public static void setToggleEffect(Player player, float scaleValue){
        var playerData = player.getData(PLAYER_SCALE);
        playerData.setScaleValue(scaleValue);
        playerData.toggleScale(player);
    }

    public static void staticTickEvent(Player player){
        player.getData(PLAYER_SCALE).onTick(player);
    }

    public void onTick(Player player){
        var attribute = player.getAttributes();
        if(player.isDeadOrDying() && this.hasAttributeInst(player)){
            attribute.removeAttributeModifiers(multiMap);
//            this.setScaleValue(0);
        }
    }

    public void setScaleValue(float scaleValue){
        multiMap.clear();
        this.scaledValue = scaleValue;
        var scaledValue = new AttributeModifier(this.effectId, this.scaledValue, AttributeModifier.Operation.ADD_VALUE);
        multiMap.put(Attributes.SCALE, scaledValue);
    }

    private boolean hasAttributeInst(Player player){
        var attribute = player.getAttributes();
        return attribute.hasModifier(Attributes.SCALE, this.effectId);
    }

    public void toggleScale(Player player){
        var attribute = player.getAttributes();
        if(this.hasAttributeInst(player)) {
            attribute.removeAttributeModifiers(multiMap);
        } else {
            attribute.addTransientAttributeModifiers(multiMap);
        }
    }

}
