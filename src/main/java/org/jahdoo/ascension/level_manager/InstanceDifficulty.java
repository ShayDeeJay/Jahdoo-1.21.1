package org.jahdoo.ascension.level_manager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.asm.enumextension.IExtensibleEnum;
import org.jahdoo.ascension.utils.Helpers;
import org.jahdoo.common.client.Icons;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static net.minecraft.util.FastColor.ARGB32.color;
import static org.jahdoo.common.registers.AttachmentReg.INSTANCE_DATA;

public enum InstanceDifficulty  implements StringRepresentable, IExtensibleEnum {

    EASY(Helpers.EASY, color(224, 181, 149), Icons.EASY),
    MEDIUM(Helpers.MEDIUM, color(139, 203, 225), Icons.MEDIUM),
    HARD(Helpers.HARD, color(223, 199, 241), Icons.HARD);

    private final String name;
    private final int color;
    private final ResourceLocation icon;

    InstanceDifficulty(String name, int color, ResourceLocation icon) {
        this.name = name;
        this.color = color;
        this.icon = icon;
    }

    @Override
    public String getSerializedName() {
        return this.name.toLowerCase();
    }

    public int getColor() {
        return color;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public static List<InstanceDifficulty> getDifficulties(){
        return Arrays.stream(InstanceDifficulty.values()).toList();
    }

    public static InstanceDifficulty getFromName(String name){
        return getDifficulties().stream().filter(d -> Objects.equals(d.getSerializedName(), name)).toList().getFirst();
    }

    public static InstanceDifficulty getFromLevel(Level level){
        var data = level.getData(INSTANCE_DATA);
        var name = data.getDifficulty();
        return getDifficulties().stream().filter(d -> Objects.equals(d.getSerializedName(), name)).toList().getFirst();
    }

}
