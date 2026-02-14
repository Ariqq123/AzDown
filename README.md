# AzDown

AzDown is a modern Paper plugin that lets players browse plugin providers from an in-game GUI.

## Features
- `/azdown [query]` command opens a provider selector.
- Provider tiles for **Modrinth**, **Spigot**, and **Hangar**.
- Async search requests to provider APIs.
- Result GUI showing plugin name, summary, version, downloads, and URL.
- Smart config validation that auto-fixes invalid values on startup.
- GUI click/success/error sounds for player feedback (configurable).

## Supported providers
- **Modrinth**: Uses public search API and filters for plugin projects.
- **Hangar**: Uses PaperMC Hangar project search API.
- **Spigot**: Falls back to a web search link because there is no stable public JSON search API.

## Build
```bash
mvn clean package
```

The plugin jar is generated in `target/`.

## Usage
1. Install the jar into your server `plugins` folder.
2. Start the server.
3. Run:
   ```
   /azdown essentials
   ```
4. Click a provider and browse results.
5. Reload config without restart (requires `azdown.admin.reload`):
   ```
   /azdown reload
   ```


## Configuration
`config.yml` supports:
- `default-query`: fallback search when `/azdown` is used without arguments.
- `search-limit`: number of results fetched per provider (1-45; invalid values auto-reset).
- `sounds.*`: enable/disable and customize click/success/error sounds with volume/pitch.

If config values are missing or invalid, AzDown auto-repairs them and saves corrected values.
