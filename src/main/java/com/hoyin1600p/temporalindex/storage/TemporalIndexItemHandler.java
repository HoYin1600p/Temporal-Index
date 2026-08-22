package com.hoyin1600p.temporalindex.storage;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

public final class TemporalIndexItemHandler implements IItemHandlerModifiable {
    private final ItemStack book;

    public TemporalIndexItemHandler(ItemStack book) {
        this.book = book;
    }

    @Override
    public int getSlots() {
        return TemporalIndexStorage.SLOT_COUNT;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return TemporalIndexStorage.getDisplayStack(book, slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || !isItemValid(slot, stack)) {
            return stack;
        }

        ItemStack insertionStack = stack;
        if (slot != TemporalIndexStorage.SHARD_SLOT) {
            insertionStack = TemporalRelics.sanitizedRelicCopy(stack);
            if (insertionStack.isEmpty()) {
                return stack;
            }
        }

        int stored = TemporalIndexStorage.getCount(book, slot);
        if (slot != TemporalIndexStorage.SHARD_SLOT && stored > 0
                && TemporalIndexStorage.getDuration(book, slot) != TemporalIndexStorage.durationFor(insertionStack)) {
            return insertionStack;
        }

        int accepted = Math.min(TemporalIndexStorage.MAX_PER_SLOT - stored, insertionStack.getCount());
        if (accepted <= 0) {
            return insertionStack;
        }

        if (!simulate) {
            TemporalIndexStorage.setCount(
                    book,
                    slot,
                    stored + accepted,
                    slot == TemporalIndexStorage.SHARD_SLOT ? 0 : TemporalIndexStorage.durationFor(insertionStack)
            );
        }

        if (accepted == insertionStack.getCount()) {
            return ItemStack.EMPTY;
        }
        ItemStack remainder = insertionStack.copy();
        remainder.shrink(accepted);
        return remainder;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        int stored = TemporalIndexStorage.getCount(book, slot);
        if (stored <= 0) {
            return ItemStack.EMPTY;
        }

        int normalStackLimit = slot == TemporalIndexStorage.SHARD_SLOT ? 1 : 64;
        int extracted = Math.min(stored, Math.min(amount, normalStackLimit));
        ItemStack result = TemporalIndexStorage.getDisplayStack(book, slot);
        result.setCount(extracted);

        if (!simulate) {
            TemporalIndexStorage.setCount(
                    book,
                    slot,
                    stored - extracted,
                    slot == TemporalIndexStorage.SHARD_SLOT ? 0 : TemporalIndexStorage.getDuration(book, slot)
            );
        }
        return result;
    }

    @Override
    public int getSlotLimit(int slot) {
        return TemporalIndexStorage.MAX_PER_SLOT;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return TemporalIndexStorage.slotFor(stack) == slot;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            int duration = slot == TemporalIndexStorage.SHARD_SLOT ? 0 : TemporalIndexStorage.getDuration(book, slot);
            TemporalIndexStorage.setCount(book, slot, 0, duration);
            return;
        }
        if (!isItemValid(slot, stack)) {
            throw new IllegalArgumentException("Item does not belong in Temporal Index slot " + slot);
        }
        TemporalIndexStorage.setCount(
                book,
                slot,
                stack.getCount(),
                slot == TemporalIndexStorage.SHARD_SLOT ? 0 : TemporalIndexStorage.durationFor(stack)
        );
    }
}
