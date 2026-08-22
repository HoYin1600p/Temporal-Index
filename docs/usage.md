# Usage and behavior

## Inventory layout

The Temporal Index contains 17 reserved slots:

- one temporal shard slot, vertically centered on the left;
- two rows of eight temporal relic slots.

Each slot accepts only its designated temporal item and stores at most 65,000. Empty relic slots are not selectable. Relics with the same modifier but different Vault-generated durations are kept distinct by validation and are not silently merged.

## Main-hand controls

The Index can collect items from anywhere in the player's hotbar or main inventory, but it can only be opened, cycled, or activated while held in the main hand.

| Input | Result |
| --- | --- |
| Sneak + right-click | Open the Index inventory |
| Sneak + scroll up/down | Select the next or previous occupied slot |
| Right-click | Use the selected shard or relic |

Selection follows the fixed slot order, skips empty slots, and wraps from the end to the beginning. If the last item in the selected stack is consumed, selection advances upward to the next occupied entry. An empty Index has no selection and displays no cover sprite.

## Temporal shard use

Using a selected shard asks Vault Hunters to roll the configured temporal loot pool and instantly identify the result. Temporal Index never owns, replaces, retries, or reverses the loot roll.

The identified relic is routed in this order:

1. its matching slot in the active Index;
2. normal player-inventory insertion, allowing another Index to accept it;
3. a normal item drop at the player's feet when no storage accepts it.

The shard is consumed only after Vault Hunters has produced a valid relic. When the shard stack becomes empty, selection advances to the next occupied entry.

## Temporal relic use

Using a selected identified relic delegates to Vault Hunters. Vault Hunters remains responsible for validating the current dimension and Vault state, applying the modifier and duration, sending feedback, and consuming the item.

Creative mode normally prevents item consumption. Use survival mode inside an active Vault when validating relic activation.

## Automatic pickup

Supported shards and relics are collected before backpack pickup upgrades receive them. When more than one Index exists, books are checked in vanilla player-inventory order:

1. hotbar slots 1–9;
2. main inventory slots 1–27.

The first eligible Index with capacity receives the item. If no Index can accept it, normal pickup continues.

## Quantities and tooltips

Index slots display their normal count until the stored amount reaches 99. At 99 or above, the compact label becomes `99+`. Hovering that slot displays the exact stored quantity.

Hovering the Index itself lists every occupied entry and exact quantity. Names use Vault Hunters' localized display names with redundant `Temporal Relic` and unidentified wording removed. When a relic is selected, the title uses the compact form `Temporal Index - <relic name>`.

## Data safety

Temporal Index stores compact modifier, duration, count, and selection data on the book. When a valid identified relic is offered to the Index, it is reduced to a canonical representation containing only its identification marker, modifier, and duration. This happens before manual and shift-click merge comparisons as well as through automatic pickup and shard-generated routing. Relics reconstructed or removed from the Index retain that same clean representation.

Positive Vault-provided durations are preserved. A missing or invalid duration uses the 6,000-tick fallback. Other NBT—including generation-source markers, Vault Gear data, client caches, custom names, and unrelated third-party tags—is intentionally discarded.

The container validates slot contents and menu transfers on the server. The active Index cannot be moved while its own menu is open. These rules are intended to prevent shift-click and carried-stack duplication paths.
