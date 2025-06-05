package org.jahdoo.trial_nexus.boon.level_boons;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.jahdoo.trial_nexus.rarity.JahdooRarity;
import org.jahdoo.trial_nexus.utils.Maths;

import java.util.Objects;

import static java.lang.String.valueOf;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.resources.ResourceLocation.withDefaultNamespace;
import static org.jahdoo.trial_nexus.utils.ColourStore.MAGNET_RANGE_GREEN;
import static org.jahdoo.trial_nexus.utils.ColourStore.MAGNET_STRENGTH_RED;
import static org.jahdoo.trial_nexus.utils.Helpers.*;

public abstract class AbstractLevelBoon {

    abstract public String id();

    abstract public boolean isPositive();

    abstract public boolean isPercentageOf();

    abstract public ResourceLocation getIcon();

    abstract public double value(JahdooRarity rarity);

    abstract public JahdooRarity rarity();

    abstract public void execute(ServerLevel level, double value);

    public int getHeaderColour(){
        return -1;
    };

    public int getStampIndex(){
        return -1;
    }

    public int textColour(){
        return isPositive() ? MAGNET_RANGE_GREEN : MAGNET_STRENGTH_RED;
    }

    public Component boonLabel(double value, String id){
        var displayValue = Maths.roundNonWholeString(value);
        var displayTime = Maths.ticksToTime(valueOf(value));
        var getBy = Objects.equals(id, "time") ? displayTime : displayValue;

        return withStyleComponent("+" + getBy + (isPercentageOf() ? "% " : " ") + stringIdToName(id), textColour());
    }

    public record SyncableData(String id, ResourceLocation icon, double value, Component label){

        public static final SyncableData EMPTY =  new SyncableData("", withDefaultNamespace(""), 0.0, empty());

        public static SyncableData toSyncable(AbstractLevelBoon boon, JahdooRarity rarity){
            var value = boon.value(rarity);
            var id = boon.id();
            return new SyncableData(id, boon.getIcon(), value, boon.boonLabel(value, id));
        }

        public static void saveSyncable(CompoundTag tag, HolderLookup.Provider registries, SyncableData boon, String name){
            var comp = new CompoundTag();
            comp.putString("id", boon.id);
            comp.putString("icon", boon.icon.getPath());
            comp.putDouble("value", boon.value);
            comp.putString("label", Component.Serializer.toJson(boon.label, registries));
            tag.put(name, comp);
        }

        public static SyncableData loadData(CompoundTag tag, HolderLookup.Provider registries, String name){
            var comp = tag.getCompound(name);
            var icon = comp.getString("icon");
            var label = Component.Serializer.fromJson(comp.getString("label"), registries);
            var id = comp.getString("id");
            var icons = icon.contains("mob_effect") || icon.isEmpty() ?  withDefaultNamespace(icon) : res(icon);
            var value = comp.getDouble("value");

            return new SyncableData(id, icons, value, label);
        }
    }
}
