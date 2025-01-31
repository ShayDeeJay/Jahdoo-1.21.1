package org.jahdoo.entities;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class EntityMovers {

    public static void moveEntitiesRelativeToPlayer(Entity entity, double radius, Consumer<Vec3> method) {
        for (int i = 0; i < radius; i++) {
            double angle = 2 * Math.PI * i / radius;
            Direction direction = entity.getDirection();

            double x = direction.getStepX() + radius * Math.cos(angle);
            double y = direction.getStepY();
            double z = direction.getStepZ() + radius * Math.sin(angle);

            method.accept(new Vec3(x, y, z));
        }
    }

    public static void entityMover(Entity receiver, Entity target, double velocity, double smoothingFactor) {
        double resistance = 0.9;

        // Calculate direction vector
        double directionX = receiver.getX() - target.getX();
        double directionY = receiver.getY() - target.getY();
        double directionZ = receiver.getZ() - target.getZ();

        // Normalize direction vector
        double directionLength = Math.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
        if (directionLength == 0) return; // Avoid division by zero
        directionX /= directionLength;
        directionY /= directionLength;
        directionZ /= directionLength;

        // Apply velocity to direction
        directionX *= velocity;
        directionY *= velocity;
        directionZ *= velocity;

        // Get current speed
        double currentSpeedX = target.getDeltaMovement().x;
        double currentSpeedY = target.getDeltaMovement().y;
        double currentSpeedZ = target.getDeltaMovement().z;

        // Apply smoothing factor to blend towards target direction
        double newSpeedX = (1 - smoothingFactor) * (directionX * (1 - resistance) + currentSpeedX * resistance) + smoothingFactor * currentSpeedX;
        double newSpeedY = (1 - smoothingFactor) * (directionY * (1 - resistance) + currentSpeedY * resistance) + smoothingFactor * currentSpeedY;
        double newSpeedZ = (1 - smoothingFactor) * (directionZ * (1 - resistance) + currentSpeedZ * resistance) + smoothingFactor * currentSpeedZ;

        // Update target's movement
        target.setDeltaMovement(newSpeedX, newSpeedY, newSpeedZ);
    }

    public static void entityMover(Entity receiver, Entity target, double velocity) {
        double resistance = 0.86 ;
        double directionX = receiver.getX() - target.getX();
        double directionY = receiver.getY() - target.getY() + target.getBbHeight();
        double directionZ = receiver.getZ() - target.getZ();

        double directionLength = Math.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
        directionX /= directionLength;
        directionY /= directionLength;
        directionZ /= directionLength;

        directionX *= velocity;
        directionY *= velocity;
        directionZ *= velocity;

        double currentSpeedX = target.getDeltaMovement().x;
        double currentSpeedY = target.getDeltaMovement().y;
        double currentSpeedZ = target.getDeltaMovement().z;

        double newSpeedX = directionX * (1 - resistance) + currentSpeedX * resistance;
        double newSpeedY = directionY * (1 - resistance) + currentSpeedY * resistance;
        double newSpeedZ = directionZ * (1 - resistance) + currentSpeedZ * resistance;

        target.setDeltaMovement(newSpeedX, newSpeedY, newSpeedZ);
    }

    public static void entityMoverNoVertical(Entity receiver, Entity target, double velocity) {
        double resistance = 0.9;
        double directionX = receiver.getX() - target.getX();
        double directionY = receiver.getY() - target.getY();
        double directionZ = receiver.getZ() - target.getZ();

        double directionLength = Math.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
        directionX /= directionLength;
        directionZ /= directionLength;

        directionX *= velocity;
        directionZ *= velocity;

        double currentSpeedX = target.getDeltaMovement().x;
        double currentSpeedZ = target.getDeltaMovement().z;

        double newSpeedX = directionX * (1 - resistance) + currentSpeedX * resistance;
        double newSpeedZ = directionZ * (1 - resistance) + currentSpeedZ * resistance;

        target.setDeltaMovement(newSpeedX, target.getDeltaMovement().y, newSpeedZ);
    }

    public static void entityMover(Entity receiver, Vec3 startPoint, double velocity, double resistance, double heightAdjustment) {
        double directionX = receiver.getX() - startPoint.x;
        double directionY = receiver.getY() + heightAdjustment - startPoint.y;
        double directionZ = receiver.getZ() - startPoint.z;

        double directionLength = Math.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
        directionX /= directionLength;
        directionY /= directionLength;
        directionZ /= directionLength;

        directionX *= velocity;
        directionY *= velocity;
        directionZ *= velocity;

        double currentSpeedX = receiver.getDeltaMovement().x;
        double currentSpeedY = receiver.getDeltaMovement().y;
        double currentSpeedZ = receiver.getDeltaMovement().z;

        double newSpeedX = directionX * (1 - resistance) + currentSpeedX * resistance;
        double newSpeedY = directionY * (1 - resistance) + currentSpeedY * resistance;
        double newSpeedZ = directionZ * (1 - resistance) + currentSpeedZ * resistance;

        receiver.setDeltaMovement(newSpeedX, newSpeedY, newSpeedZ);
    }
}
