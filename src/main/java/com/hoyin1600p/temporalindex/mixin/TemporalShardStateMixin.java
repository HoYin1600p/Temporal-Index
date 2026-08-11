package com.hoyin1600p.temporalindex.mixin;

import com.hoyin1600p.temporalindex.storage.TemporalRelics;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.IdentifiableItem;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.gear.TemporalShardItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = TemporalShardItem.class, priority = 800, remap = false)
public abstract class TemporalShardStateMixin implements IdentifiableItem {
    @Override
    public VaultGearState getState(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof TemporalShardItem
                && stack.hasTag()
                && stack.getTag().getBoolean(TemporalRelics.LIGHTWEIGHT_IDENTIFIED_TAG)) {
            return VaultGearState.IDENTIFIED;
        }
        AttributeGearData gearData = AttributeGearData.read(stack);
        if (gearData instanceof VaultGearData vaultGearData) {
            return vaultGearData.getState();
        }
        return gearData.getFirstValue(ModGearAttributes.STATE).orElse(VaultGearState.UNIDENTIFIED);
    }
}
