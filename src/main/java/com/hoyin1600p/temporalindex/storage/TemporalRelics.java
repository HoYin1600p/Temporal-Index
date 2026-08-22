package com.hoyin1600p.temporalindex.storage;

import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.item.IdentifiableItem;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.TemporalShardItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class TemporalRelics {
    public static final int DEFAULT_DURATION = 6000;
    public static final String LIGHTWEIGHT_IDENTIFIED_TAG = TemporalRelicNbt.IDENTIFIED_TAG;

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

        return findDefinition(modifier);
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

        if (findDefinition(stack) >= 0) {
            var modifier = TemporalShardItem.getVaultModifier(stack);
            if (modifier != null) {
                return new TextComponent(modifier.getDisplayName())
                        .setStyle(Style.EMPTY.withColor(modifier.getDisplayTextColor()));
            }
        }

        Component hoverName = stack.getHoverName();
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
        stack.setTag(TemporalRelicNbt.createCanonicalTag(
                definition.modifier(),
                normalizeDuration(duration)
        ));
        return stack;
    }

    public static boolean sanitizeIdentifiedRelic(ItemStack stack) {
        if (!isTemporalItem(stack) || !TemporalShardItem.isIdentified(stack)) {
            return false;
        }

        ResourceLocation modifier = TemporalShardItem.getModifier(stack);
        if (findDefinition(modifier) < 0) {
            return false;
        }

        stack.setTag(TemporalRelicNbt.createCanonicalTag(
                modifier,
                normalizeDuration(TemporalShardItem.getDuration(stack))
        ));
        return true;
    }

    public static ItemStack sanitizedRelicCopy(ItemStack stack) {
        ItemStack copy = stack.copy();
        return sanitizeIdentifiedRelic(copy) ? copy : ItemStack.EMPTY;
    }

    private static int findDefinition(ResourceLocation modifier) {
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

    private static int normalizeDuration(int duration) {
        return duration > 0 ? duration : DEFAULT_DURATION;
    }

    public record Definition(ResourceLocation modifier) {
    }
}
