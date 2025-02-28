package org.jahdoo.common.block.shopping_table;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public record DisplayDirection(
    int direction,
    double x,
    double z
){
    public static final DisplayDirection NORTH = new DisplayDirection(0, 0.5, 0);
    public static final DisplayDirection SOUTH = new DisplayDirection(180, 0.5, 1);
    public static final DisplayDirection EAST = new DisplayDirection(90, 1, 0.5);
    public static final DisplayDirection WEST = new DisplayDirection(270, 0, 0.5);
    private static final DisplayDirection[] DIRECTIONS = {NORTH, EAST, SOUTH, WEST};

    public static void writeToNBT(DisplayDirection displayDirection, CompoundTag tag) {
        if(displayDirection != null){
            tag.putInt("Direction", displayDirection.direction());
            tag.putDouble("X", displayDirection.x());
            tag.putDouble("Z", displayDirection.z());
        }
    }

    public static DisplayDirection readFromNBT(CompoundTag tag) {
        int direction = tag.getInt("Direction");
        double x = tag.getDouble("X");
        double z = tag.getDouble("Z");
        return new DisplayDirection(direction, x, z);
    }

    public DisplayDirection rotate() {
        for (int i = 0; i < DIRECTIONS.length; i++) {
            if (this.equals(DIRECTIONS[i])) {
                return DIRECTIONS[(i + 1) % DIRECTIONS.length];
            }
        }

        return NORTH;
    }

    public static DisplayDirection fromMCDirection(Direction mcDirection) {
        return switch (mcDirection) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            default -> throw new IllegalArgumentException("Unsupported direction: " + mcDirection);
        };
    }
}
