package com.feima.posetuner;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(PoseTunerMod.MODID)
public class PoseTunerMod {

    public static final String MODID = "posetuner";
    public static final Logger LOGGER = LogUtils.getLogger();

    public PoseTunerMod() {
        ModLoadingContext.get().registerConfig(
                ModConfig.Type.CLIENT,
                TunerConfig.SPEC,
                "posetuner.toml"
        );
    }
}