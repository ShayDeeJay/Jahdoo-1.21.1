package org.jahdoo.common.block.shopping_table;

import net.minecraft.core.Direction;

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
