package com.feima.posetuner.client;

import com.feima.posetuner.PoseTunerMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(
        modid = PoseTunerMod.MODID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public final class ClientKeys {

    private ClientKeys() {}

    public static final String CATEGORY = "key.categories.posetuner";

    /** 开关强制姿态预览，默认 P。 */
    public static final KeyMapping TOGGLE = new KeyMapping(
            "key.posetuner.toggle",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            CATEGORY
    );

    @SubscribeEvent
    public static void onRegister(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE);
    }
}