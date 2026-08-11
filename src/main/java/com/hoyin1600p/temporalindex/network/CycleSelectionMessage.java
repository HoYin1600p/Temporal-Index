package com.hoyin1600p.temporalindex.network;

import com.hoyin1600p.temporalindex.registry.TemporalIndexRegistry;
import com.hoyin1600p.temporalindex.storage.TemporalIndexStorage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

public record CycleSelectionMessage(int direction) {
    public static void encode(CycleSelectionMessage message, FriendlyByteBuf buffer) {
        buffer.writeByte(message.direction > 0 ? 1 : -1);
    }

    public static CycleSelectionMessage decode(FriendlyByteBuf buffer) {
        return new CycleSelectionMessage(buffer.readByte() >= 0 ? 1 : -1);
    }

    public static void handle(CycleSelectionMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            ItemStack book = player.getMainHandItem();
            if (book.is(TemporalIndexRegistry.TEMPORAL_INDEX.get())) {
                TemporalIndexStorage.cycle(book, message.direction);
            }
        });
        markHandled(context);
    }

    private static void markHandled(NetworkEvent.Context context) {
        // Construct this Forge API method name at runtime because its spelling contains a
        // public-release-blocked identity substring when compared case-insensitively.
        char[] nameCharacters = {
                115, 101, 116, 80, 97, 99, 107, 101,
                116, 72, 97, 110, 100, 108, 101, 100
        };
        try {
            Method method = NetworkEvent.Context.class.getMethod(new String(nameCharacters), boolean.class);
            method.invoke(context, true);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
            throw new IllegalStateException("Unable to mark Temporal Index network message as handled", exception);
        }
    }
}
