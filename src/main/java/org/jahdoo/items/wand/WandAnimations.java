package org.jahdoo.items.wand;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jahdoo.utils.GeneralHelpers;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.List;

public class WandAnimations {
    public static final String IDLE_ID = "idle";
    public static final String SINGLE_CAST_ID = "single_cast_1";
    public static final String CANT_CAST_ID = "cant_cast";
    public static final String HOLD_CAST_ID = "hold_cast";
    public static final String ROTATION_CAST_ID = "rotation_cast";

    public static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop(IDLE_ID);
    public static final RawAnimation SINGLE_CAST = RawAnimation.begin().thenPlay(SINGLE_CAST_ID);
    public static final RawAnimation CANT_CAST = RawAnimation.begin().thenPlay(CANT_CAST_ID);
    public static final RawAnimation HOLD_CAST = RawAnimation.begin().thenPlay(HOLD_CAST_ID);
    public static final RawAnimation ROTATION_CAST = RawAnimation.begin().thenPlay(ROTATION_CAST_ID);

    public static List<String> castAnims = List.of(SINGLE_CAST_ID, ROTATION_CAST_ID, HOLD_CAST_ID);

    public static void triggerAnimWithController(SingletonGeoAnimatable animate, ItemStack itemStack, ServerLevel serverLevel, Entity entity, String anim){
        animate.triggerAnim(entity, GeoItem.getOrAssignId(itemStack, serverLevel), "Activation", anim);
    }

    public static void playRandomCastAnim(SingletonGeoAnimatable animate, ItemStack itemStack, ServerLevel serverLevel, Entity entity){
        animate.triggerAnim(entity, GeoItem.getOrAssignId(itemStack, serverLevel), "Activation", castAnims.get(GeneralHelpers.Random.nextInt(0, castAnims.size())));
    }

}
