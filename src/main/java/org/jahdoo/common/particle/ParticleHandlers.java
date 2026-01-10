package org.jahdoo.common.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jahdoo.common.particle.particle_options.BakedParticleOptions;
import org.jahdoo.common.particle.particle_options.GenericParticleOptions;
import org.jahdoo.common.registers.mod.ElementReg;
import org.jahdoo.trial_nexus.element.AbstractElement;
import org.jahdoo.trial_nexus.utils.PositionFinders;

import java.util.List;

import static net.minecraft.util.RandomSource.create;
import static org.jahdoo.common.particle.ParticleStore.*;
import static org.jahdoo.common.registers.AttachmentReg.CASTER_DATA;
import static org.jahdoo.trial_nexus.utils.Helpers.Random;

public class ParticleHandlers {

    public static GenericParticleOptions genericParticle(int particleType, AbstractElement element, int lifetime, float size, double speed){
        return new GenericParticleOptions(particleType, element.partColourA(), element.partColourFade(), lifetime, size, false, speed);
    }

    public static GenericParticleOptions genericParticle(AbstractElement element, int lifetime, float size){
        return new GenericParticleOptions(MAGIC_PARTICLE, element.partColourA(), element.partColourFade(), lifetime,size,false,1);
    }

    public static GenericParticleOptions flamingParticle(int type, AbstractElement element, int lifetime, float size){
        return new GenericParticleOptions(type, element.textColourB(), element.textColourB(), lifetime,size,false, 1);
    }

    public static GenericParticleOptions genericParticle(int type, AbstractElement element, int lifetime, float size){
        return new GenericParticleOptions(type, element.textColourB(), element.textColourB(), lifetime,size,false, 1);
    }

    public static BakedParticleOptions bakedParticle(int type, int lifetime, float size, boolean setStaticSize){
        return new BakedParticleOptions(type, lifetime, size,setStaticSize);
    }

    public static GenericParticleOptions genericParticle(AbstractElement element, int lifetime, float size, boolean staticSize){
        return new GenericParticleOptions(MAGIC_PARTICLE, element.partColourA(), element.partColourFade(), lifetime,size,staticSize, 1);
    }

    public static GenericParticleOptions genericParticle(int lifetime, float size, int colourPrimary, int colourSecondary){
        return new GenericParticleOptions(MAGIC_PARTICLE, colourPrimary, colourSecondary, lifetime,size,false, 1);
    }

    public static GenericParticleOptions genericParticle(int particleType, AbstractElement element, int lifetime, float size, boolean staticSize){
        return new GenericParticleOptions(particleType, element.partColourA(), element.partColourFade(), lifetime,size,staticSize, 1);
    }

    public static GenericParticleOptions genericParticle(int particleType, int colourPrimary, int colourFade, int lifetime, float size, boolean staticSize, double speed){
        return new GenericParticleOptions(particleType, colourPrimary, colourFade, lifetime,size,staticSize, speed);
    }

    public static GenericParticleOptions genericParticle(int particleType, int lifetime, float size, int colourPrimary, int colourSecondary){
        return new GenericParticleOptions(particleType, colourPrimary, colourSecondary, lifetime, size, false, 1);
    }

    public static GenericParticleOptions genericParticle(int particleType, int lifetime, float size, int colourPrimary, int colourSecondary, boolean setStaticSize){
        return new GenericParticleOptions(particleType, colourPrimary, colourSecondary, lifetime,size,setStaticSize, 1);
    }

    public static GenericParticleOptions genericParticle(int particleType, AbstractElement element, int lifetime, float size, boolean staticSize, double speed){
        return new GenericParticleOptions(particleType, element.partColourA(), element.partColourFade(), lifetime, size, staticSize, speed);
    }

    public static void particleBurst(Level world, Vec3 pos, int particleCount, ParticleOptions particleOptions, double x, double y, double z, float speed) {
        for (int i = 0; i < 10; i++) {
            sendParticles(world, particleOptions, pos, particleCount, x, y, z, speed);
        }
    }

    public static void particleBurst(Level world, Vec3 pos, int particleCount, ParticleOptions particleOptions, double x, double y, double z, float speed, int totalPoofs) {
        for (int i = 0; i < totalPoofs; i++) {
            sendParticles(world, particleOptions, pos, particleCount, x, y, z, speed);
        }
    }

    public static void invisibleLight(Level world, Vec3 loc, ParticleOptions particleOptions, double bound1, double bound2, int speed) {
        for (int i = 0; i < 3; i++) {
            sendParticles(world, particleOptions, loc, 0, 0, Random.nextDouble(bound1, bound2), 0, speed);
        }
    }

    public static ParticleOptions getAllParticleTypes(AbstractElement element, int lifetime, float size){
        var baked = bakedParticle(element.id(), lifetime, size, false);
        var generic = genericParticle(GENERIC_PARTICLE, element, lifetime, size);
        var magic = genericParticle(MAGIC_PARTICLE, element, lifetime, size);
        var soft = genericParticle(SOFT_PARTICLE, element, lifetime, size);
        var collectTypes = List.of(baked, generic, magic, soft);
        return collectTypes.get(Random.nextInt(collectTypes.size()));
    }

    public static ParticleOptions getAllParticleTypesAlt(AbstractElement element, int lifetime, float size){
        var baked = bakedParticle(element.id(), lifetime, size, false);
        var generic = flamingParticle(GENERIC_PARTICLE, element, lifetime, size);
        var magic = flamingParticle(MAGIC_PARTICLE, element, lifetime, size);
        var soft = flamingParticle(SOFT_PARTICLE, element, lifetime, size);
        var collectTypes = List.of(baked, generic, magic, soft);
        return collectTypes.get(Random.nextInt(collectTypes.size()));
    }

    public static ParticleOptions getNonBakedParticles(int colour1, int colour2, int lifetime, float size){
        var generic = genericParticle(GENERIC_PARTICLE, colour1, colour2, lifetime, size, false, 1);
        var magic = genericParticle(MAGIC_PARTICLE, colour1, colour2, lifetime, size, false, 1);
        var soft = genericParticle(SOFT_PARTICLE, colour1, colour2, lifetime, size, false, 1);
        var collectTypes = List.of(generic, magic, soft);
        return collectTypes.get(Random.nextInt(collectTypes.size()));
    }

    public static void particleBurst(Level world, Vec3 pos, int particleCount, ParticleOptions particleOptions) {
        for (int i = 0; i < 5; i++) {
            sendParticles(world, particleOptions, pos, particleCount,
                (world.random.nextFloat() * 1 - 0.5) / 3,
                (world.random.nextFloat() * 1 - 0.5) / 3,
                (world.random.nextFloat() * 1 - 0.5) / 3, 0.1f
            );
        }
    }

    public static void particleBurst(Level world, Vec3 pos, int particleCount, ParticleOptions particleOptions, float speed) {
        for (int i = 0; i < 5; i++) {
            sendParticles(world, particleOptions, pos, particleCount,
                (world.random.nextFloat() * 1 - 0.5) / 3,
                (world.random.nextFloat() * 1 - 0.5) / 3,
                (world.random.nextFloat() * 1 - 0.5) / 3, speed
            );
        }
    }

    public static void genericProjPart(Level world, Vec3 pos, int particleCount, ParticleOptions particleOptions, float speed) {
        for (int i = 0; i < 2; i++) {
            var spread = 0.2;
            sendParticles(world, particleOptions, pos, particleCount,
                (world.random.nextFloat() * 1 - spread) / 3,
                (world.random.nextFloat() * 1 - spread) / 3,
                (world.random.nextFloat() * 1 - spread) / 3,
                speed
            );
        }
    }

    public static <T extends ParticleOptions> int sendParticles(Level level, T type, Vec3 positions, int pParticleCount, double xOff, double yOff, double zOff, double speed) {
        if((level instanceof ServerLevel serverLevel)){
            var clientboundlevelparticlespacket = new ClientboundLevelParticlesPacket(type, false, positions.x, positions.y, positions.z, (float) xOff, (float) yOff, (float) zOff, (float) speed, pParticleCount);
            var i = 0;
            for (int j = 0; j < serverLevel.players().size(); ++j) {
                var serverplayer = serverLevel.players().get(j);
                if (sendParticles(serverplayer, positions.x, positions.y, positions.z, clientboundlevelparticlespacket)) ++i;
            }
            return i;
        } else {
            level.addParticle(type, positions.x, positions.y, positions.z, xOff/100 * speed, yOff/100 * speed, zOff/100 * speed);
            return 0;
        }
    }

    private static boolean sendParticles(ServerPlayer player, double posX, double posY, double posZ, Packet<?> pPacket) {
        if (player.level().isClientSide) {
            return false;
        } else {
            var blockpos = player.blockPosition();
            if (blockpos.closerToCenterThan(new Vec3(posX, posY, posZ), 64.0D)) {
                player.connection.send(pPacket);
                return true;
            } else {
                return false;
            }
        }
    }

    public static void spawnElectrifiedParticles(Level level, Vec3 position, ParticleOptions particleType, int count, LivingEntity livingEntity, double speed) {

        for (int i = 0; i < count; i++) {

            var offsetX = (Random.nextDouble() - 0.5) * livingEntity.getBbWidth();
            var offsetY = Random.nextDouble() * livingEntity.getBbHeight();
            var offsetZ = (Random.nextDouble() - 0.5) * livingEntity.getBbWidth();

            // Give the particles an electrified jittery motion
            var speedX = (Random.nextDouble() - 0.5) * 0.1;
            var speedY = (Random.nextDouble() - 0.5) * 0.1;
            var speedZ = (Random.nextDouble() - 0.5) * 0.1;

            sendParticles(level, particleType, position.add(offsetX, offsetY, offsetZ), 1,speedX, speedY, speedZ,speed);
        }
    }

    public static void spawnElectrifiedParticles(ServerLevel level, Vec3 position, ParticleOptions particleType, int count, LivingEntity livingEntity, double speed, double ySpeed) {

        for (int i = 0; i < count; i++) {

            var offsetX = (Random.nextDouble() - 0.5) * livingEntity.getBbWidth();
            var offsetY = Random.nextDouble() * livingEntity.getBbHeight();
            var offsetZ = (Random.nextDouble() - 0.5) * livingEntity.getBbWidth();

            // Give the particles an electrified jittery motion
            var speedX = (Random.nextDouble() - 0.5) * 0.1;
            var speedY = (Random.nextDouble() - 0.5) * 0.1;
            var speedZ = (Random.nextDouble() - 0.5) * 0.1;

            sendParticles(level, particleType, position.add(offsetX, offsetY, offsetZ), 1,speedX, speedY + ySpeed, speedZ,speed);
        }
    }

    public static void playParticles(ParticleOptions particleOptions, Projectile projectile, double getX, double getY, double getZ) {
        var deltaX = getX - projectile.xOld;
        var deltaY = getY - projectile.yOld;
        var deltaZ = getZ - projectile.zOld;
        var dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 5);
        for (var j = 0; j < Math.max(dist, 5); j++) {
            var coeff = j / dist;
            var position = new Vec3((float) (projectile.xo + deltaX * coeff), (float) (projectile.yo + deltaY * coeff) + 0.1, (float) (projectile.zo + deltaZ * coeff));
            sendParticles(
                projectile.level(), particleOptions, position, 1,
                0.0125f * (Random.nextFloat() - 0.5f),
                0.0125f * (Random.nextFloat() - 0.5f),
                0.0125f * (Random.nextFloat() - 0.5f),
                0
            );
        }
    }

    public static void playParticles2(ParticleOptions particleOptions, Projectile projectile, double getX, double getY, double getZ, double speed) {
        var deltaX = getX - projectile.xOld;
        var deltaY = getY - projectile.yOld;
        var deltaZ = getZ - projectile.zOld;
        var dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 5);
        for (var j = 0; j < dist; j++) {
            var coeff = j / dist;
            var position = new Vec3((float) (projectile.xo + deltaX * coeff), (float) (projectile.yo + deltaY * coeff) + 0.1, (float) (projectile.zo + deltaZ * coeff));
            sendParticles(
                projectile.level(), particleOptions, position, 2,
                0.0125f * (Random.nextFloat() - 0.5f),
                0.0125f * (Random.nextFloat() - 0.5f),
                0.0125f * (Random.nextFloat() - 0.5f),
                speed
            );
        }
    }

    public static void  playParticles3(
        ParticleOptions particleOptions,
        Projectile projectile,
        int multiplier,
        double speed
    ) {
        var deltaX = projectile.getX() - projectile.xOld;
        var deltaY = projectile.getY() - projectile.yOld;
        var deltaZ = projectile.getZ() - projectile.zOld;
        var dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * multiplier);
        for (var j = 0; j < dist; j++) {
            var coeff = j / dist;
            if (projectile.level() instanceof ServerLevel serverLevel){
                Vec3 position = new Vec3((float) (projectile.xo + deltaX * coeff), (float) (projectile.yo + deltaY * coeff) + 0.1, (float) (projectile.zo + deltaZ * coeff));
                sendParticles(
                    serverLevel, particleOptions, position, 1,
                    0.0125f * (Random.nextFloat() - 0.5f),
                    0.0125f * (Random.nextFloat() - 0.5f),
                    0.0125f * (Random.nextFloat() - 0.5f),
                    speed
                );
            }
        }
    }

    public static void GenericProjectile(
        Projectile projectile,
        ParticleOptions particleMain,
        ParticleOptions particleTrail,
        double speed
    ){
        if(projectile.tickCount > 1){
            var directionX = projectile.getX() - projectile.xOld;
            var directionY = projectile.getY() - projectile.yOld;
            var directionZ = projectile.getZ() - projectile.zOld;
            var magnitude = Math.sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ);
            var normalizedX = directionX / magnitude;
            var normalizedY = directionY / magnitude;
            var normalizedZ = directionZ / magnitude;
            var offsetDistance = 0.8;
            var offsetX = projectile.getX() - normalizedX * offsetDistance;
            var offsetY = projectile.getY() - normalizedY * offsetDistance;
            var offsetZ = projectile.getZ() - normalizedZ * offsetDistance;

            playParticles(particleMain, projectile, offsetX, offsetY, offsetZ);
            playParticles2(particleTrail, projectile, offsetX, offsetY, offsetZ, speed);
        }
    }

    public static void entityProjectileParticles(
        Projectile projectile,
        int tickCount,
        float spread,
        AbstractElement element
    ){
        if (tickCount > 2) {
            var velocity = projectile.getDeltaMovement();
            var offsetX = velocity.x * -2.2;
            var offsetY = velocity.y * -2.2;
            var offsetZ = velocity.z * -2.2;
            var particleX = projectile.getX() + offsetX;
            var particleY = projectile.getY() + projectile.getBbHeight() / 2 + offsetY;
            var particleZ = projectile.getZ() + offsetZ;
            var heightOffset = 0.02;

            for (int i = 0; i < 4; i++){
                var position = new Vec3(
                    particleX + Random.nextFloat(-spread, spread),
                    particleY + Random.nextFloat(-spread, spread),
                    particleZ + Random.nextFloat(-spread, spread)
                );
                var genericSlow = genericParticle(SOFT_PARTICLE, element, 3, 1.2f, false);
                var bakedSlow = bakedParticle(element.id(), 2,2.5f,false);
                var subtract = position.subtract(0, heightOffset, 0);
                var level = projectile.level();
                sendParticles(level, bakedSlow, subtract, 0, 0, 0, 0,0);
                sendParticles(level, genericSlow, subtract, 0, 0, 0, 0,0);
            }
        }
    }

    public static void burningSkull(
        Projectile projectile,
        int tickCount,
        float spread,
        AbstractElement element
    ){
        if (tickCount > 2) {
            var velocity = projectile.getDeltaMovement();
            var offsetX = velocity.x * -2.2;
            var offsetY = velocity.y * -2.2;
            var offsetZ = velocity.z * -2.2;
            var particleX = projectile.getX() + offsetX;
            var particleY = projectile.getY() + projectile.getBbHeight() / 2 + offsetY;
            var particleZ = projectile.getZ() + offsetZ;
            var heightOffset = 0.04;

            for (int i = 0; i < 3; i++){
                var position = new Vec3(
                    particleX + Random.nextFloat(-spread, spread),
                    particleY + Random.nextFloat(-spread, spread),
                    particleZ + Random.nextFloat(-spread, spread)
                );
                var genericSlow = flamingParticle(GENERIC_PARTICLE, element, 3, 1.8f);
                var bakedSlow = flamingParticle(MAGIC_PARTICLE, element, 3, 0.8f);
                var subtract = position.subtract(0, heightOffset, 0);
                var level = projectile.level();
                sendParticles(level, bakedSlow, subtract, 0, 0, 0, 0,0);
                sendParticles(level, genericSlow, subtract, 0, 0, 0, 0,0);
            }
        }
    }

    public void pullParticlesToCenter(Player player){
        var casterData = player.getData(CASTER_DATA);
        var manaReduction = casterData.getMaxMana(player) / 60;
        var bakedParticleOptions = new BakedParticleOptions(
            ElementReg.vitality().id(),
            6, 2f, false
        );
        var genericParticleOptions = genericParticle(GENERIC_PARTICLE, ElementReg.vitality(), 6, 2f);

        var particleOptionsList = List.of(
            bakedParticleOptions,
            genericParticleOptions
        );

        if(casterData.getManaPool() >= manaReduction){
            var height = player.getBbHeight();
            var ticksUsing = player.getTicksUsingItem();
            PositionFinders.innerRadiusRandom(
                player.position()
                    .add(0, height / 2, 0)
                    .offsetRandom(create(), 1.5f), 3, (double) ticksUsing /10,
                positions -> {
                    if (player.level() instanceof ServerLevel serverLevel) {
                        var directions = player.position().subtract(positions).normalize().add(0, height / 2, 0);
                        sendParticles(
                            serverLevel,
                            particleOptionsList.get(create().nextInt(0, 2)),
                            positions,
                            0,
                            directions.x,
                            Random.nextDouble(-0.3, 0.3),
                            directions.z,
                            (double) ticksUsing /500
                        );
                    }
                }
            );
        }
    }
}
