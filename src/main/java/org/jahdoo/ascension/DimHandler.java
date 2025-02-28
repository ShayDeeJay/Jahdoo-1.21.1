package org.jahdoo.ascension;

import net.minecraft.world.phys.Vec3;

public record DimHandler(Vec3 spawn, String id) {
    public static final String TRIAL = "trial";
    public static final String TRADING_POST = "trading_post";

    public static DimHandler trial(){
        return new DimHandler(new Vec3(-24.5, 65, -120.5), TRIAL);
    }
    public static DimHandler tradingPost(){
        return new DimHandler(new Vec3(-22.5, 51, -42.5), TRADING_POST);
    }
}