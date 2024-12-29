package org.jahdoo.entities;


import software.bernie.geckolib.animation.RawAnimation;

public class ProjectileAnimations {
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    public static final RawAnimation ICE_SPIKES = RawAnimation.begin().thenLoop("ice_spikes");
    public static final RawAnimation FIREBALL = RawAnimation.begin().thenLoop("fireball");
    public static final RawAnimation FIREBALL_EXPLODE = RawAnimation.begin().thenPlayAndHold("fireball_explode");
    public static final RawAnimation QUANTUM_EXPANSION = RawAnimation.begin().thenLoop("quantum");
    public static final RawAnimation QUANTUM_COMBUSTION = RawAnimation.begin().thenLoop("quantum_end");
    public static final RawAnimation SEMTEX = RawAnimation.begin().thenLoop("mystical_semtex");
    public static final RawAnimation ORB = RawAnimation.begin().thenLoop("energy_orb");
    public static final RawAnimation BOLTZ = RawAnimation.begin().thenLoop("boltz");
    public static final RawAnimation ORB_END = RawAnimation.begin().thenLoop("energy_orb_end");

    public static final RawAnimation IDLE_BLOCK = RawAnimation.begin().thenPlay("idle");
    public static final RawAnimation WEST = RawAnimation.begin().thenPlay("direction1");
    public static final RawAnimation EAST = RawAnimation.begin().thenPlay("direction2");
    public static final RawAnimation SOUTH = RawAnimation.begin().thenPlay("direction3");
    public static final RawAnimation UP = RawAnimation.begin().thenPlay("direction4");
    public static final RawAnimation DOWN = RawAnimation.begin().thenPlay("direction5");
    public static final RawAnimation NORTH = RawAnimation.begin().thenPlay("direction6");


    public static final RawAnimation ALTAR_IDLE = RawAnimation.begin().thenPlay("idle");
    public static final RawAnimation ALTAR_INITIATE = RawAnimation.begin().thenPlay("initiate").thenPlay("idle");
    public static final RawAnimation ALTAR_SPAWNING = RawAnimation.begin().thenPlay("spawning");

}
