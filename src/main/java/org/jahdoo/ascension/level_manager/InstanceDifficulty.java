package org.jahdoo.ascension.level_manager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import org.jahdoo.ascension.utils.ColourStore;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.client.Icons;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public enum InstanceDifficulty implements StringRepresentable, IExtensibleEnum {

    NOVICE(1, Helpers.EASY, ColourStore.BRONZE_COIN, Icons.EASY, 1, 2),
    EXPERT(2, Helpers.MEDIUM, ColourStore.SILVER_COIN, Icons.MEDIUM, 3, 10),
    MASTER(3, Helpers.HARD, ColourStore.GOLD_COIN, Icons.HARD, 6, 20);

    private final int id;
    private final String name;
    private final int color;
    private final ResourceLocation icon;
    private final int experienceMultiplier;
    private final int specialSpawnChance;

    InstanceDifficulty(int id, String name, int color, ResourceLocation icon, int experienceMultiplier, int specialSpawnChance) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.icon = icon;
        this.experienceMultiplier = experienceMultiplier;
        this.specialSpawnChance = specialSpawnChance;
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

    public int getSpecialSpawnChance() {
        return specialSpawnChance;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public int expMultiplier() {
        return experienceMultiplier;
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

    public static InstanceDifficulty getFromLevel(Level level) {
        var data = level.getData(INSTANCE_DATA);
        var name = data.getDifficulty();
        return getFromName(name);
    }
}
