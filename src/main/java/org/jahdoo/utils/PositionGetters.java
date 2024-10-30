package org.jahdoo.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.jahdoo.utils.GeneralHelpers.Random;

public class PositionGetters {

    public static void getOuterRingOfRadius(Vec3 positions, double radius, double points, Consumer<Vec3> method) {
        double stepSize = 2 * Math.PI / points;

        // Iterate over angles from 0 to 2π with evenly spaced points
        for (int i = 0; i < points; i++) {
            double theta = i * stepSize;
            double posX = positions.x + radius * Math.cos(theta);
            double posZ = positions.z + radius * Math.sin(theta);
            Vec3 pos = new Vec3(posX, positions.y, posZ);
            method.accept(pos);
        }
    }

    public static void getOuterSquareOfRadius(Vec3 positions, double radius, double points, Consumer<Vec3> method) {
        // Determine how many points per side of the square
        double pointsPerSide = points / 4.0;
        double stepSize = (2 * radius) / pointsPerSide;

        // Left edge (moving up)
        for (int i = 0; i < pointsPerSide; i++) {
            double posZ = positions.z - radius + i * stepSize;
            Vec3 pos = new Vec3(positions.x - radius, positions.y, posZ);
            method.accept(pos);
        }

        // Top edge (moving right)
        for (int i = 0; i < pointsPerSide; i++) {
            double posX = positions.x - radius + i * stepSize;
            Vec3 pos = new Vec3(posX, positions.y, positions.z + radius);
            method.accept(pos);
        }

        // Right edge (moving down)
        for (int i = 0; i < pointsPerSide; i++) {
            double posZ = positions.z + radius - i * stepSize;
            Vec3 pos = new Vec3(positions.x + radius, positions.y, posZ);
            method.accept(pos);
        }

        // Bottom edge (moving left)
        for (int i = 0; i < pointsPerSide; i++) {
            double posX = positions.x + radius - i * stepSize;
            Vec3 pos = new Vec3(posX, positions.y, positions.z - radius);
            method.accept(pos);
        }
    }

    public static void getInnerRingOfRadiusRandom(Vec3 position, double radius, double numPoints, Consumer<Vec3> method) {
        double sectorAngle = 2 * Math.PI / numPoints;

        for (int i = 0; i < numPoints; i++) {
            double angle = i * sectorAngle + Math.random() * sectorAngle;
            double distance = Math.sqrt(Math.random()) * radius;
            double posX = position.x + distance * Math.cos(angle);
            double posZ = position.z + distance * Math.sin(angle);
            Vec3 pos = new Vec3(posX, position.y, posZ);
            method.accept(pos);
        }
    }

    public static List<Vec3> getInnerRingOfRadius(Entity player, double radius) {
        List<Vec3> positions = new ArrayList<>(Mth.floor(radius));
        Vec3 playerPos = player.position();
        double playerX = playerPos.x;
        double playerZ = playerPos.z;

        for (double x = -radius; x <= radius; x++) {
            for (double z = -radius; z <= radius; z++) {
                double distance = Math.sqrt(x * x + z * z);
                if (distance <= radius) {
                    double posX = playerX + x;
                    double posZ = playerZ + z;
                    positions.add(new Vec3(posX, playerPos.y, posZ));
                }
            }
        }
        return positions;
    }


    public static void getInnerRingOfRadiusRandom(BlockPos blockPos, double radius, int numPoints, Consumer<Vec3> method) {
        Vec3 playerPos = blockPos.getCenter();
        double playerX = playerPos.x;
        double playerZ = playerPos.z;
        double sectorAngle = 2 * Math.PI / numPoints;

        // Generate points within each sector
        for (int i = 0; i < numPoints; i++) {
            double angle = i * sectorAngle + Math.random() * sectorAngle;
            double distance = Math.sqrt(Math.random()) * radius;
            double posX = playerX + distance * Math.cos(angle);
            double posZ = playerZ + distance * Math.sin(angle);
            Vec3 pos = new Vec3(posX, playerPos.y, posZ);
            method.accept(pos);
        }
    }

    public static List<Vec3> getOuterRingOfRadiusList(Vec3 positions, double radius, double points) {
        double stepSize = 2 * Math.PI / points;
        List<Vec3> positions1 = new ArrayList<>();

        // Iterate over angles from 0 to 2π with evenly spaced points
        for (int i = 0; i < points; i++) {
            double theta = i * stepSize;
            double posX = positions.x + radius * Math.cos(theta);
            double posZ = positions.z + radius * Math.sin(theta);
            Vec3 pos = new Vec3(posX, positions.y, posZ);
            positions1.add(pos);
        }

        return positions1;
    }

    public static List<Vec3> getInnerRingOfRadiusRandom(Vec3 position, double radius, double numPoints) {
        double sectorAngle = 2 * Math.PI / numPoints;
        List<Vec3> list = new ArrayList<>();
        for (int i = 0; i < numPoints; i++) {
            double angle = i * sectorAngle + Math.random() * sectorAngle;
            double distance = Math.sqrt(Math.random()) * radius;
            double posX = position.x + distance * Math.cos(angle);
            double posZ = position.z + distance * Math.sin(angle);
            Vec3 pos = new Vec3(posX, position.y, posZ);
            list.add(pos);
        }
        return list;
    }

    public static void getOuterRingOfRadiusRandom(Vec3 position, double radius, double points, Consumer<Vec3> method) {
        // Determine the number of points based on the density provided by 'points'
        int numberOfPoints = (int)(points * radius); // For example, points could be points per unit length of circumference

        // Generate random angles and positions for the determined number of points
        for (int i = 0; i < numberOfPoints; i++) {
            double theta = Random.nextDouble() * 2 * Math.PI; // Random angle between 0 and 2π
            double posX = position.x + radius * Math.cos(theta);
            double posZ = position.z + radius * Math.sin(theta);
            Vec3 pos = new Vec3(posX, position.y, posZ);
            method.accept(pos);
        }
    }

    public static List<Vec3> getSemicircle(Vec3 position, double radius, double points, float yaw, int range) {
        // Determine the number of points based on the density provided by 'points'
        int numberOfPoints = (int)(points * radius); // Adjust as needed
        List<Vec3> positions = new ArrayList<>();
        // Get the player's position
        double playerX = position.x;
        double playerY = position.y;
        double playerZ = position.z;

        // Get the player's yaw rotation (horizontal rotation)

        // Calculate the center of the quarter circle with offset
        float offsetAngleDegrees = 90;
        double centerX = playerX + radius * Math.cos(Math.toRadians(yaw + offsetAngleDegrees)); // Adjust to center correctly
        double centerZ = playerZ + radius * Math.sin(Math.toRadians(yaw + offsetAngleDegrees)); // Adjust to center correctly

        // Calculate the start and end angles for the quarter circle based on player's yaw
        double startAngle = Math.toRadians(yaw + offsetAngleDegrees - range); // Start 45 degrees to the left of adjusted direction
        double endAngle = Math.toRadians(yaw + offsetAngleDegrees + range);   // End 45 degrees to the right of adjusted direction

        // Generate random angles and positions for the determined number of points in a quarter circle
        for (int i = 0; i < numberOfPoints; i++) {
            double theta = startAngle + Random.nextDouble() * (endAngle - startAngle); // Random angle within the specified range
            double posX = centerX + radius * Math.cos(theta);
            double posZ = centerZ + radius * Math.sin(theta);
            positions.add(new Vec3(posX, playerY, posZ));
        }
        return positions;
    }

    public static void getSphericalPositions(Entity entity, double radius, double numPoints, Consumer<Vec3> method) {
        Vec3 entityPos = entity.position();

        // Calculate the angle between each point
        double phiIncrement = Math.PI * (3.0 - Math.sqrt(5.0)); // Golden angle

        // Generate points on the sphere using spherical coordinates
        for (int i = 0; i < numPoints; i++) {
            double y = 1 - (i / (numPoints - 1)) * 2; // Range from -1 to 1
            double radiusAtHeight = Math.sqrt(1 - y * y) * radius;

            double phi = i * phiIncrement; // Angle around the y-axis

            double x = Math.cos(phi) * radiusAtHeight;
            double z = Math.sin(phi) * radiusAtHeight;

            Vec3 pos = new Vec3(entityPos.x + x, entityPos.y + y * radius, entityPos.z + z);
            method.accept(pos);
        }
    }

    public static void getSphericalBlockPositions(Entity entity, double radius, Consumer<BlockPos> method) {
        Vec3 entityPos = entity.position();
        int radiusCeil = (int) Math.ceil(radius);

        for (int x = -radiusCeil; x <= radiusCeil; x++) {
            for (int y = -radiusCeil; y <= radiusCeil; y++) {
                for (int z = -radiusCeil; z <= radiusCeil; z++) {
                    BlockPos currentPos = entity.blockPosition().offset(x, y, z);
                    double distanceSquared = entityPos.distanceToSqr(Vec3.atCenterOf(currentPos));

                    if (distanceSquared <= radius * radius) {
                        if (Random.nextDouble() < 0.90) { // 70% chance to break the block
                            method.accept(currentPos);
                        }
                    }
                }
            }
        }
    }

    public static void getRandomSphericalPositions(Entity entity, double radius, double numPoints, Consumer<Vec3> method) {
        Vec3 entityPos = entity.position();

        for (int i = 0; i < numPoints; i++) {
            // Generate random spherical coordinates
            double theta = 2 * Math.PI * Random.nextDouble(); // Random angle in the xy-plane
            double phi = Math.acos(2 * Random.nextDouble() - 1); // Random angle from z-axis

            // Convert spherical coordinates to Cartesian coordinates
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.sin(phi) * Math.sin(theta);
            double z = radius * Math.cos(phi);

            Vec3 pos = new Vec3(entityPos.x + x, entityPos.y + y, entityPos.z + z);
            method.accept(pos);
        }
    }

    public static void getRandomSphericalPositions(Vec3 positions, double radius, double numPoints, Consumer<Vec3> method) {
        for (int i = 0; i < numPoints; i++) {
            // Generate random spherical coordinates
            double theta = 2 * Math.PI * Random.nextDouble(); // Random angle in the xy-plane
            double phi = Math.acos(2 * Random.nextDouble() - 1); // Random angle from z-axis

            // Convert spherical coordinates to Cartesian coordinates
            double x = radius * Math.sin(phi) * Math.cos(theta);
            double y = radius * Math.sin(phi) * Math.sin(theta);
            double z = radius * Math.cos(phi);

            Vec3 pos = new Vec3(positions.x + x, positions.y + y, positions.z + z);
            method.accept(pos);
        }
    }

}
