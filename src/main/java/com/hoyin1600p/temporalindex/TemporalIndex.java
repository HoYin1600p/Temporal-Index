package com.hoyin1600p.temporalindex;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(TemporalIndex.MOD_ID)
public final class TemporalIndex {
    public static final String MOD_ID = "temporal_index";

    private static final Logger LOGGER = LogUtils.getLogger();

    public TemporalIndex() {
        LOGGER.info("Temporal Index initialized");
    }
}
