package org.jahdoo.trial_nexus.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jahdoo.common.registers.AttachmentReg;

public class ChaosCubeData implements IAttachment {
    BlockPos actionDirection;
    BlockPos inputInvDirection;
    BlockPos outputInvDirection;
    boolean active;
    private int speed;
    private BlockPos worldPosition;
    private boolean chained;

    public ChaosCubeData(){}

    public ChaosCubeData(
        BlockPos actionDirection,
        BlockPos inputInvDirection,
        BlockPos outputInvDirection,
        boolean active,
        int speed,
        BlockPos worldPosition,
        boolean chained
    ){
        this.actionDirection = actionDirection;
        this.inputInvDirection = inputInvDirection;
        this.outputInvDirection = outputInvDirection;
        this.active = active;
        this.speed = speed;
        this.worldPosition = worldPosition;
        this.chained = chained;
    }

    public BlockPos worldPosition(){
        return this.worldPosition;
    }

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

    public boolean chained(){
        return this.chained;
    }

    public int speed(){
        return this.speed;
    }

    public static BlockPos getActionDirection(BlockEntity entity){
        return entity.getData(AttachmentReg.MODULAR_CHAOS_CUBE).actionDirection;
    }

    public static boolean getActive(BlockEntity entity){
        return entity.getData(AttachmentReg.MODULAR_CHAOS_CUBE).active;
    }

    public static boolean getChained(BlockEntity entity){
        return entity.getData(AttachmentReg.MODULAR_CHAOS_CUBE).chained;
    }

    public static int getSpeed(BlockEntity entity){
        return entity.getData(AttachmentReg.MODULAR_CHAOS_CUBE).speed;
    }


    public static ChaosCubeData initData(BlockPos blockPos){
        return new ChaosCubeData(blockPos.north(), blockPos.above(), blockPos.below(), false, 100, blockPos, true);
    }

    public ChaosCubeData updateActionDirection(BlockPos actionDirection){
        return new ChaosCubeData(actionDirection, this.inputInvDirection, this.outputInvDirection, this.active, this.speed, this.worldPosition, this.chained);
    }

    public ChaosCubeData updateActive(boolean active){
        return new ChaosCubeData(this.actionDirection, this.inputInvDirection, this.outputInvDirection, active, this.speed, this.worldPosition, this.chained);
    }

    public ChaosCubeData updateInput(BlockPos inputInvDirection){
        return new ChaosCubeData(this.actionDirection, inputInvDirection, this.outputInvDirection, this.active, this.speed, this.worldPosition, this.chained);
    }

    public ChaosCubeData updateOutput(BlockPos outputInvDirection){
        return new ChaosCubeData(this.actionDirection, this.inputInvDirection, outputInvDirection, this.active, this.speed, this.worldPosition, this.chained);
    }

    public ChaosCubeData updateSpeed(int speed){
        return new ChaosCubeData(this.actionDirection, this.inputInvDirection, this.outputInvDirection, this.active, speed, this.worldPosition, this.chained);
    }

    public ChaosCubeData updateChained(boolean updateChained){
        return new ChaosCubeData(this.actionDirection, this.inputInvDirection, this.outputInvDirection, this.active, speed, this.worldPosition, updateChained);
    }

    public static ChaosCubeData updateAll(BlockPos action, BlockPos input, BlockPos output, boolean active, int speed, BlockPos worldPosition, boolean chained){
        return new ChaosCubeData(action, input, output, active, speed, worldPosition, chained);
    }

    public static void setActionDirection(BlockEntity entity, BlockPos actionDirection){
        var auto = entity.getData(AttachmentReg.MODULAR_CHAOS_CUBE);
        auto.updateActionDirection(actionDirection);
        entity.setChanged();
    }

    public static BlockPos getRelativePosition(Direction direction, BlockPos blockPos) {
        return switch (direction) {
            case NORTH -> blockPos.north();
            case SOUTH -> blockPos.south();
            case EAST -> blockPos.east();
            case WEST -> blockPos.west();
            case UP -> blockPos.above();
            case DOWN -> blockPos.below();
        };
    }

    public static final StreamCodec<FriendlyByteBuf, ChaosCubeData> STREAM_CODEC = StreamCodec.ofMember(
        ChaosCubeData::serialise,
        ChaosCubeData::deserialise
    );

    private void serialise(FriendlyByteBuf friendlyByteBuf){
        friendlyByteBuf.writeBlockPos(actionDirection);
        friendlyByteBuf.writeBlockPos(inputInvDirection);
        friendlyByteBuf.writeBlockPos(outputInvDirection);
        friendlyByteBuf.writeBoolean(active);
        friendlyByteBuf.writeInt(speed);
        friendlyByteBuf.writeBlockPos(worldPosition);
        friendlyByteBuf.writeBoolean(chained);
    }

    private static ChaosCubeData deserialise(FriendlyByteBuf friendlyByteBuf){
        return new ChaosCubeData(
            friendlyByteBuf.readBlockPos(),
            friendlyByteBuf.readBlockPos(),
            friendlyByteBuf.readBlockPos(),
            friendlyByteBuf.readBoolean(),
            friendlyByteBuf.readInt(),
            friendlyByteBuf.readBlockPos(),
            friendlyByteBuf.readBoolean()
        );
    }

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.put("world_pos", NbtUtils.writeBlockPos(worldPosition));
        nbt.put("direction", NbtUtils.writeBlockPos(actionDirection));
        nbt.put("inpInv", NbtUtils.writeBlockPos(inputInvDirection));
        nbt.put("outInv", NbtUtils.writeBlockPos(outputInvDirection));
        nbt.putBoolean("chained", chained);
        nbt.putBoolean("active", active);
        nbt.putInt("speed", speed);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        NbtUtils.readBlockPos(nbt, "world_pos").ifPresent(direction -> worldPosition = direction);
        NbtUtils.readBlockPos(nbt, "direction").ifPresent(direction -> actionDirection = direction);
        NbtUtils.readBlockPos(nbt, "inpInv").ifPresent(direction -> inputInvDirection = direction);
        NbtUtils.readBlockPos(nbt, "outInv").ifPresent(direction -> outputInvDirection = direction);
        chained = nbt.getBoolean("chained");
        active = nbt.getBoolean("active");
        this.speed = nbt.getInt("speed");
    }

    public static final Codec<ChaosCubeData> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            BlockPos.CODEC.fieldOf("action_direction").forGetter(ChaosCubeData::action),
            BlockPos.CODEC.fieldOf("input_direction").forGetter(ChaosCubeData::input),
            BlockPos.CODEC.fieldOf("output_direction").forGetter(ChaosCubeData::output),
            Codec.BOOL.fieldOf("active").forGetter(ChaosCubeData::active),
            Codec.INT.fieldOf("speed").forGetter(ChaosCubeData::speed),
            BlockPos.CODEC.fieldOf("world_pos").forGetter(ChaosCubeData::worldPosition),
            Codec.BOOL.fieldOf("chained").forGetter(ChaosCubeData::chained)
        ).apply(instance, ChaosCubeData::new)
    );

    public Direction getDirection(BlockPos blockPos) {
        if (blockPos.equals(this.worldPosition.north())) {
            return Direction.NORTH;
        } else if (blockPos.equals(this.worldPosition.south())) {
            return Direction.SOUTH;
        } else if (blockPos.equals(this.worldPosition.east())) {
            return Direction.EAST;
        } else if (blockPos.equals(this.worldPosition.west())) {
            return Direction.WEST;
        } else if (blockPos.equals(this.worldPosition.above())) {
            return Direction.UP;
        } else  {
            return Direction.DOWN;
        }
    }
}
