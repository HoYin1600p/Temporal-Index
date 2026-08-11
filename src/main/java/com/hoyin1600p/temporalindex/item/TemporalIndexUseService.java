package com.hoyin1600p.temporalindex.item;

import com.hoyin1600p.temporalindex.TemporalIndex;
import com.hoyin1600p.temporalindex.storage.TemporalIndexItemHandler;
import com.hoyin1600p.temporalindex.storage.TemporalIndexStorage;
import com.hoyin1600p.temporalindex.storage.TemporalRelics;
import iskallia.vault.gear.item.IdentifiableItem;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.TemporalShardItem;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public final class TemporalIndexUseService {
    private TemporalIndexUseService() {
    }

    public static boolean useSelected(ServerPlayer player, ItemStack book) {
        int selected = TemporalIndexStorage.getSelectedSlot(book);
        if (selected < 0) {
            return false;
        }
        return selected == TemporalIndexStorage.SHARD_SLOT
                ? revealShard(player, book)
                : activateRelic(player, book, selected);
    }

    private static boolean revealShard(ServerPlayer player, ItemStack book) {
        if (TemporalIndexStorage.getCount(book, TemporalIndexStorage.SHARD_SLOT) <= 0) {
            return false;
        }

        ItemStack generated = new ItemStack(ModItems.TEMPORAL_SHARD);
        try {
            ((IdentifiableItem) generated.getItem()).instantIdentify(player, generated);
        } catch (RuntimeException exception) {
            TemporalIndex.LOGGER.error("Temporal relic identification failed", exception);
        }
        int relicSlot = TemporalIndexStorage.slotFor(generated);
        if (relicSlot <= TemporalIndexStorage.SHARD_SLOT) {
            player.displayClientMessage(
                    new TranslatableComponent("message.temporal_index.identification_failed"),
                    true
            );
            return false;
        }

        TemporalRelics.sanitizeIdentifiedRelic(generated);
        TemporalIndexItemHandler activeBook = new TemporalIndexItemHandler(book);
        ItemStack unplaced = generated.copy();
        try {
            unplaced = activeBook.insertItem(relicSlot, unplaced, false);
            if (!unplaced.isEmpty()) {
                player.getInventory().add(unplaced);
            }
        } catch (RuntimeException exception) {
            TemporalIndex.LOGGER.error("Generated temporal relic could not be routed", exception);
        } finally {
            if (!unplaced.isEmpty()) {
                player.drop(unplaced, false);
                player.displayClientMessage(
                        new TranslatableComponent("message.temporal_index.relic_preserved"),
                        true
                );
            }
            activeBook.extractItem(TemporalIndexStorage.SHARD_SLOT, 1, false);
        }
        return true;
    }

    private static boolean activateRelic(ServerPlayer player, ItemStack book, int selectedSlot) {
        if (TemporalIndexStorage.getCount(book, selectedSlot) <= 0) {
            return false;
        }

        ItemStack relic = TemporalIndexStorage.getDisplayStack(book, selectedSlot);
        relic.setCount(1);
        ItemStack originalMainHand = player.getMainHandItem();
        boolean consumed;

        player.setItemInHand(InteractionHand.MAIN_HAND, relic);
        try {
            ((TemporalShardItem) relic.getItem()).use(player.level, player, InteractionHand.MAIN_HAND);
        } finally {
            ItemStack result = player.getMainHandItem();
            consumed = result.isEmpty() || result.getCount() < 1;
            player.setItemInHand(InteractionHand.MAIN_HAND, originalMainHand);
            if (consumed) {
                new TemporalIndexItemHandler(book).extractItem(selectedSlot, 1, false);
            }
        }
        return consumed;
    }
}
