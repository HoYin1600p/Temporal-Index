package com.hoyin1600p.temporalindex.menu;

import com.hoyin1600p.temporalindex.storage.TemporalRelics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class TemporalIndexScreen extends AbstractContainerScreen<TemporalIndexMenu> {
    public TemporalIndexScreen(TemporalIndexMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 152;
        inventoryLabelY = 58;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        int left = leftPos;
        int top = topPos;
        fill(poseStack, left, top, left + imageWidth, top + imageHeight, 0xFF1A2026);
        fill(poseStack, left + 3, top + 3, left + imageWidth - 3, top + 56, 0xFF26343B);
        fill(poseStack, left + 3, top + 64, left + imageWidth - 3, top + imageHeight - 3, 0xFF20272D);

        drawSlot(poseStack, left + 7, top + 26, 0xFF31464E);
        for (int slot = 1; slot < TemporalIndexMenu.BOOK_SLOT_COUNT; slot++) {
            int relicIndex = slot - 1;
            int x = left + 25 + (relicIndex % TemporalIndexMenu.RELIC_COLUMNS) * 18;
            int y = top + 17 + (relicIndex / TemporalIndexMenu.RELIC_COLUMNS) * 18;
            drawSlot(poseStack, x, y, 0xFF31464E);
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlot(poseStack, left + 7 + column * 18, top + 69 + row * 18, 0xFF2C343A);
            }
        }
        for (int column = 0; column < 9; column++) {
            drawSlot(poseStack, left + 7 + column * 18, top + 127, 0xFF2C343A);
        }
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        float centeredTitleX = (imageWidth - font.width(title)) / 2.0F;
        font.draw(poseStack, title, centeredTitleX, titleLabelY, 0xFFD7B85A);
        font.draw(poseStack, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 0xFFB9C4CA);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int mouseX, int mouseY) {
        if (!menu.getCarried().isEmpty() || hoveredSlot == null || !hoveredSlot.hasItem()) {
            return;
        }

        ItemStack hovered = hoveredSlot.getItem();
        if (!(hoveredSlot instanceof TemporalIndexSlot)) {
            renderTooltip(poseStack, hovered, mouseX, mouseY);
            return;
        }

        List<Component> lines = new ArrayList<>(getTooltipFromItem(hovered));
        if (!lines.isEmpty() && TemporalRelics.findDefinition(hovered) >= 0) {
            lines.set(0, TemporalRelics.getIndexDisplayName(hovered));
        }
        if (hovered.getCount() >= TemporalIndexSlot.COMPACT_COUNT_THRESHOLD) {
            lines.add(new TranslatableComponent(
                    "tooltip.temporal_index.exact_quantity",
                    hovered.getCount()
            ).withStyle(ChatFormatting.GOLD));
        }
        renderTooltip(poseStack, lines, hovered.getTooltipImage(), mouseX, mouseY, hovered);
    }

    private static void drawSlot(PoseStack poseStack, int x, int y, int interiorColor) {
        fill(poseStack, x, y, x + 18, y + 18, 0xFF0D1114);
        fill(poseStack, x + 1, y + 1, x + 17, y + 17, interiorColor);
    }
}
