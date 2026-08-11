package com.hoyin1600p.temporalindex.menu;

import com.hoyin1600p.temporalindex.storage.TemporalIndexStorage;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public final class TemporalIndexSlot extends SlotItemHandler {
    public static final int COMPACT_COUNT_THRESHOLD = 99;

    public TemporalIndexSlot(IItemHandler handler, int index, int x, int y) {
        super(handler, index, x, y);
    }

    @Override
    public int getMaxStackSize() {
        return TemporalIndexStorage.MAX_PER_SLOT;
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return TemporalIndexStorage.MAX_PER_SLOT;
    }
}
