package com.hoyin1600p.temporalindex.mixin;

import com.hoyin1600p.temporalindex.storage.TemporalRelics;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.item.gear.TemporalShardItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TemporalShardItem.class, priority = 800, remap = false)
public abstract class TemporalShardItemMixin {
    @Inject(method = "getState", at = @At("HEAD"), cancellable = true, remap = false)
    private void temporalIndex$readLightweightGearState(
            ItemStack stack,
            CallbackInfoReturnable<VaultGearState> callback
    ) {
        if (isLightweightIdentified(stack)) {
            callback.setReturnValue(VaultGearState.IDENTIFIED);
        }
    }

    @Inject(method = "isIdentified", at = @At("HEAD"), cancellable = true, remap = false)
    private static void temporalIndex$readLightweightState(
            ItemStack stack,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (isLightweightIdentified(stack)) {
            callback.setReturnValue(true);
        }
    }

    private static boolean isLightweightIdentified(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() instanceof TemporalShardItem
                && stack.hasTag()
                && stack.getTag().getBoolean(TemporalRelics.LIGHTWEIGHT_IDENTIFIED_TAG);
    }
}
