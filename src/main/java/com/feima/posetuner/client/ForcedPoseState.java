package com.feima.posetuner.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/** 调试模式：本地开关控制是否应用配置里的姿态。 */
public final class ForcedPoseState {

    private ForcedPoseState() {}

    private static boolean active = false;

    /** 锁定的身体朝向，硬编码为北（yaw = 180）。 */
    private static final float LOCKED_YAW = 180F;

    public static boolean isActive() {
        return active;
    }

    /** 锁定的身体朝向。只在 {@link #isActive()} 为 true 时被读取。 */
    public static float getLockedYaw() {
        return LOCKED_YAW;
    }

    public static void toggle() {
        active = !active;
        chat("[PoseTuner] " + (active ? "ON" : "OFF"));
    }

    private static void chat(String msg) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.displayClientMessage(Component.literal(msg), true);
        }
    }
}