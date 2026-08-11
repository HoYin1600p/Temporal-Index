package com.hoyin1600p.temporalindex.network;

import com.hoyin1600p.temporalindex.TemporalIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class TemporalIndexNetwork {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TemporalIndex.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private TemporalIndexNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(
                0,
                CycleSelectionMessage.class,
                CycleSelectionMessage::encode,
                CycleSelectionMessage::decode,
                CycleSelectionMessage::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
    }
}
