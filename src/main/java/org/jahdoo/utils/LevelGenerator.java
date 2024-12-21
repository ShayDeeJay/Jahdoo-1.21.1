package org.jahdoo.utils;

public class LevelGenerator {


//    private static void deleteCurrentWorld(Player player, ServerLevel serverLevel) {
//        for (ServerLevel allLevel : serverLevel.getServer().getAllLevels()) {
//            if(allLevel instanceof CustomLevel customLevel){
//                System.out.println(customLevel);
//            }
//        }
//
//        ArcadeDimensions.delete(serverLevel.getServer(), (CustomLevel) player.level());
//    }

//    private static void createNewWorld(Player player, ServerLevel serverLevel) {
//        ResourceKey<Level> levelResourceKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.withDefaultNamespace("overworld"));
//        var builder = new CustomLevelBuilder()
//            .randomDimensionKey()
//            .gameRules(
//        (gameRules) -> {
//                    gameRules.getRule(GameRules.RULE_DOMOBSPAWNING).set(true, null);
//                    gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
//                    gameRules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, null);
//                    return null;
//                }
//            )
//            .timeOfDay(18000)
//            .randomSeed()
//            .vanillaDefaults(VanillaDimension.Overworld);
//            .build(serverLevel.getServer());
//
//        var newLevel = ArcadeDimensions.add(serverLevel.getServer(), builder);
//        var newLevelSpawn = newLevel.getSharedSpawnPos();
//        player.teleportTo(newLevel, newLevelSpawn.getX(), newLevelSpawn.getY(), newLevelSpawn.getZ(), Set.of(),0,0);
//    }
}
