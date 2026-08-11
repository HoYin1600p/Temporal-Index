# Placeholder book art

These original development assets are temporary and are intended to be replaced by final artist-made sprites and models.

## Files

- `temporal_index_closed_source.png`: transparent high-resolution closed-book source.
- `temporal_index_open_source.png`: transparent high-resolution open-book source retained from the earlier prototype; it is not used by the final closed-cover renderer.
- Runtime textures live under `src/main/resources/assets/temporal_index/textures/item/`. Version 1.0.0 always renders the closed book and places the selected temporal sprite on its cover.

## Generation

The sources were generated with OpenAI's built-in image generation tool, then processed with chroma-key removal and alpha validation.

Closed-book prompt summary: original dark-teal and aged-brass pixel-art spellbook, three-quarter inventory view, blank central cover panel, no symbols or text, flat green removal background.

Open-book prompt summary: matching open pixel-art spellbook with blank cream pages and a clear right-page relic area, no symbols or text, flat green removal background.
