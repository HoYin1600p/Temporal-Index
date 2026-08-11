package com.hoyin1600p.temporalindex.mixin;

import com.hoyin1600p.temporalindex.storage.TemporalRelics;
import iskallia.vault.item.gear.TemporalShardItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TemporalShardItem.class, remap = false)
public abstract class TemporalShardItemMixin {
    @Inject(method = "isIdentified", at = @At("HEAD"), cancellable = true, remap = false)
    private static void temporalIndex$readLightweightState(
            ItemStack stack,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (!stack.isEmpty() && stack.getItem() instanceof TemporalShardItem
                && stack.hasTag()
                && stack.getTag().getBoolean(TemporalRelics.LIGHTWEIGHT_IDENTIFIED_TAG)) {
            callback.setReturnValue(true);
        }
    }
}
