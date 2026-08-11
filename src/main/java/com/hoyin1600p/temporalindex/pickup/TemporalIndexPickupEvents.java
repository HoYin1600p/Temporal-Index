package com.hoyin1600p.temporalindex.pickup;

import com.hoyin1600p.temporalindex.TemporalIndex;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TemporalIndex.MOD_ID)
public final class TemporalIndexPickupEvents {
    private TemporalIndexPickupEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void beforeBackpackPickup(EntityItemPickupEvent event) {
        Player player = event.getPlayer();
        if (player.level.isClientSide) {
            return;
        }

        ItemEntity entity = event.getItem();
        ItemStack stack = entity.getItem();
        int before = stack.getCount();
        TemporalIndexPickup.tryInsert(player.getInventory(), stack);
        int inserted = before - stack.getCount();
        if (inserted <= 0) {
            return;
        }

        player.take(entity, inserted);
        if (stack.isEmpty()) {
            entity.discard();
            event.setCanceled(true);
        }
    }
}
