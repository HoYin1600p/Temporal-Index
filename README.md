# Temporal Index

Temporal Index is a Minecraft Forge addon for Vault Hunters that will provide a dedicated container for temporal shards and temporal relics.

## Development target

- Minecraft 1.18.2
- Forge 40.3.11
- Java 17
- Mod ID: `temporal_index`

## Current status

The repository contains the initial, buildable Forge scaffold. Container behavior and Vault Hunters integration will be implemented after the reference mods have been inspected.

## Build

```powershell
.\gradlew.bat build
```

The built mod JAR is written to `build/libs/`.

## Development runs

Import `build.gradle` as a Gradle project in IntelliJ IDEA, then generate run configurations if needed:

```powershell
.\gradlew.bat genIntellijRuns
```

## License

All rights reserved.
