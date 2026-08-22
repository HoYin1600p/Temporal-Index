package com.hoyin1600p.temporalindex.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public final class TemporalRelicNbt {
    public static final String IDENTIFIED_TAG = "identified";
    public static final String MODIFIER_TAG = "modifier";
    public static final String DURATION_TAG = "duration";

    private TemporalRelicNbt() {
    }

    public static CompoundTag createCanonicalTag(ResourceLocation modifier, int duration) {
        CompoundTag canonical = new CompoundTag();
        canonical.putBoolean(IDENTIFIED_TAG, true);
        canonical.putString(MODIFIER_TAG, modifier.toString());
        canonical.putInt(DURATION_TAG, Math.max(0, duration));
        return canonical;
    }
}
