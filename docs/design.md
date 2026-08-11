# Temporal Index design

## Storage

- The item is a portable book with one reserved slot for temporal shards and one reserved slot for each supported temporal modifier.
- Every slot has a hard cap of 65,000.
- Slots reject all items except their exact designated shard or relic form.
- Relics with the same modifier but different durations are not merged.
- Counts, selection, modifier IDs, and durations persist on the book item.
- The active book cannot be moved while its menu is open.

## Selection and rendering

- Only occupied slots participate in selection.
- Scrolling wraps in both directions.
- Scroll packets contain only a direction; the server derives the selected slot from authoritative book data.
- Removing or consuming the final selected item advances upward to the next occupied slot.
- An empty book renders closed. A book containing shards or relics renders open.
- The selected item's Vault-owned sprite is rendered over the open book.

## Interaction

- The book must be in the main hand for scrolling, opening, or activation.
- Sneak + right-click opens the menu.
- Right-click on an identified relic delegates activation to the Vault item itself. Vault therefore owns dimension, objective, modifier, duration, sound, and chat behavior.
- Right-click on a shard asks Vault's `IdentifiableItem` implementation to perform an instant identification and loot-pool roll.

## Generated relic routing

1. Insert into the matching slot in the active book.
2. If full, pass the identified relic through normal player-inventory insertion so another book can accept it.
3. If no book or inventory slot accepts it, drop it at the player's feet.
4. Once Vault has produced a valid relic, consume the shard and never reroll or delete the result.

## Pickup order

Book lookup follows the player's `Inventory.items` order:

1. Hotbar slots 1–9.
2. Main inventory slots 1–27.

Ground pickup is handled at highest Forge event priority before backpack pickup upgrades. Direct inventory insertion is also intercepted at the same method used by Vault's built-in pouches.

## NBT policy

- Stored relics retain modifier ID, duration, and count only.
- Reconstructed relics use a lightweight `identified` marker and omit `vaultGearData` and `clientCache`.
- Vault remains responsible for rolling modifier IDs and durations.
