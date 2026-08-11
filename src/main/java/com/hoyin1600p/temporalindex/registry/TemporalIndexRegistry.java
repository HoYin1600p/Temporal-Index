package com.hoyin1600p.temporalindex.registry;

import com.hoyin1600p.temporalindex.TemporalIndex;
import com.hoyin1600p.temporalindex.item.TemporalIndexItem;
import com.hoyin1600p.temporalindex.menu.TemporalIndexMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class TemporalIndexRegistry {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TemporalIndex.MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, TemporalIndex.MOD_ID);

    public static final RegistryObject<Item> TEMPORAL_INDEX = ITEMS.register(
            "temporal_index",
            () -> new TemporalIndexItem(new Item.Properties()
                    .stacksTo(1)
                    .tab(iskallia.vault.init.ModItems.VAULT_MOD_GROUP))
    );

    public static final RegistryObject<MenuType<TemporalIndexMenu>> TEMPORAL_INDEX_MENU = MENUS.register(
            "temporal_index",
            () -> IForgeMenuType.create(TemporalIndexMenu::fromNetwork)
    );

    private TemporalIndexRegistry() {
    }
}
