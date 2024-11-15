package org.jahdoo.capabilities.player_abilities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jahdoo.capabilities.AbstractAttachment;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.registers.AttachmentRegister;

public class AutoBlock implements AbstractAttachment {

    BlockPos actionDirection;
    BlockPos inputInvDirection;
    BlockPos outputInvDirection;
    boolean active;
    private int speed;

    public AutoBlock(){}

    public AutoBlock(BlockPos actionDirection, BlockPos inputInvDirection, BlockPos outputInvDirection, boolean active, int speed){
        this.actionDirection = actionDirection;
        this.inputInvDirection = inputInvDirection;
        this.outputInvDirection = outputInvDirection;
        this.active = active;
        this.speed = speed;
    }

    public static AutoBlock initData(BlockPos blockPos){
        return new AutoBlock(blockPos.north(), blockPos.above(), blockPos.below(), false, 100);
    }

    public AutoBlock updateActionDirection(BlockPos actionDirection){
        return new AutoBlock(actionDirection, this.inputInvDirection, this.outputInvDirection, this.active, this.speed);
    }

    public AutoBlock updateActive(boolean active){
        return new AutoBlock(this.actionDirection, this.inputInvDirection, this.outputInvDirection, active, this.speed);
    }

    public AutoBlock updateInput(BlockPos inputInvDirection){
        return new AutoBlock(this.actionDirection, inputInvDirection, this.outputInvDirection, this.active, this.speed);
    }

    public AutoBlock updateOutput(BlockPos outputInvDirection){
        return new AutoBlock(this.actionDirection, this.inputInvDirection, outputInvDirection, this.active, this.speed);
    }

    public AutoBlock updateSpeed(int speed){
        return new AutoBlock(this.actionDirection, this.inputInvDirection, this.outputInvDirection, this.active, speed);
    }

    public static void setActionDirection(BlockEntity entity, BlockPos actionDirection){
        var auto = entity.getData(AttachmentRegister.AUTO_BLOCK);
        auto.updateActionDirection(actionDirection);
        entity.setChanged();
    }

    private void serialise(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeBlockPos(actionDirection);
        friendlyByteBuf.writeBlockPos(inputInvDirection);
        friendlyByteBuf.writeBlockPos(outputInvDirection);
        friendlyByteBuf.writeBoolean(active);
        friendlyByteBuf.writeInt(speed);
    }

    private static AutoBlock deserialise(FriendlyByteBuf friendlyByteBuf){
        return new AutoBlock(
            friendlyByteBuf.readBlockPos(),
            friendlyByteBuf.readBlockPos(),
            friendlyByteBuf.readBlockPos(),
            friendlyByteBuf.readBoolean(),
            friendlyByteBuf.readInt()
        );
    }

    private static final StreamCodec<FriendlyByteBuf, AutoBlock> STREAM_CODEC = StreamCodec.ofMember(
        AutoBlock::serialise,
        AutoBlock::deserialise
    );

    public BlockPos action(){
        return this.actionDirection;
    }

    public BlockPos input(){
        return this.inputInvDirection;
    }

    public BlockPos output(){
        return this.outputInvDirection;
    }

    public boolean active(){
        return this.active;
    }

    public int speed(){
        return this.speed;
    }

    public static final Codec<AutoBlock> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BlockPos.CODEC.fieldOf("action_direction").forGetter(AutoBlock::action),
            BlockPos.CODEC.fieldOf("input_direction").forGetter(AutoBlock::input),
            BlockPos.CODEC.fieldOf("output_direction").forGetter(AutoBlock::output),
            Codec.BOOL.fieldOf("active").forGetter(AutoBlock::active),
            Codec.INT.fieldOf("speed").forGetter(AutoBlock::speed)
        ).apply(instance, AutoBlock::new)
    );

    public static BlockPos getActionDirection(BlockEntity entity){
        return entity.getData(AttachmentRegister.AUTO_BLOCK).actionDirection;
    }

    public static boolean getActive(BlockEntity entity){
        return entity.getData(AttachmentRegister.AUTO_BLOCK).active;
    }

    public static int getSpeed(BlockEntity entity){
        return entity.getData(AttachmentRegister.AUTO_BLOCK).speed;
    }

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.put("direction", NbtUtils.writeBlockPos(actionDirection));
        nbt.put("inpInv", NbtUtils.writeBlockPos(inputInvDirection));
        nbt.put("outInv", NbtUtils.writeBlockPos(outputInvDirection));
        nbt.putBoolean("active", active);
        nbt.putInt("speed", speed);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        NbtUtils.readBlockPos(nbt, "direction").ifPresent(direction -> actionDirection = direction);
        NbtUtils.readBlockPos(nbt, "inpInv").ifPresent(direction -> inputInvDirection = direction);
        NbtUtils.readBlockPos(nbt, "outInv").ifPresent(direction -> outputInvDirection = direction);
        active = nbt.getBoolean("active");
        this.speed = nbt.getInt("speed");
    }
}
