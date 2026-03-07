package org.jahdoo.trial_nexus.level_manager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import org.jahdoo.trial_nexus.utils.Icons;
import org.jahdoo.trial_nexus.attachments.InstanceData;
import org.shaydee.shaydeeapi.helpers.ColourHelpers;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.jahdoo.trial_nexus.utils.JahdooHelpers.*;

public enum InstanceDifficulty implements StringRepresentable, IExtensibleEnum {

    NOVICE(1, EASY, ColourHelpers.getBronzeCoin(), Icons.EASY, 1, 2, 10, 5),
    EXPERT(2, MEDIUM, ColourHelpers.getSilverCoin(), Icons.MEDIUM, 3, 10, 20, 10),
    MASTER(3, HARD, ColourHelpers.getGoldCoin(), Icons.HARD, 6, 20, 40, 20);

    private final String name;
    private final ResourceLocation icon;
    private final int id;
    private final int color;
    private final int experienceMultiplier;
    private final int specialSpawnChance;
    private final int maxMobsOnField;
    private final int intervalsTillSanctum;

    InstanceDifficulty(
        int id,
        String name,
        int color,
        ResourceLocation icon,
        int experienceMultiplier,
        int specialSpawnChance,
        int maxAllowedMobs,
        int intervalsTillSanctum
    ) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.experienceMultiplier = experienceMultiplier;
        this.specialSpawnChance = specialSpawnChance;
        this.maxMobsOnField = maxAllowedMobs;
        this.intervalsTillSanctum = intervalsTillSanctum;
    }

    @Override
    public String getSerializedName() {
        return this.name.toLowerCase();
    }

    public int getColor() {
        return color;
    }

    public int getId() {
        return id;
    }

    public int getMaxMobsOnField() {
        return maxMobsOnField;
    }

    public int getSpecialSpawnChance() {
        return specialSpawnChance;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public int expMultiplier() {
        return experienceMultiplier;
    }

    public int getSanctumIntervals() {
        return intervalsTillSanctum;
    }

    public static List<InstanceDifficulty> getDifficulties() {
        return Arrays.stream(InstanceDifficulty.values()).toList();
    }

    public static InstanceDifficulty getFromName(String name) {
        return getDifficulties().stream()
            .filter(d -> Objects.equals(d.getSerializedName(), name))
            .findFirst()
            .orElse(NOVICE);
    }

    public static InstanceDifficulty getFromLevel(InstanceData data) {
        var name = data.getDifficulty();
        return getFromName(name);
    }
}
