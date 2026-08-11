package com.hoyin1600p.temporalindex.storage;

import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.item.IdentifiableItem;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.TemporalShardItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class TemporalRelics {
    public static final int DEFAULT_DURATION = 6000;
    public static final String LIGHTWEIGHT_IDENTIFIED_TAG = "identified";

    public static final List<Definition> DEFINITIONS = List.of(
            definition("overpower"),
            definition("champion_domain"),
            definition("loot_goblin"),
            definition("door_hunter"),
            definition("ultimate_regeneration"),
            definition("kill_nuke"),
            definition("kill_charm"),
            definition("kill_hunter"),
            definition("kill_totem"),
            definition("bronze_nuke"),
            definition("glued_mobs"),
            definition("rock_solid"),
            definition("pylon_hunter"),
            definition("soul_fest"),
            definition("daycare"),
            definition("lunar")
    );

    private TemporalRelics() {
    }

    private static Definition definition(String path) {
        return new Definition(new ResourceLocation("the_vault", path));
    }

    public static int findDefinition(ItemStack stack) {
        if (!isTemporalItem(stack) || !TemporalShardItem.isIdentified(stack)) {
            return -1;
        }

        ResourceLocation modifier = TemporalShardItem.getModifier(stack);
        if (modifier == null) {
            return -1;
        }

        for (int index = 0; index < DEFINITIONS.size(); index++) {
            if (DEFINITIONS.get(index).modifier().equals(modifier)) {
                return index;
            }
        }
        return -1;
    }

    public static boolean isUnidentifiedShard(ItemStack stack) {
        if (!isTemporalItem(stack) || TemporalShardItem.isIdentified(stack)) {
            return false;
        }
        return ((IdentifiableItem) stack.getItem()).getState(stack) == VaultGearState.UNIDENTIFIED;
    }

    public static boolean isTemporalItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() == ModItems.TEMPORAL_SHARD;
    }

    public static Component getIndexDisplayName(ItemStack stack) {
        if (isUnidentifiedShard(stack)) {
            return new TranslatableComponent("tooltip.temporal_index.shard_name");
        }

        Component hoverName = stack.getHoverName();
        if (findDefinition(stack) < 0) {
            return hoverName;
        }

        String displayName = hoverName.getString();
        int separator = displayName.indexOf(": ");
        String specificName = separator >= 0 ? displayName.substring(separator + 2) : displayName;
        return new TextComponent(specificName).setStyle(hoverName.getStyle());
    }

    public static ItemStack createShardStack(int count) {
        return count <= 0 ? ItemStack.EMPTY : new ItemStack(ModItems.TEMPORAL_SHARD, count);
    }

    public static ItemStack createRelicStack(Definition definition, int duration, int count) {
        if (count <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(ModItems.TEMPORAL_SHARD, count);
        TemporalShardItem.setModifierData(stack, definition.modifier(), Math.max(0, duration));
        sanitizeIdentifiedRelic(stack);
        return stack;
    }

    public static void sanitizeIdentifiedRelic(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(LIGHTWEIGHT_IDENTIFIED_TAG, true);
        tag.remove("vaultGearData");
        tag.remove("clientCache");
    }

    public record Definition(ResourceLocation modifier) {
    }
}
