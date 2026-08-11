# Client/server safety

Temporal Index must be installed on both the client and server. The mod is not client-only, and its `the_vault` dependency is mandatory on both sides.

Wold's Vaults remains optional on both sides. When it is installed, the 1.0.1 temporal-state compatibility injection composes with Wold's own common mixin without importing a Wold's class or moving any authority to the client. The dedicated-server lifecycle test below remains a Vault-only test; it demonstrates Temporal Index's own distribution safety rather than making a claim about the server-side behavior of an optional Wold's build.

## Side separation

Client-only code is limited to the `com.hoyin1600p.temporalindex.client` package, the container screen, and the client GUI mixin.

- Client event subscribers are annotated with `Dist.CLIENT`.
- The custom screen is registered only during `FMLClientSetupEvent`.
- Mouse-wheel input and item-frame rendering subscribe only on the physical client.
- `AbstractContainerScreenMixin` is listed under the mixin configuration's `client` section, so it is not prepared or applied by a dedicated server.
- The custom item renderer is returned only through Forge's client initialization hook.

The common mod constructor registers only items, menus, and the network channel. It does not reference Minecraft's client singleton or initialize render classes.

## Server authority

The logical server owns every mutation that can affect gameplay or saved data:

- menu opening and the active-book inventory slot;
- accepted item type, modifier, duration, and capacity;
- shift-click transfers and extraction;
- selected-slot cycling;
- ground pickup and direct inventory insertion;
- temporal shard consumption, Vault-owned loot generation, and result routing;
- temporal relic activation and stored-count decrement.

The client predicts scroll selection for immediate rendering, then sends only a direction (`-1` or `1`). The server re-reads the actual main-hand Index and derives the selected occupied slot from authoritative book data. The packet is registered as `PLAY_TO_SERVER` only.

## Dedicated-server verification

The release JAR was tested through Forge's dedicated game-test server launch target with Minecraft 1.18.2, Java 17, Vault Hunters 3.20.3.6055, and Forge 40.3.12. Wold's Vaults was not present.

Observed server lifecycle milestones:

1. Forge recognized `temporal_index-1.0.0` as a valid JavaFML mod.
2. Runtime distribution cleaning accepted the JAR in `SERVER` mode.
3. The common player-inventory and Vault temporal-item mixins prepared and applied.
4. Only `TemporalIndexPickupEvents` auto-subscribed; the client event subscriber was excluded.
5. Forge dispatched common setup successfully.
6. Forge generated the `mod:temporal_index` server data-pack entry.
7. The server reached game-test server creation and stopped only because the borrowed host project contains no game-test batches.

The log contained no Temporal Index `LoadingFailedException`, invalid-distribution error, client-class `ClassNotFoundException`, or client GUI mixin preparation. A separate Forge 40.3.11 launch also passed discovery and server distribution cleaning before the host Vault configuration fixture stopped its own setup.

## Client verification

The same release JAR was relaunched in CMA Remastered on Forge 40.3.11, in survival mode inside a Vault. Sneak-scroll changed the server-authoritative `SelectedSlot` value from relic slot `1` to shard slot `0` and back to relic slot `1`, while the selected hotbar slot remained on the Index. This verifies the client event, scroll cancellation, `PLAY_TO_SERVER` packet, and integrated-server handler together after the network hardening change.

## Multiplayer version matching

Temporal Index uses Forge's normal network protocol acceptance and does not opt out of client/server version checks. Clients and servers should use the same Temporal Index version. A server without Temporal Index is not a supported target for a client carrying or rendering Index data.
