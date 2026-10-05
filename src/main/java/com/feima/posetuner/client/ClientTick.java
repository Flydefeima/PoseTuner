package com.feima.posetuner.client;

import com.feima.posetuner.PoseTunerMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PoseTunerMod.MODID, value = Dist.CLIENT)
public final class ClientTick {

    private ClientTick() {}

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        while (ClientKeys.TOGGLE.consumeClick()) {
            ForcedPoseState.toggle();
        }
    }
}