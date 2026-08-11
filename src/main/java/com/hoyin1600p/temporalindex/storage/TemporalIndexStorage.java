package com.hoyin1600p.temporalindex.storage;

import iskallia.vault.item.gear.TemporalShardItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public final class TemporalIndexStorage {
    public static final int MAX_PER_SLOT = 65_000;
    public static final int SHARD_SLOT = 0;
    public static final int SLOT_COUNT = 1 + TemporalRelics.DEFINITIONS.size();

    private static final String ROOT_TAG = "TemporalIndexData";
    private static final String SHARD_COUNT_TAG = "ShardCount";
    private static final String RELICS_TAG = "Relics";
    private static final String COUNT_TAG = "Count";
    private static final String DURATION_TAG = "Duration";
    private static final String SELECTED_SLOT_TAG = "SelectedSlot";

    private TemporalIndexStorage() {
    }

    public static int getCount(ItemStack book, int slot) {
        validateSlot(slot);
        CompoundTag data = getData(book);
        if (slot == SHARD_SLOT) {
            return clampCount(data.getInt(SHARD_COUNT_TAG));
        }

        CompoundTag relic = getRelics(data).getCompound(relicKey(slot));
        return clampCount(relic.getInt(COUNT_TAG));
    }

    public static int getDuration(ItemStack book, int slot) {
        validateRelicSlot(slot);
        CompoundTag relic = getRelics(getData(book)).getCompound(relicKey(slot));
        int duration = relic.getInt(DURATION_TAG);
        return duration > 0 ? duration : TemporalRelics.DEFAULT_DURATION;
    }

    public static void setCount(ItemStack book, int slot, int count, int duration) {
        validateSlot(slot);
        int clamped = clampCount(count);
        CompoundTag data = getData(book);

        if (slot == SHARD_SLOT) {
            data.putInt(SHARD_COUNT_TAG, clamped);
        } else {
            CompoundTag relics = getRelics(data);
            String key = relicKey(slot);
            if (clamped == 0) {
                relics.remove(key);
            } else {
                CompoundTag relic = new CompoundTag();
                relic.putInt(COUNT_TAG, clamped);
                relic.putInt(DURATION_TAG, Math.max(0, duration));
                relics.put(key, relic);
            }
        }

        int selected = data.contains(SELECTED_SLOT_TAG) ? data.getInt(SELECTED_SLOT_TAG) : -1;
        if (clamped > 0 && !isOccupied(book, selected)) {
            data.putInt(SELECTED_SLOT_TAG, slot);
        } else if (clamped == 0 && selected == slot) {
            cycleFrom(book, slot, 1);
        }
    }

    public static boolean hasAny(ItemStack book) {
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            if (getCount(book, slot) > 0) {
                return true;
            }
        }
        return false;
    }

    public static int getSelectedSlot(ItemStack book) {
        CompoundTag data = getData(book);
        int selected = data.contains(SELECTED_SLOT_TAG) ? data.getInt(SELECTED_SLOT_TAG) : -1;
        if (isOccupied(book, selected)) {
            return selected;
        }

        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            if (getCount(book, slot) > 0) {
                data.putInt(SELECTED_SLOT_TAG, slot);
                return slot;
            }
        }

        data.remove(SELECTED_SLOT_TAG);
        return -1;
    }

    public static boolean cycle(ItemStack book, int direction) {
        if (direction == 0) {
            return false;
        }
        int selected = getSelectedSlot(book);
        if (selected < 0) {
            return false;
        }
        return cycleFrom(book, selected, direction > 0 ? 1 : -1);
    }

    public static ItemStack getDisplayStack(ItemStack book, int slot) {
        int count = getCount(book, slot);
        if (count <= 0) {
            return ItemStack.EMPTY;
        }
        if (slot == SHARD_SLOT) {
            return TemporalRelics.createShardStack(count);
        }
        return TemporalRelics.createRelicStack(
                TemporalRelics.DEFINITIONS.get(slot - 1),
                getDuration(book, slot),
                count
        );
    }

    public static ItemStack getSelectedDisplayStack(ItemStack book) {
        int selected = getSelectedSlot(book);
        return selected < 0 ? ItemStack.EMPTY : getDisplayStack(book, selected);
    }

    public static int slotFor(ItemStack stack) {
        if (TemporalRelics.isUnidentifiedShard(stack)) {
            return SHARD_SLOT;
        }
        int definition = TemporalRelics.findDefinition(stack);
        return definition < 0 ? -1 : definition + 1;
    }

    public static int durationFor(ItemStack stack) {
        int duration = TemporalShardItem.getDuration(stack);
        return duration > 0 ? duration : TemporalRelics.DEFAULT_DURATION;
    }

    private static boolean cycleFrom(ItemStack book, int start, int direction) {
        CompoundTag data = getData(book);
        for (int step = 1; step <= SLOT_COUNT; step++) {
            int candidate = Math.floorMod(start + direction * step, SLOT_COUNT);
            if (getCount(book, candidate) > 0) {
                data.putInt(SELECTED_SLOT_TAG, candidate);
                return candidate != start;
            }
        }
        data.remove(SELECTED_SLOT_TAG);
        return false;
    }

    private static boolean isOccupied(ItemStack book, int slot) {
        return slot >= 0 && slot < SLOT_COUNT && getCount(book, slot) > 0;
    }

    private static CompoundTag getData(ItemStack book) {
        return book.getOrCreateTagElement(ROOT_TAG);
    }

    private static CompoundTag getRelics(CompoundTag data) {
        if (!data.contains(RELICS_TAG, CompoundTag.TAG_COMPOUND)) {
            data.put(RELICS_TAG, new CompoundTag());
        }
        return data.getCompound(RELICS_TAG);
    }

    private static String relicKey(int slot) {
        validateRelicSlot(slot);
        return TemporalRelics.DEFINITIONS.get(slot - 1).modifier().toString();
    }

    private static int clampCount(int count) {
        return Math.max(0, Math.min(MAX_PER_SLOT, count));
    }

    private static void validateSlot(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) {
            throw new IndexOutOfBoundsException("Temporal Index slot " + slot + " is outside 0.." + (SLOT_COUNT - 1));
        }
    }

    private static void validateRelicSlot(int slot) {
        if (slot <= SHARD_SLOT || slot >= SLOT_COUNT) {
            throw new IndexOutOfBoundsException("Temporal Index relic slot " + slot + " is invalid");
        }
    }
}
