package org.jahdoo.trial_nexus.mobs.mob_setup;

import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.AcropolisMonsters.Clawdian_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.Draugar.Aptrgangr_Entity;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.Internal_Animation_Monster;
import com.github.L_Ender.cataclysm.entity.InternalAnimationMonster.Kobolediator_Entity;
import com.github.L_Ender.cataclysm.entity.etc.Animation_Monsters;
import com.github.L_Ender.cataclysm.init.ModEntities;
import net.casual.arcade.dimensions.level.CustomLevel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.entity.living.LivingShieldBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jahdoo.common.components.LootCrateData;
import org.jahdoo.common.registers.AttachmentReg;
import org.jahdoo.common.registers.ItemReg;
import org.jahdoo.common.registers.SoundReg;
import org.jahdoo.trial_nexus.attachments.CasterData;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.jahdoo.trial_nexus.level_manager.InstanceDifficulty;
import org.jahdoo.trial_nexus.level_manager.LevelGenerator;
import org.jahdoo.trial_nexus.utils.JahdooHelpers;
import org.jahdoo.trial_nexus.utils.LocalLootBeamData;
import org.shaydee.loot_beams_neoforge.components.LootBeamComponent;
import org.shaydee.shaydeeapi.Helpers;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;
import org.shaydee.shaydeeapi.helpers.TextHelpers;

import java.util.List;

import static net.minecraft.world.level.block.LightBlock.LEVEL;
import static org.jahdoo.common.block.altar.AltarBlockEntity.getAllBlockPos;
import static org.jahdoo.common.block.altar.AltarBlockEntity.roomBounding;
import static org.jahdoo.common.block.altar.altar_states.EndAltar.*;
import static org.jahdoo.common.particle.ParticleHandlers.getNonBakedParticles;
import static org.jahdoo.common.particle.ParticleHandlers.sendParticles;
import static org.jahdoo.trial_nexus.level_manager.BlockSetupManager.blockExitBarrier;
import static org.jahdoo.trial_nexus.level_manager.StructureManager.placeLocksWithData;
import static org.jahdoo.trial_nexus.loot.LootHelpers.itemBehaviour;
import static org.jahdoo.trial_nexus.loot.RewardLootTables.getCompletionLoot;
import static org.jahdoo.trial_nexus.mobs.MobSpawnManager.generateMob;
import static org.jahdoo.trial_nexus.utils.JahdooHelpers.Random;
import static org.jahdoo.trial_nexus.utils.PositionFinders.innerRadiusRandom;

public class MiniBossMobs {

    public static String CHALLENGER_BOSS = "jahdoo.challenger.id";

    public static LivingEntity getClawdian(ServerLevel serverLevel, InstanceData instanceData) {
        var livingEntity = new Clawdian_Entity(ModEntities.CLAWDIAN.get(), serverLevel);
        return generateMob(livingEntity,  instanceData);
    }

    public static LivingEntity getAptrgangr(ServerLevel serverLevel, InstanceData instanceData) {
        var livingEntity = new Aptrgangr_Entity(ModEntities.APTRGANGR.get(), serverLevel);
        return generateMob(livingEntity,  instanceData);
    }

    public static LivingEntity getKobolediator(ServerLevel serverLevel, InstanceData instanceData) {
        var livingEntity = new Kobolediator_Entity(ModEntities.KOBOLEDIATOR.get(), serverLevel);
        return generateMob(livingEntity,  instanceData);
    }

    public static LivingEntity getRandomMiniBoss(ServerLevel serverLevel, InstanceData instanceData) {
        var getBosses = List.of(
            getClawdian(serverLevel, instanceData),
            getAptrgangr(serverLevel, instanceData),
            getKobolediator(serverLevel, instanceData)
        );
        return Helpers.listRandom(getBosses);
    }

    public static void rightClickInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        var target = event.getTarget();
        var entity = event.getEntity();

        if(entity instanceof ServerPlayer serverPlayer && target instanceof Mob mob){
            var b = MiniBossMobs.onInteract(serverPlayer, mob);
            event.setCancellationResult(b ? InteractionResult.SUCCESS : InteractionResult.FAIL);
        }
    }

    public static void tickDeathLootsplotion(EntityTickEvent.Pre event) {
        if(LevelGenerator.isNexus(event.getEntity().level())){
            var entity = event.getEntity();
            if (entity instanceof Animation_Monsters animationMonsters) {
                if (animationMonsters.deathTime > 0) {
                    MiniBossMobs.onDeathTick(animationMonsters, animationMonsters.deathTime);
                }
            }
        }
    }

    public static LivingEntity getMiniBoss(Direction direction, ServerLevel serverLevel, InstanceData data){
        var miniBoss = getRandomMiniBoss(serverLevel, data);
        if(miniBoss instanceof Mob mob) mob.setNoAi(true);

        miniBoss.setYBodyRot(direction.toYRot());
        return miniBoss;
    }

    public static void blockNoAIDamage(LivingShieldBlockEvent event, LivingEntity entity) {
        if(LevelGenerator.isNexus(entity.level())){
            if(entity instanceof Internal_Animation_Monster monster){
                if(entity.getPersistentData().getBoolean(CHALLENGER_BOSS)){
                    if(monster.isNoAi()){
                        event.setBlocked(true);
                        monster.removeAllEffects();
                    }
                }
            }
        }
    }

    public static void onDeathTick(LivingEntity entity, int animTick) {
        var level = entity.level();
        var instanceData = level.getData(AttachmentReg.INSTANCE_DATA);
        var killCredit = entity.getKillCredit();
        if(killCredit instanceof Player player) {
            var level1 = CasterData.getLevel(player);
            var crateData = new LootCrateData(level1, 10, 0, instanceData.getDifficulty());
            var difficulty = InstanceDifficulty.getFromName(crateData.difficulty());
            var chestRarity = difficulty.getId();

            if (level instanceof ServerLevel serverLevel) {
                var rewards = getCompletionLoot(serverLevel, entity.position(), difficulty.getSerializedName(), chestRarity);
                var random = Helpers.listRandom(rewards);

                if(animTick == 1){
                    var reward = new ItemStack(ItemReg.CHALLENGER_SOUL);
                    LootBeamComponent.toLootBeamComponent(LocalLootBeamData.soulItemBeam(), reward);
                    itemBehaviour(entity.position(), serverLevel, ColourHelpers.getRgb(), false, 20, chestRarity, reward);
                }

                if(entity.tickCount % 2 == 0) {
                    itemBehaviour(entity.position(), serverLevel, ColourHelpers.getRgb(), false, 20, chestRarity, random);
                }
            }
        }
    }

    public static boolean onInteract(Player player, Mob mob){
        if(mob.isNoAi() && LevelGenerator.isNexus(player.level()) && mob instanceof Animation_Monsters) {
            var pos = mob.blockPosition();

            if(mob.level() instanceof ServerLevel serverLevel){
                blockExitBarrier(serverLevel, pos);
                sendNotification(serverLevel, pos, SoundReg.START_TRIAL.get(), TextHelpers.withStyleComponentTrans("info.jahdoo.challenge_accepted", ColourHelpers.getMagnetStrengthRed()));
                var getPositions = innerRadiusRandom(pos.getCenter().subtract(0, 1, 0), 2, 100);
                for (var vec3 : getPositions) {
                    var colour = ColourHelpers.getMagnetStrengthRed();
                    var particle = getNonBakedParticles(colour, colour, Random.nextInt(6, 12), Random.nextInt(2, 4));
                    sendParticles(serverLevel, particle, vec3, 0, 0, 0.5, 0, Random.nextDouble(0.6, 2.2));
                }

            }

            var getWithBounding = roomBounding(pos);
            var bounding = getAllBlockPos(getWithBounding);

            if(player instanceof ServerPlayer serverPlayer){
                JahdooHelpers.sendClientSound(serverPlayer, SoundReg.BOSS.get(), 0.1F, 1, true);
                JahdooHelpers.sendClientSound(serverPlayer, SoundReg.MAGIC_EXPLOSION.get(), 0.5F, 1.8F, false);
                JahdooHelpers.sendClientSound(serverPlayer, SoundReg.BLOCK.get(), 0.4F, 0.8F, false);
                JahdooHelpers.sendClientSound(serverPlayer, SoundReg.BLOCK.get(), 0.4F, 0.4F, false);
            }

            for (var blockPos : bounding) {
                if(mob.level() instanceof ServerLevel serverLevel){
                    var blockState = serverLevel.getBlockState(blockPos);
                    var b = blockState.is(Blocks.LIGHT);
                    if(b){
                        serverLevel.setBlockAndUpdate(blockPos, blockState.setValue(LEVEL, Random.nextInt(0, 15)));
                    }
                }
            }

            mob.setNoAi(false);
            return true;
        }

        return false;
    }

    public static void onDeath(LivingEntity entity){
        if(!entity.getPersistentData().getBoolean(CHALLENGER_BOSS)) return;
        if(entity.level() instanceof CustomLevel cLevel && LevelGenerator.isNexus(cLevel)){
            var pos = NbtUtils.readBlockPos(entity.getPersistentData(), "block_pos");
            var direction = entity.getPersistentData().getString("direction");
            var name = Direction.byName(direction.toLowerCase());

            if(pos.isPresent()){
                var get = pos.get();
                placeLocksWithData(cLevel, get.below(2), false, false);
                sendAlterEndNotification(cLevel, get);
                removeLootAndOres(cLevel, get);
                for (var player : cLevel.players()) {
                    JahdooHelpers.sendClientSound(player, SoundReg.LOOP.get(), 0.4F, 1, true);
                    JahdooHelpers.sendClientSound(player, SoundReg.END_TRIAL.get(), 0.4F, 1, false);
                }

                var data = cLevel.getData(AttachmentReg.INSTANCE_DATA);
                data.incrementClearedRooms();
                if(name != null){
                    setEndFocusBlock(data, cLevel, get, name);
                }
            }
        }
    }
}
