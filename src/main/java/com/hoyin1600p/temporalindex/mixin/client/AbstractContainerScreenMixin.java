package com.hoyin1600p.temporalindex.mixin.client;

import com.hoyin1600p.temporalindex.menu.TemporalIndexScreen;
import com.hoyin1600p.temporalindex.menu.TemporalIndexSlot;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @Redirect(
            method = "renderSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"
            )
    )
    private void temporalIndex$renderCompactCount(
            ItemRenderer renderer,
            Font font,
            ItemStack stack,
            int x,
            int y,
            @Nullable String countLabel,
            PoseStack poseStack,
            Slot slot
    ) {
        String displayedCount = countLabel;
        if ((Object) this instanceof TemporalIndexScreen
                && slot instanceof TemporalIndexSlot
                && stack.getCount() >= TemporalIndexSlot.COMPACT_COUNT_THRESHOLD) {
            displayedCount = "99+";
        }
        renderer.renderGuiItemDecorations(font, stack, x, y, displayedCount);
    }
}
