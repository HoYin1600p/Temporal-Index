# Vault compatibility verification

Temporal Index has only three runtime dependencies: Minecraft, Forge, and `the_vault`. Wold's Vaults and the other inspected reference mods are not required.

The mod must be installed on both the client and server. Client code owns its custom screen and item rendering; server-authoritative code owns storage, transfer, selection, pickup, shard routing, and activation.

The `the_vault` dependency is intentionally constrained by mod ID rather than a pack-specific release number. Vault reports versions such as `1.18.2-20.0.3-remastered.6909`, which are not meaningfully ordered against upstream-style `3.21.x` labels by Forge's Maven version-range rules. Compatibility is instead verified against the concrete jars below.

## CMA matrix

For the 1.0.0 release on 2026-08-10, the source compiled successfully against every distinct active `the_vault` jar in the five CMA-labelled Prism instances:

| CMA instance | Vault jar | SHA-256 prefix | Result |
| --- | --- | --- | --- |
| CMA Wolds | 3.21.5.6573 | `58672F06C4B3564` | Pass |
| CMA Third / CMA Wolds 0.33.0 | 3.21.6.6884 | `E4B1E896558D6940` | Pass |
| CMA Asgard | 3.21.62 | `DBB00F7E0FCA832F` | Pass |
| CMA Remastered | 20.0.3-remastered.6909 | `53E56FE94AA546F8` | Pass |

The installed `temporal_shard.json` configurations were also inspected. All five use the same ordered set of 16 modifier IDs expected by the book. Durations differ between modifiers in the two CMA Wolds configurations; the book retains the Vault-generated duration with each stored modifier instead of replacing it with a hard-coded duration.

## Optional Wold's Vaults compatibility

The 1.0.1 release was checked against every Wold's Vaults generation currently represented by the CMA instances:

| CMA instance | Wold's Vaults build | Vault build | Verification |
| --- | --- | --- | --- |
| CMA Wolds 0.33.0 | 0.33.1 | 3.21.6.6884 | Full client test: both mods loaded, the Index opened in survival, and using a shard changed its count from 5 to 4 while Wold's generated and identified a Daycare relic that routed into reserved slot 15. |
| CMA Wolds 0.32.2 | 0.32.3 | 3.21.5.6573 | Client startup test: both mods loaded and both temporal-shard mixins applied without an overwrite conflict or mixin application error. |
| CMA Asgard | 0.23.4 standalone | 3.21.62 | API compilation passed. This older Wold's build does not patch `TemporalShardItem`, so it has no temporal-method collision with the Index. |

Modern Wold's Vaults supplies its own temporal-shard state, identification, and loot behavior. Temporal Index handles its compact `identified` marker through a cancellable injection with lower mixin priority, allowing Wold's implementation to remain authoritative for every other stack. No Wold's class is imported, referenced, or required at runtime.

The 0.33.1 functional test also confirms that Temporal Index does not replace, predict, or undo the Wold's/Vault temporal loot roll. It accepts the generated relic only after the owning mod has created and identified it.

## Dedicated-server verification

The 1.0.0 release JAR was loaded through Forge's dedicated game-test server target with no Wold's Vaults dependency. The verification environment used Minecraft 1.18.2, Vault Hunters 3.20.3.6055, Java 17, and Forge 40.3.12, which is inside the declared `[40.3.11,41)` compatibility range.

Temporal Index completed Forge discovery, construction, common setup, common event-subscriber registration, common mixin application, and mod data-pack registration. The server reached game-test server creation; the borrowed host project then exited because it defines no test batches. No Temporal Index loading error, invalid distribution error, or attempted `net.minecraft.client` class load occurred.

An additional exact-Forge-40.3.11 launch verified FML discovery, server distribution cleaning, and server-side mixin preparation. See [Client/server safety](server-safety.md) for the sided-code audit and server-authority boundaries.

The release JAR was then relaunched in CMA Remastered on exact Forge 40.3.11. A server-authoritative NBT readback verified sneak-scroll selection moving from slot `1` to slot `0` and back to slot `1` through the client-to-server packet.

## Repeating the check

Run:

```powershell
.\scripts\verify-cma-vault-compatibility.ps1
```

The script discovers CMA-labelled Prism instances, deduplicates identical Vault jars by SHA-256, and performs a clean Java compilation against each distinct jar. It does not install files into an instance.
