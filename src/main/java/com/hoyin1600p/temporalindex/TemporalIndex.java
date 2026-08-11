package com.hoyin1600p.temporalindex;

import com.mojang.logging.LogUtils;
import com.hoyin1600p.temporalindex.network.TemporalIndexNetwork;
import com.hoyin1600p.temporalindex.registry.TemporalIndexRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(TemporalIndex.MOD_ID)
public final class TemporalIndex {
    public static final String MOD_ID = "temporal_index";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TemporalIndex() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        TemporalIndexRegistry.ITEMS.register(modBus);
        TemporalIndexRegistry.MENUS.register(modBus);
        TemporalIndexNetwork.register();
    }
}
