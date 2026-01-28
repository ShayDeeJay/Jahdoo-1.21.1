package org.jahdoo.trial_nexus.level_manager;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import java.util.*;

public class OrePileGenerator {

    private final int oreCount;
    private final int maxWidth;
    private final int maxHeight;
    private final BlockState oreBlock;
    private final RandomSource random;
    private final boolean checkValidPlacement;

    public OrePileGenerator(int oreCount, int maxWidth, int maxHeight,
                            BlockState oreBlock, RandomSource random,
                            boolean checkValidPlacement) {
        this.oreCount = oreCount;
        this.maxWidth = Math.min(maxWidth, 16);
        this.maxHeight = Math.min(maxHeight, 10);
        this.oreBlock = oreBlock;
        this.random = random;
        this.checkValidPlacement = checkValidPlacement;
    }

    /**
     * Main generation method - creates a pile that stacks properly
     */
    public void generatePile(Level level, BlockPos basePos) {
        // First, find the actual ground position (non-air below)
        BlockPos groundPos = findGroundPosition(level, basePos);
        if (groundPos == null) return;

        List<BlockPos> orePositions = new ArrayList<>();

        // Generate the pile shape
        generatePyramidPile(orePositions, groundPos);

        // Place blocks ensuring they're supported
        placeSupportedBlocks(level, orePositions);
    }

    /**
     * Find a valid ground position to start the pile
     */
    private BlockPos findGroundPosition(Level level, BlockPos startPos) {
        // Look downwards for solid ground
        for (int y = startPos.getY(); y > startPos.getY() - 10; y--) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            if (!level.isEmptyBlock(checkPos) && !level.getBlockState(checkPos).isAir()) {
                // Found ground, place first ore above it
                return checkPos.above();
            }
        }
        return null;
    }

    /**
     * Generate a pyramid-shaped pile (most realistic for loose ore)
     */
    private void generatePyramidPile(List<BlockPos> positions, BlockPos basePos) {
        int remainingOres = oreCount;

        // Layer 1: Base layer
        int baseLayerOres = Math.min(remainingOres, random.nextInt(6) + 4);
        generateLayer(positions, basePos, baseLayerOres, 1, 0);
        remainingOres -= positions.size();

        // Subsequent layers
        int layer = 1;
        while (remainingOres > 0 && layer < maxHeight) {
            int layerOres = Math.min(remainingOres,
                Math.max(2, random.nextInt(4) + (int)(baseLayerOres * (1.0 - (layer * 0.3)))));

            // Center position for this layer (slightly random offset)
            int offsetX = random.nextInt(3) - 1;
            int offsetZ = random.nextInt(3) - 1;
            BlockPos layerCenter = basePos.offset(offsetX, layer, offsetZ);

            generateLayer(positions, layerCenter, layerOres,
                Math.max(1, (int)(maxWidth * (1.0 - (layer * 0.25)))), layer);

            remainingOres -= (positions.size() - (oreCount - remainingOres));
            layer++;

            if (layer >= maxHeight) break;
        }
    }

    /**
     * Generate a single layer of the pile
     */
    private void generateLayer(List<BlockPos> positions, BlockPos center,
                               int targetCount, int maxRadius, int layer) {
        int placed = 0;
        int attempts = 0;

        // Try to place blocks in roughly circular/oval pattern
        while (placed < targetCount && attempts < targetCount * 3) {
            // Get random position within radius
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = random.nextDouble() * maxRadius;

            int dx = (int)(Math.cos(angle) * distance);
            int dz = (int)(Math.sin(angle) * distance);

            // Add some randomness to height within layer
            int dy = random.nextInt(2); // 0 or 1

            BlockPos candidate = center.offset(dx, dy, dz);

            // Check if this position is valid for this layer
            if (isValidLayerPosition(candidate, center, maxRadius, layer) &&
                !positions.contains(candidate)) {

                positions.add(candidate);
                placed++;
            }

            attempts++;
        }

        // If we didn't get enough, try more aggressive placement
        if (placed < targetCount) {
            generateFillPattern(positions, center, targetCount - placed, maxRadius, layer);
        }
    }

    /**
     * Fill pattern for when random placement isn't enough
     */
    private void generateFillPattern(List<BlockPos> positions, BlockPos center,
                                     int needed, int radius, int layer) {
        List<BlockPos> candidates = new ArrayList<>();

        // Generate all possible positions in radius
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // Skip positions too far from center (circular pattern)
                double distance = Math.sqrt(x*x + z*z);
                if (distance <= radius) {
                    for (int y = 0; y < 2; y++) { // Allow small height variation
                        BlockPos pos = center.offset(x, y, z);
                        if (!positions.contains(pos)) {
                            candidates.add(pos);
                        }
                    }
                }
            }
        }

        // Shuffle and add needed positions
        Collections.shuffle(candidates, new Random(random.nextLong()));
        int toAdd = Math.min(needed, candidates.size());
        positions.addAll(candidates.subList(0, toAdd));
    }

    /**
     * Check if position is valid for this layer
     */
    private boolean isValidLayerPosition(BlockPos pos, BlockPos center,
                                         int maxRadius, int layer) {
        // Check radius
        int dx = pos.getX() - center.getX();
        int dz = pos.getZ() - center.getZ();
        double distance = Math.sqrt(dx*dx + dz*dz);

        // Allow slightly larger radius at lower layers
        double allowedRadius = maxRadius * (1.0 + (1.0 / (layer + 1)));

        return distance <= allowedRadius;
    }

    /**
     * Place blocks ensuring they're properly supported
     */
    private void placeSupportedBlocks(Level level, List<BlockPos> positions) {
        // Sort positions by Y level (lowest first)
        positions.sort(Comparator.comparingInt(BlockPos::getY));

        // Track placed blocks to check support
        Set<BlockPos> placedBlocks = new HashSet<>();
        int placedCount = 0;

        for (BlockPos pos : positions) {
            if (placedCount >= oreCount) break;

            // Check if block can be placed here (air or replaceable)
            if (canPlaceBlock(level, pos)) {
                // Check for support - need solid block below or adjacent placed block
                if (hasSupport(level, pos, placedBlocks)) {
                    level.setBlock(pos, oreBlock, 2);
                    placedBlocks.add(pos);
                    placedCount++;
                }
            }
        }

        // If we didn't place enough, try to fill in gaps
        if (placedCount < oreCount) {
            fillRemainingOres(level, positions, placedBlocks, placedCount);
        }
    }

    /**
     * Check if block can be placed at position
     */
    private boolean canPlaceBlock(Level level, BlockPos pos) {
        BlockState current = level.getBlockState(pos);
        return current.isAir();
    }

    /**
     * Check if position has adequate support
     */
    private boolean hasSupport(Level level, BlockPos pos, Set<BlockPos> placedBlocks) {
        // Direct support from below
        BlockPos below = pos.below();
        if (!level.isEmptyBlock(below) && !level.getBlockState(below).isAir()) {
            return true;
        }

        // Support from placed block below
        if (placedBlocks.contains(below)) {
            return true;
        }

        // Support from adjacent placed blocks at same level (for spreading)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                BlockPos adjacent = pos.offset(dx, 0, dz);
                if (placedBlocks.contains(adjacent)) {
                    // Check if adjacent block has support
                    if (hasSupport(level, adjacent, placedBlocks)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Try to place remaining ores in valid positions
     */
    private void fillRemainingOres(Level level, List<BlockPos> positions,
                                   Set<BlockPos> placedBlocks, int placedCount) {
        int needed = oreCount - placedCount;
        int placed = 0;

        // Try positions near already placed blocks
        List<BlockPos> candidates = new ArrayList<>();

        for (BlockPos placedPos : placedBlocks) {
            // Check adjacent positions
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = 0; dy <= 1; dy++) { // Can go up or same level
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;

                        BlockPos candidate = placedPos.offset(dx, dy, dz);

                        // Don't go too high
                        if (candidate.getY() - placedPos.getY() > 2) continue;

                        if (canPlaceBlock(level, candidate) &&
                            !placedBlocks.contains(candidate) &&
                            !candidates.contains(candidate)) {
                            candidates.add(candidate);
                        }
                    }
                }
            }
        }

        // Sort by Y (lowest first) for better stability
        candidates.sort(Comparator.comparingInt(BlockPos::getY));

        // Place remaining ores
        for (BlockPos candidate : candidates) {
            if (placed >= needed) break;

            // Simple support check for fill-in blocks
            BlockPos below = candidate.below();
            if (placedBlocks.contains(below) ||
                (!level.isEmptyBlock(below) && !level.getBlockState(below).isAir())) {
                level.setBlock(candidate, oreBlock, 2);
                placedBlocks.add(candidate);
                placed++;
            }
        }
    }

    /**
     * Alternative: Generate a more organic, lumpy pile
     */
    public void generateOrganicPile(Level level, BlockPos basePos) {
        BlockPos groundPos = findGroundPosition(level, basePos);
        if (groundPos == null) return;

        // Create main mound
        List<BlockPos> mainMound = generateMoundShape(groundPos, (int)(oreCount * 0.7));

        // Create some smaller satellite piles
        List<BlockPos> allPositions = new ArrayList<>(mainMound);

        int remaining = oreCount - mainMound.size();
        if (remaining > 3) {
            // Add 1-2 smaller piles nearby
            int smallPiles = Math.min(2, remaining / 4);
            for (int i = 0; i < smallPiles && remaining > 3; i++) {
                int smallPileSize = Math.min(remaining, random.nextInt(5) + 2);
                int offsetX = random.nextInt(4) - 2;
                int offsetZ = random.nextInt(4) - 2;

                BlockPos smallPileBase = groundPos.offset(offsetX, 0, offsetZ);
                BlockPos smallGround = findGroundPosition(level, smallPileBase);
                if (smallGround != null) {
                    List<BlockPos> smallPile = generateMoundShape(smallGround, smallPileSize);
                    allPositions.addAll(smallPile);
                    remaining -= smallPileSize;
                }
            }
        }

        // Place all blocks
        placeSupportedBlocks(level, allPositions);
    }

    /**
     * Generate a simple mound shape
     */
    private List<BlockPos> generateMoundShape(BlockPos center, int size) {
        List<BlockPos> positions = new ArrayList<>();

        // Simple algorithm for a natural mound
        double radius = Math.sqrt(size) * 0.8;

        for (int i = 0; i < size; i++) {
            // Random position in circle
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = random.nextDouble() * radius;

            // Height decreases with distance from center
            double heightFactor = 1.0 - (distance / radius);
            int height = (int)(heightFactor * (maxHeight - 1)) + 1;

            int dx = (int)(Math.cos(angle) * distance);
            int dz = (int)(Math.sin(angle) * distance);

            // Random slight height variation
            int dy = random.nextInt(height);

            BlockPos pos = center.offset(dx, dy, dz);
            positions.add(pos);
        }

        return positions;
    }
}