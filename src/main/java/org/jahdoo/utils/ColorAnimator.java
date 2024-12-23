//package org.jahdoo.utils;
//
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.network.chat.TextColor;
//import net.minecraft.util.FastColor;
//import org.joml.Vector3f;
//import org.joml.Vector3i;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ColorAnimator {
//    private final List<Vector3f> colors = new ArrayList<>();
//    private final int nextColorTime;
//
//    public ColorAnimator(int nextColorTime) {
//        this.nextColorTime = nextColorTime;
//    }
//
//    public static ColorAnimator createRainbow(int cooldown) {
//        return create(cooldown).addFrames(new float[][] {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}});
//    }
//
//    public static ColorAnimator createLightRainbow(int cooldown) {
//        return create(cooldown).addFrames(new float[][] {
//            {1, 0, 0}, {1, 1, 0}, {0, 1, 0}, {0, 1, 1}, {0, 0, 1}, {1, 0, 1}
//        });
//    }
//
//
//    public static ColorAnimator simple(Vector3f color) {
//        return create(0).addFrame(color);
//    }
//
//
//    public Vector3f getAsSimple() {
//        if (this.colors.size() == 1) {
//            return this.colors.get(0);
//        }
//        throw new IllegalStateException("color animator was not simple");
//    }
//
//    public static ColorAnimator create(int nextColorTime) {
//        return new ColorAnimator(nextColorTime);
//    }
//
//    public ColorAnimator addFrame(Vector3f color) {
//        colors.add(color);
//        return this;
//    }
//
//    public ColorAnimator addFrame(float r, float g, float b) {
//        return addFrame(new Vector3f(r, g, b));
//    }
//
//    public ColorAnimator addFrames(float[][] matrix) {
//        for (float[] color : matrix) {
//            addFrame(color[0], color[1], color[2]);
//        }
//        return this;
//    }
//
//    @SuppressWarnings("ALL")
//    public Vector3f getColor(int curTime) {
//        Vector3f oldColor = null;
//        curTime %= getLength();
//        if (colors.size() == 0) throw new IllegalArgumentException("can not make color animation with 0 colors");
//        if (colors.size() == 1) return colors.get(0);
//        for (Vector3f entry : colors) {
//            if (nextColorTime > curTime) {
//                if (oldColor == null) {
//                    return getColor(curTime, colors.get(colors.size() - 1), entry);
//                } else {
//                    return getColor(curTime, oldColor, entry);
//                }
//            }
//            oldColor = entry;
//            curTime -= nextColorTime;
//        }
//        return getColor(curTime, oldColor, colors.get(0));
//    }
//
//    public TextColor makeTextColor(int curTime)  {
//        return TextColor.fromRgb(FastColor.ARGB32.color(getColor(curTime)));
//    }
//
//    private Vector3f getColor(int curTime, Vector3f first, Vector3f second) {
//        float percentage = MathHelper.makePercentage(curTime, nextColorTime);
//        float xOff = second.x - first.x;
//        float yOff = second.y - first.y;
//        float zOff = second.z - first.z;
//        xOff *= percentage;
//        yOff *= percentage;
//        zOff *= percentage;
//        return new Vector3f(first.x + xOff, first.y + yOff, first.z + zOff);
//    }
//
//    public int getLength() {
//        return nextColorTime * colors.size();
//    }
//
//    public void writeToBytes(FriendlyByteBuf buf) {
//        buf.writeInt(getLength());
//        buf.writeInt(nextColorTime);
//        colors.forEach((vector3f) -> NetworkingHelper.writeVector3f(buf, vector3f));
//    }
//
//    public static ColorAnimator fromBuf(FriendlyByteBuf buf) {
//        int length = buf.readInt();
//        int nextColorTime = buf.readInt();
//        ColorAnimator animator = create(nextColorTime);
//        MiscHelper.repeat(length, integer -> {
//            Vector3f color = DustParticleOptionsBase.readVector3f(buf);
//            animator.addFrame(color);
//        });
//        return animator;
//    }
//
//   public static Vector3i intToRGB(int in) {
//        int r = in >> 16 & 255;
//        int g = in >> 8 & 255;
//        int b = in & 255;
//        return new Vector3i(r, g, b);
//    }
//}