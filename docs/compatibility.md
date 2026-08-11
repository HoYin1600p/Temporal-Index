# Vault compatibility verification

Temporal Index has only three runtime dependencies: Minecraft, Forge, and `the_vault`. Wold's Vaults and the other inspected reference mods are not required.

The `the_vault` dependency is intentionally constrained by mod ID rather than a pack-specific release number. Vault reports versions such as `1.18.2-20.0.3-remastered.6909`, which are not meaningfully ordered against upstream-style `3.21.x` labels by Forge's Maven version-range rules. Compatibility is instead verified against the concrete jars below.

## CMA matrix

On 2026-08-10 the source compiled successfully against every distinct active `the_vault` jar in the five CMA-labelled Prism instances:

| CMA instance | Vault jar | SHA-256 prefix | Result |
| --- | --- | --- | --- |
| CMA Wolds | 3.21.5.6573 | `58672F06C4B3564` | Pass |
| CMA Third / CMA Wolds 0.33.0 | 3.21.6.6884 | `E4B1E896558D6940` | Pass |
| CMA Asgard | 3.21.62 | `DBB00F7E0FCA832F` | Pass |
| CMA Remastered | 20.0.3-remastered.6909 | `53E56FE94AA546F8` | Pass |

The installed `temporal_shard.json` configurations were also inspected. All five use the same ordered set of 16 modifier IDs expected by the book. Durations differ between modifiers in the two CMA Wolds configurations; the book retains the Vault-generated duration with each stored modifier instead of replacing it with a hard-coded duration.

## Repeating the check

Run:

```powershell
.\scripts\verify-cma-vault-compatibility.ps1
```

The script discovers CMA-labelled Prism instances, deduplicates identical Vault jars by SHA-256, and performs a clean Java compilation against each distinct jar. It does not install files into an instance.
