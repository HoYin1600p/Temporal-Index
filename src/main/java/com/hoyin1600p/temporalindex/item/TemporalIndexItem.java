package com.hoyin1600p.temporalindex.item;

import com.hoyin1600p.temporalindex.client.TemporalIndexItemRenderer;
import com.hoyin1600p.temporalindex.menu.TemporalIndexMenu;
import com.hoyin1600p.temporalindex.storage.TemporalIndexStorage;
import com.hoyin1600p.temporalindex.storage.TemporalRelics;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.client.IItemRenderProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public final class TemporalIndexItem extends Item {
    public TemporalIndexItem(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return TemporalIndexItemRenderer.getInstance();
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack book = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.pass(book);
        }

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                int slot = player.getInventory().selected;
                NetworkHooks.openGui(
                        serverPlayer,
                        new SimpleMenuProvider(
                                (windowId, inventory, ignored) -> new TemporalIndexMenu(windowId, inventory, slot),
                                new TranslatableComponent("container.temporal_index.temporal_index")
                        ),
                        buffer -> buffer.writeInt(slot)
                );
            }
            return InteractionResultHolder.sidedSuccess(book, level.isClientSide);
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            TemporalIndexUseService.useSelected(serverPlayer, book);
        }
        return InteractionResultHolder.sidedSuccess(book, level.isClientSide);
    }

    @Override
    public Component getName(ItemStack book) {
        MutableComponent name = super.getName(book).copy();
        int selectedSlot = TemporalIndexStorage.getSelectedSlot(book);
        if (selectedSlot > TemporalIndexStorage.SHARD_SLOT) {
            ItemStack selected = TemporalIndexStorage.getDisplayStack(book, selectedSlot);
            name.append(new TextComponent(" - "));
            name.append(TemporalRelics.getIndexDisplayName(selected));
        }
        return name;
    }

    @Override
    public void appendHoverText(ItemStack book, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(book, level, tooltip, flag);
        int total = 0;
        for (int slot = 0; slot < TemporalIndexStorage.SLOT_COUNT; slot++) {
            total += TemporalIndexStorage.getCount(book, slot);
        }

        if (total == 0) {
            tooltip.add(new TranslatableComponent("tooltip.temporal_index.empty").withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(new TranslatableComponent("tooltip.temporal_index.contents", total).withStyle(ChatFormatting.GRAY));
            for (int slot = 0; slot < TemporalIndexStorage.SLOT_COUNT; slot++) {
                int count = TemporalIndexStorage.getCount(book, slot);
                if (count <= 0) {
                    continue;
                }

                ItemStack stored = TemporalIndexStorage.getDisplayStack(book, slot);
                tooltip.add(new TranslatableComponent(
                        "tooltip.temporal_index.content_entry",
                        TemporalRelics.getIndexDisplayName(stored),
                        count
                ).withStyle(ChatFormatting.GRAY));
            }

            int selectedSlot = TemporalIndexStorage.getSelectedSlot(book);
            ItemStack selected = TemporalIndexStorage.getDisplayStack(book, selectedSlot);
            tooltip.add(new TranslatableComponent(
                    "tooltip.temporal_index.selected",
                    TemporalRelics.getIndexDisplayName(selected),
                    TemporalIndexStorage.getCount(book, selectedSlot)
            ).withStyle(ChatFormatting.AQUA));
        }
        tooltip.add(new TranslatableComponent("tooltip.temporal_index.controls").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }
}
