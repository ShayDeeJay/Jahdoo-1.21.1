package org.jahdoo.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.JahdooMod;
import org.jahdoo.all_magic.AbstractElement;
import org.jahdoo.client.SharedUI;
import org.jahdoo.components.AbilityHolder;
import org.jahdoo.components.WandAbilityHolder;
import org.jahdoo.registers.*;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class GeneralHelpers {
    public static final Random Random = ThreadLocalRandom.current();
    public static GeneralHelpers generalHelpers = new GeneralHelpers();

    public static ResourceLocation modResourceLocation(String location) {
        return ResourceLocation.fromNamespaceAndPath(JahdooMod.MOD_ID, location);
    }

    public static <T> T getRandomListElement(List<T> collection){
        Collections.shuffle(collection);
        return collection.getFirst();
    }

    public static Map<String, AbilityHolder.AbilityModifiers> getModifierValue(WandAbilityHolder wandAbilityHolder, String tagName) {
        if(wandAbilityHolder != null){
            var allModifiers = wandAbilityHolder.abilityProperties();
            if (allModifiers != null) {
                if(allModifiers.get(tagName) != null){
                    return allModifiers.get(tagName).abilityProperties();
                }
            }
        }
        return Collections.emptyMap();
    }

    public static String roundNonWhole(double number) {
        // Get the decimal part by subtracting the integer part
        double decimalPart = number - (int) number;

        // If the decimal part is 0, round it and return as string
        if (decimalPart == 0) {
            return String.valueOf(Math.round(number));
        }

        // If not, return the original number as string
        return String.valueOf(number);
    }

    public static float getFormattedFloat(float value){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return  Float.parseFloat(decimalFormat.format(value));
    }

    public static ListTag nbtDoubleList(double... pNumbers) {
        ListTag listtag = new ListTag();
        for(double d0 : pNumbers) listtag.add(DoubleTag.valueOf(d0));
        return listtag;
    }

    public static Component withStyleComponent(String text, int colour){
        return Component.literal(text).withStyle(style -> style.withColor(colour));
    }

    public static Component withStyleComponentTrans(String text, int colour, Object... args){
        return Component.translatable(text,args).withStyle(style -> style.withColor(colour));
    }

    public static void debugComponent(ItemStack itemStack, Player player){
        player.sendSystemMessage(Component.literal("New Request"));
        player.sendSystemMessage(Component.literal("-----------------------------------------------------"));
        for (TypedDataComponent<?> component : itemStack.getComponents()) {
            player.sendSystemMessage(withStyleComponent(component.toString(), new Color((int)(Math.random() * 0x1000000)).getRGB()));
            player.sendSystemMessage(Component.literal(" "));
        }
        player.sendSystemMessage(Component.literal("-----------------------------------------------------"));
        player.sendSystemMessage(Component.literal(" "));
    }

    public static void playDebugMessage(Player player, Object... info){
        var randomColour = new Color((int)(Math.random() * 0x1000000)).getRGB();
        player.sendSystemMessage(Component.literal(Arrays.toString(info)).withStyle(style -> style.withColor(randomColour)));
    }

    public static float attributeModifierCalculator(
        LivingEntity player,
        float initialValue,
        AbstractElement getElementType,
        Holder<Attribute> attribute,
        boolean isAddition
    ){
        var wandItem = player.getMainHandItem();
        var abilityName = wandItem.get(DataComponentRegistry.WAND_DATA.get());
        if(abilityName == null) return 0;
        var getDamageAttribute = player.getAttributes().getValue(attribute);
        var getAbility = AbilityRegister.getFirstSpellByTypeId(abilityName.selectedAbility());

        if(getAbility.isPresent()){
            var element = SharedUI.getElementWithType(getAbility.get(), wandItem);
            float reCalculatedDamage = initialValue;

            if (Objects.equals(getElementType, element)) {
                float getPercentageDamage = (float) (initialValue / 100 * getDamageAttribute);

                if(isAddition){
                    reCalculatedDamage = reCalculatedDamage + getPercentageDamage;
                } else {
                    reCalculatedDamage = reCalculatedDamage - getPercentageDamage;
                }

            }
            return reCalculatedDamage;
        }

        return initialValue;
    }

    public static void damageEntityWithModifiers(LivingEntity target, LivingEntity player, float currentDamage, AbstractElement getElementType){
        target.hurt(player.damageSources().playerAttack((Player) player), attributeModifierCalculator(player, currentDamage, getElementType, AttributesRegister.MAGIC_DAMAGE_MULTIPLIER, true));
    }

    public static void getSoundWithPosition(Level level, BlockPos position, SoundEvent audio){
        level.playSound(null, position.getX(), position.getY(), position.getZ(), audio, SoundSource.BLOCKS,1,1) ;
    }

    public static void getSoundWithPosition(Level level, BlockPos position, SoundEvent audio, float volume){
        level.playSound(null, position.getX(), position.getY(), position.getZ(), audio, SoundSource.BLOCKS,volume,1) ;
    }

    public static void  getSoundWithPosition(Level level, BlockPos position, SoundEvent audio, float volume, float pitch){
        level.playSound(null, position.getX(), position.getY(), position.getZ(), audio, SoundSource.BLOCKS,volume, pitch) ;
    }

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

    public static Vec3 getRandomParticleVelocity(Entity entity, double speed) {
        double theta = Random.nextDouble() * 2 * Math.PI; // Angle around the y-axis
        double phi = Random.nextDouble() * Math.PI; // Angle from the y-axis

        // Convert spherical coordinates to Cartesian coordinates
        double x = Math.sin(phi) * Math.cos(theta);
        double y = Math.cos(phi);
        double z = Math.sin(phi) * Math.sin(theta);

        // Scale the velocity vector by the desired speed
        return new Vec3(x, y, z).normalize().scale(speed);
    }

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

    public static String stringIdToName(String input) {
        String[] words = input.split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            word = word.trim();
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
            }
        }
        return result.toString().trim();
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

    public static void entityMover(Entity receiver, Entity target, double velocity) {
        double resistance = 0.9;
        double directionX = receiver.getX() - target.getX();
        double directionY = receiver.getY() - target.getY();
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

    public <T extends ParticleOptions> int sendParticles(ServerLevel serverLevel, T pType, Vec3 positions, int pParticleCount, double pXOffset, double pYOffset, double pZOffset, double pSpeed) {
        if(serverLevel != null){

            ClientboundLevelParticlesPacket clientboundlevelparticlespacket = new ClientboundLevelParticlesPacket(pType, false, positions.x, positions.y, positions.z, (float) pXOffset, (float) pYOffset, (float) pZOffset, (float) pSpeed, pParticleCount);
            int i = 0;

            for (int j = 0; j < serverLevel.players().size(); ++j) {
                ServerPlayer serverplayer = serverLevel.players().get(j);

                if (this.sendParticles(serverplayer, false, positions.x, positions.y, positions.z, clientboundlevelparticlespacket)) {
                    ++i;
                }
            }

            return i;
        }
        return 0;
    }

    private boolean sendParticles(ServerPlayer pPlayer, boolean pLongDistance, double pPosX, double pPosY, double pPosZ, Packet<?> pPacket) {
        if (pPlayer.level().isClientSide) {
            return false;
        } else {
            BlockPos blockpos = pPlayer.blockPosition();
            if (blockpos.closerToCenterThan(new Vec3(pPosX, pPosY, pPosZ), pLongDistance ? 128.0D : 64.0D)) {
                pPlayer.connection.send(pPacket);
                return true;
            } else {
                return false;
            }
        }
    }


}
