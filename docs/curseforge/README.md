# CurseForge release materials

This directory contains the source-of-truth listing text and upload metadata for Temporal Index on CurseForge.

- `DESCRIPTION.md`: project-page Markdown.
- `CHANGELOG-X.Y.Z.md`: concise, user-visible per-file changelog.
- `UPLOAD-X.Y.Z.md`: copy/paste fields, dependency metadata, integrity values, and upload checklist.
- `../../art/release/temporal-index-curseforge.png`: square project icon.

Locally assembled upload kits belong under `release/curseforge/` and are excluded from Git. Upload the normal runnable JAR from `build/libs/` as the CurseForge project file; never upload the support ZIP in its place.
