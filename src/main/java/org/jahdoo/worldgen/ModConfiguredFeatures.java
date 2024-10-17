package org.jahdoo.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import org.jahdoo.registers.BlocksRegister;
import org.jahdoo.utils.GeneralHelpers;

import java.util.List;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_CRYSTAL_ORE_KEY = registerKey("crystal_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> OVERWORLD_CRYSTAL_DEEPSLATE_ORE_KEY = registerKey("crystal_deepslate_ore");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneReplaceable = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        List<OreConfiguration.TargetBlockState> overworldCrystalOres = List.of(
            OreConfiguration.target(stoneReplaceable, BlocksRegister.CRYSTAL_ORE.get().defaultBlockState()),
            OreConfiguration.target(deepslateReplaceables, BlocksRegister.CRYSTAL_DEEPSLATE_ORE.get().defaultBlockState())
        );

        register(context, OVERWORLD_CRYSTAL_ORE_KEY, Feature.ORE, new OreConfiguration(overworldCrystalOres, 16));
        register(context, OVERWORLD_CRYSTAL_DEEPSLATE_ORE_KEY, Feature.ORE, new OreConfiguration(overworldCrystalOres, 16));
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, GeneralHelpers.modResourceLocation(name));
    }
    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(
        BootstrapContext<ConfiguredFeature<?, ?>> context,
        ResourceKey<ConfiguredFeature<?, ?>> key,
        F feature,
        FC configuration
    ) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
