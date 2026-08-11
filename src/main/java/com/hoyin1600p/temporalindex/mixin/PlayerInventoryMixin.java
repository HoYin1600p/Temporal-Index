package com.hoyin1600p.temporalindex.mixin;

import com.hoyin1600p.temporalindex.pickup.TemporalIndexPickup;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Inventory.class, priority = 850)
public abstract class PlayerInventoryMixin {
    @Inject(
            method = "add(Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void temporalIndex$insertBeforeInventory(ItemStack stack, CallbackInfoReturnable<Boolean> callback) {
        if (TemporalIndexPickup.tryInsert((Inventory) (Object) this, stack)) {
            callback.setReturnValue(true);
        }
    }
}
