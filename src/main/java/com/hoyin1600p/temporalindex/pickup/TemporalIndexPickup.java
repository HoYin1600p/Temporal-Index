package com.hoyin1600p.temporalindex.pickup;

import com.hoyin1600p.temporalindex.menu.TemporalIndexMenu;
import com.hoyin1600p.temporalindex.registry.TemporalIndexRegistry;
import com.hoyin1600p.temporalindex.storage.TemporalIndexItemHandler;
import com.hoyin1600p.temporalindex.storage.TemporalIndexStorage;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class TemporalIndexPickup {
    private TemporalIndexPickup() {
    }

    public static boolean tryInsert(Inventory inventory, ItemStack incoming) {
        if (incoming.isEmpty() || inventory.player.level.isClientSide
                || inventory.player.containerMenu instanceof TemporalIndexMenu) {
            return false;
        }

        int targetSlot = TemporalIndexStorage.slotFor(incoming);
        if (targetSlot < 0) {
            return false;
        }

        // Inventory.items is ordered hotbar 1-9, then main inventory 1-27.
        for (ItemStack candidate : inventory.items) {
            if (!candidate.is(TemporalIndexRegistry.TEMPORAL_INDEX.get())) {
                continue;
            }

            ItemStack remainder = new TemporalIndexItemHandler(candidate).insertItem(targetSlot, incoming, false);
            if (!remainder.isEmpty() && remainder.hasTag()) {
                incoming.setTag(remainder.getTag().copy());
            }
            incoming.setCount(remainder.getCount());
            if (incoming.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
