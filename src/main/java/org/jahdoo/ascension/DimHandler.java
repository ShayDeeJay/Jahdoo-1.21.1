package org.jahdoo.ascension;

import net.minecraft.world.phys.Vec3;

public record DimHandler(Vec3 spawn, String id) {

    public static final String TRIAL = "trial";

    public static DimHandler trial(){
        return new DimHandler(new Vec3(23.5, 42, 27.5), TRIAL);
    }

}