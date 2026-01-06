package org.jahdoo.trial_nexus.attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jahdoo.common.registers.AttachmentReg;

public class ChaosCubeData implements IAttachment {
    String actionDirection;
    String inputInvDirection;
    String outputInvDirection;
    boolean active;
    private int speed;
    private boolean chained;

    public ChaosCubeData() {}

    public ChaosCubeData(
        String actionDirection,
        String inputInvDirection,
        String outputInvDirection,
        boolean active,
        int speed,
        boolean chained
    ) {
        this.actionDirection = actionDirection;
        this.inputInvDirection = inputInvDirection;
        this.outputInvDirection = outputInvDirection;
        this.active = active;
        this.speed = speed;
        this.chained = chained;
    }

    public String action() {
        return this.actionDirection;
    }

    public String input() {
        return this.inputInvDirection;
    }

    public String output() {
        return this.outputInvDirection;
    }

    public boolean active() {
        return this.active;
    }

    public boolean chained() {
        return this.chained;
    }

    public int speed() {
        return this.speed;
    }

    public static ChaosCubeData initData() {
        return new ChaosCubeData("north", "up", "down", false, 100, true);
    }

    public ChaosCubeData updateActionDirection(String actionDirection) {
        return new ChaosCubeData(
            actionDirection,
            this.inputInvDirection,
            this.outputInvDirection,
            this.active,
            this.speed,
            this.chained
        );
    }

    public ChaosCubeData updateActive(boolean active) {
        return new ChaosCubeData(
            this.actionDirection,
            this.inputInvDirection,
            this.outputInvDirection,
            active,
            this.speed,
            this.chained
        );
    }

    public ChaosCubeData updateInput(String inputInvDirection) {
        return new ChaosCubeData(
            this.actionDirection,
            inputInvDirection,
            this.outputInvDirection,
            this.active,
            this.speed,
            this.chained
        );
    }

    public ChaosCubeData updateOutput(String outputInvDirection) {
        return new ChaosCubeData(
            this.actionDirection,
            this.inputInvDirection,
            outputInvDirection,
            this.active,
            this.speed,
            this.chained
        );
    }

    public ChaosCubeData updateSpeed(int speed) {
        return new ChaosCubeData(
            this.actionDirection,
            this.inputInvDirection,
            this.outputInvDirection,
            this.active,
            speed,
            this.chained
        );
    }

    public ChaosCubeData updateChained(boolean updateChained) {
        return new ChaosCubeData(
            this.actionDirection,
            this.inputInvDirection,
            this.outputInvDirection,
            this.active,
            this.speed,
            updateChained
        );
    }

    public static ChaosCubeData updateAll(
        String action,
        String input,
        String output,
        boolean active,
        int speed,
        boolean chained
    ) {
        return new ChaosCubeData(action, input, output, active, speed, chained);
    }

    public static void setActionDirection(BlockEntity entity, String actionDirection) {
        var data = entity.getData(AttachmentReg.MODULAR_CHAOS_CUBE);
        data.updateActionDirection(actionDirection);
        entity.setChanged();
    }

    public static final StreamCodec<FriendlyByteBuf, ChaosCubeData> STREAM_CODEC =
        StreamCodec.ofMember(
            ChaosCubeData::serialise,
            ChaosCubeData::deserialise
        );

    private void serialise(FriendlyByteBuf buf) {
        buf.writeUtf(actionDirection);
        buf.writeUtf(inputInvDirection);
        buf.writeUtf(outputInvDirection);
        buf.writeBoolean(active);
        buf.writeInt(speed);
        buf.writeBoolean(chained);
    }

    private static ChaosCubeData deserialise(FriendlyByteBuf buf) {
        return new ChaosCubeData(
            buf.readUtf(),
            buf.readUtf(),
            buf.readUtf(),
            buf.readBoolean(),
            buf.readInt(),
            buf.readBoolean()
        );
    }

    @Override
    public void saveNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putString("direction", actionDirection);
        nbt.putString("inpInv", inputInvDirection);
        nbt.putString("outInv", outputInvDirection);
        nbt.putBoolean("chained", chained);
        nbt.putBoolean("active", active);
        nbt.putInt("speed", speed);
    }

    @Override
    public void loadNBTData(CompoundTag nbt, HolderLookup.Provider provider) {
        actionDirection = nbt.getString("direction");
        inputInvDirection = nbt.getString("inpInv");
        outputInvDirection = nbt.getString("outInv");
        chained = nbt.getBoolean("chained");
        active = nbt.getBoolean("active");
        speed = nbt.getInt("speed");
    }

    public static void saveChaosData(CompoundTag nbt, ChaosCubeData data) {
        CompoundTag dataTag = new CompoundTag();

        dataTag.putString("direction", data.actionDirection);
        dataTag.putString("inpInv", data.inputInvDirection);
        dataTag.putString("outInv", data.outputInvDirection);
        dataTag.putBoolean("chained", data.chained());
        dataTag.putBoolean("active", data.active());
        dataTag.putInt("speed", data.speed());

        nbt.put("chaos_cube_data", dataTag);
    }

    public static ChaosCubeData loadChaosData(CompoundTag nbt) {
        if (!nbt.contains("chaos_cube_data")) return null;

        CompoundTag dataTag = nbt.getCompound("chaos_cube_data");

        return new ChaosCubeData(
            dataTag.getString("direction"),
            dataTag.getString("inpInv"),
            dataTag.getString("outInv"),
            dataTag.getBoolean("active"),
            dataTag.getInt("speed"),
            dataTag.getBoolean("chained")
        );
    }

    public static final Codec<ChaosCubeData> CODEC =
        RecordCodecBuilder.create(instance ->
            instance.group(
                Codec.STRING.fieldOf("action_direction").forGetter(ChaosCubeData::action),
                Codec.STRING.fieldOf("input_direction").forGetter(ChaosCubeData::input),
                Codec.STRING.fieldOf("output_direction").forGetter(ChaosCubeData::output),
                Codec.BOOL.fieldOf("active").forGetter(ChaosCubeData::active),
                Codec.INT.fieldOf("speed").forGetter(ChaosCubeData::speed),
                Codec.BOOL.fieldOf("chained").forGetter(ChaosCubeData::chained)
            ).apply(instance, ChaosCubeData::new)
        );

    @Override
    public String toString() {
        return "ChaosCubeData{" +
            "actionDirection='" + actionDirection + '\'' +
            ", inputInvDirection='" + inputInvDirection + '\'' +
            ", outputInvDirection='" + outputInvDirection + '\'' +
            ", active=" + active +
            ", speed=" + speed +
            ", chained=" + chained +
            '}';
    }
}
