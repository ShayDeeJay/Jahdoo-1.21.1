package org.jahdoo.ascension;

import net.minecraft.world.phys.Vec3;

import static org.jahdoo.ascension.StructureManager.GLOBAL_Y;

public record DimHandler(Vec3 spawn, String id) {

    public static final String TRIAL = "trial";

    public static DimHandler trial(){
        return new DimHandler(new Vec3(33.5, GLOBAL_Y + 2, 27.5), TRIAL);
    }

}