package com.hoyin1600p.temporalindex.client;

import com.hoyin1600p.temporalindex.TemporalIndex;
import com.hoyin1600p.temporalindex.client.config.TemporalIndexRenderCalibrationScreen;
import com.hoyin1600p.temporalindex.client.config.TemporalIndexRenderTransformConfig;
import com.hoyin1600p.temporalindex.menu.TemporalIndexScreen;
import com.hoyin1600p.temporalindex.network.CycleSelectionMessage;
import com.hoyin1600p.temporalindex.network.TemporalIndexNetwork;
import com.hoyin1600p.temporalindex.registry.TemporalIndexRegistry;
import com.hoyin1600p.temporalindex.storage.TemporalIndexStorage;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TemporalIndex.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class TemporalIndexClientEvents {
    private static final KeyMapping OPEN_RENDER_CALIBRATION = new KeyMapping(
            "key.temporal_index.open_render_calibration",
            InputConstants.UNKNOWN.getValue(),
            "key.categories.temporal_index"
    );

    private TemporalIndexClientEvents() {
    }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(TemporalIndexRegistry.TEMPORAL_INDEX_MENU.get(), TemporalIndexScreen::new);
            ClientRegistry.registerKeyBinding(OPEN_RENDER_CALIBRATION);
            TemporalIndexRenderTransformConfig.getInstance();
        });
    }

    @Mod.EventBusSubscriber(modid = TemporalIndex.MOD_ID, value = Dist.CLIENT)
    public static final class ForgeEvents {
        private ForgeEvents() {
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.screen == null && OPEN_RENDER_CALIBRATION.consumeClick()) {
                minecraft.setScreen(new TemporalIndexRenderCalibrationScreen());
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onMouseScroll(InputEvent.MouseScrollEvent event) {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            if (player == null || minecraft.screen != null || !player.isShiftKeyDown()) {
                return;
            }

            ItemStack book = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (!book.is(TemporalIndexRegistry.TEMPORAL_INDEX.get()) || event.getScrollDelta() == 0.0D) {
                return;
            }

            int direction = event.getScrollDelta() > 0.0D ? 1 : -1;
            TemporalIndexStorage.cycle(book, direction);
            TemporalIndexNetwork.CHANNEL.sendToServer(new CycleSelectionMessage(direction));
            event.setCanceled(true);
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void onRenderItemInFrame(RenderItemInFrameEvent event) {
            ItemStack book = event.getItemStack();
            if (!book.is(TemporalIndexRegistry.TEMPORAL_INDEX.get())) {
                return;
            }

            PoseStack poseStack = event.getPoseStack();
            TemporalIndexItemRenderer.getInstance().renderInFrame(
                    book,
                    poseStack,
                    event.getMultiBufferSource(),
                    event.getPackedLight()
            );
            event.setCanceled(true);
        }
    }
}
