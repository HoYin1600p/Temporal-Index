package com.hoyin1600p.temporalindex.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemporalRelicNbtTest {
    @Test
    void canonicalTagContainsOnlyRelicIdentityAndDuration() {
        ResourceLocation modifier = new ResourceLocation("the_vault", "kill_nuke");

        CompoundTag canonical = TemporalRelicNbt.createCanonicalTag(modifier, 6000);

        assertEquals(Set.of("identified", "modifier", "duration"), canonical.getAllKeys());
        assertTrue(canonical.getBoolean("identified"));
        assertEquals("the_vault:kill_nuke", canonical.getString("modifier"));
        assertEquals(6000, canonical.getInt("duration"));
    }

    @Test
    void canonicalTagClampsNegativeDuration() {
        CompoundTag canonical = TemporalRelicNbt.createCanonicalTag(
                new ResourceLocation("the_vault", "lunar"),
                -1
        );

        assertEquals(0, canonical.getInt("duration"));
    }
}
