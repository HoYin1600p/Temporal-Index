# Contributing

Contributions to Temporal Index are welcome through GitHub issues and pull requests.

## Development environment

- Java 17
- Minecraft 1.18.2
- Forge 40.3.11
- A local compatible `the_vault` JAR

Build with:

```powershell
.\gradlew.bat build -Pvault_mod_jar="X:\path\to\the_vault.jar"
```

Before submitting a change:

1. Run `./gradlew.bat build` against the Vault Hunters version being targeted.
2. Run `./scripts/verify-public-identity.ps1`.
3. Test server-authoritative inventory transfers in survival mode.
4. Test relic activation inside an active Vault rather than relying on creative mode.
5. Describe the Vault Hunters JAR version used for verification.

The optional compatibility matrix can be repeated on a machine with CMA test instances:

```powershell
.\scripts\verify-cma-vault-compatibility.ps1
```

## Project boundaries

- Do not copy source code or assets from Vault Hunters or inspected reference mods.
- Do not make Wold's Vaults, Fruit Sac, Sophisticated Backpacks, or CMA a runtime dependency.
- Keep Vault Hunters authoritative for temporal loot pools, identification, restrictions, effects, and durations.
- Preserve server-side validation for storage, selection, transfer, use, and overflow paths.

## Licensing contributions

By contributing, you agree that your contribution is licensed under this repository's GNU General Public License v3.0 only (`GPL-3.0-only`).
