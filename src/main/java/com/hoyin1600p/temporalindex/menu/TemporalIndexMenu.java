package com.hoyin1600p.temporalindex.menu;

import com.hoyin1600p.temporalindex.registry.TemporalIndexRegistry;
import com.hoyin1600p.temporalindex.storage.TemporalIndexItemHandler;
import com.hoyin1600p.temporalindex.storage.TemporalIndexStorage;
import com.hoyin1600p.temporalindex.storage.TemporalRelics;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class TemporalIndexMenu extends OverSizedSlotContainer {
    public static final int RELIC_COLUMNS = 8;
    public static final int BOOK_SLOT_COUNT = TemporalIndexStorage.SLOT_COUNT;
    public static final int PLAYER_SLOT_START = BOOK_SLOT_COUNT;

    private final Inventory inventory;
    private final int bookSlot;
    private final TemporalIndexItemHandler bookHandler;

    public TemporalIndexMenu(int windowId, Inventory inventory, int bookSlot) {
        super(TemporalIndexRegistry.TEMPORAL_INDEX_MENU.get(), windowId, inventory.player);
        this.inventory = inventory;
        this.bookSlot = bookSlot;
        ItemStack book = validBookSlot(bookSlot) ? inventory.getItem(bookSlot) : ItemStack.EMPTY;
        this.bookHandler = new TemporalIndexItemHandler(book);

        // The shard sits half a slot lower so it is vertically centered beside
        // the two complete rows of eight temporal relics.
        addSlot(new TemporalIndexSlot(bookHandler, TemporalIndexStorage.SHARD_SLOT, 8, 27));
        for (int slot = 1; slot < BOOK_SLOT_COUNT; slot++) {
            int relicIndex = slot - 1;
            int column = relicIndex % RELIC_COLUMNS;
            int row = relicIndex / RELIC_COLUMNS;
            addSlot(new TemporalIndexSlot(bookHandler, slot, 26 + column * 18, 18 + row * 18));
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addPlayerSlot(column + row * 9 + 9, 8 + column * 18, 70 + row * 18);
            }
        }
        for (int column = 0; column < 9; column++) {
            addPlayerSlot(column, 8 + column * 18, 128);
        }
    }

    public static TemporalIndexMenu fromNetwork(int windowId, Inventory inventory, FriendlyByteBuf buffer) {
        return new TemporalIndexMenu(windowId, inventory, buffer.readInt());
    }

    private void addPlayerSlot(int inventoryIndex, int x, int y) {
        addSlot(new Slot(inventory, inventoryIndex, x, y) {
            @Override
            public boolean mayPickup(Player player) {
                return inventoryIndex != bookSlot;
            }

            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return inventoryIndex != bookSlot;
            }
        });
    }

    @Override
    public boolean stillValid(Player player) {
        return validBookSlot(bookSlot)
                && inventory.getItem(bookSlot).is(TemporalIndexRegistry.TEMPORAL_INDEX.get());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot source = slots.get(index);
        if (!source.hasItem() || !source.mayPickup(player)) {
            return ItemStack.EMPTY;
        }

        ItemStack original = source.getItem().copy();
        if (index < BOOK_SLOT_COUNT) {
            int amount = Math.min(original.getCount(), original.getMaxStackSize());
            ItemStack extracted = bookHandler.extractItem(index, amount, false);
            ItemStack moving = extracted.copy();
            if (!moveItemStackTo(moving, PLAYER_SLOT_START, slots.size(), false)) {
                bookHandler.insertItem(index, extracted, false);
                return ItemStack.EMPTY;
            }
            if (!moving.isEmpty()) {
                bookHandler.insertItem(index, moving, false);
            }
            source.setChanged();
            return original;
        }

        ItemStack moving = source.getItem();
        TemporalRelics.sanitizeIdentifiedRelic(moving);
        if (!moveItemStackTo(moving, 0, BOOK_SLOT_COUNT, false)) {
            return ItemStack.EMPTY;
        }
        if (moving.isEmpty()) {
            source.set(ItemStack.EMPTY);
        } else {
            source.setChanged();
        }
        return original;
    }

    private boolean validBookSlot(int slot) {
        return slot >= 0 && slot < inventory.items.size();
    }
}
