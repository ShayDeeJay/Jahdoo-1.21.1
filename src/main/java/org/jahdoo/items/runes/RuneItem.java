package org.jahdoo.items.runes;

import com.mojang.serialization.MapCodec;
//import net.casual.arcade.dimensions.ArcadeDimensions;
//import net.casual.arcade.dimensions.level.CustomLevel;
//import net.casual.arcade.dimensions.level.LevelProperties;
//import net.casual.arcade.dimensions.level.builder.CustomLevelBuilder;
//import net.casual.arcade.dimensions.level.vanilla.VanillaDimension;
//import net.casual.arcade.dimensions.utils.DimensionRegistryKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.feature.foliageplacers.AcaciaFoliagePlacer;
import org.jahdoo.ability.JahdooRarity;
import org.jahdoo.components.RuneData;
import org.jahdoo.items.augments.AugmentItemHelper;
import org.jahdoo.registers.DataComponentRegistry;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.jahdoo.components.RuneData.RuneHelpers.*;

public class RuneItem extends Item {
    public RuneItem() {
        super(new Properties().component(DataComponentRegistry.RUNE_DATA.get(), RuneData.DEFAULT));
    }

    @Override
    public Component getName(ItemStack stack) {
        return getNameWithStyle(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(JahdooRarity.attachRuneRarityTooltip(stack));
//        tooltipComponents.add(Component.empty());
        tooltipComponents.add(standAloneAttributes(stack));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if(!level.isClientSide){
            var stack = player.getMainHandItem();
            var newStack = stack.copyWithCount(1);
            stack.shrink(1);
            generateRandomTypAttribute(newStack);
            AugmentItemHelper.throwOrAddItem(player, newStack);
        }

//        if(level instanceof ServerLevel serverLevel){
//            createNewWorld(player, serverLevel);
//            deleteCurrentWorld(player, serverLevel);
//        }
        return InteractionResultHolder.fail(player.getMainHandItem());
    }

//    private static void deleteCurrentWorld(Player player, ServerLevel serverLevel) {
//        for (ServerLevel allLevel : serverLevel.getServer().getAllLevels()) {
//            if(allLevel instanceof CustomLevel customLevel){
//                System.out.println(customLevel);
//            }
//        }
//
//        ArcadeDimensions.delete(serverLevel.getServer(), (CustomLevel) player.level());
//    }
//
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
////            .build(serverLevel.getServer());
//
//        var newLevel = ArcadeDimensions.add(serverLevel.getServer(), builder);
//        var newLevelSpawn = newLevel.getSharedSpawnPos();
//        player.teleportTo(newLevel, newLevelSpawn.getX(), newLevelSpawn.getY(), newLevelSpawn.getZ(), Set.of(),0,0);
//    }
}
