# AzDown

AzDown is a modern Paper plugin that lets players browse plugin providers from an in-game GUI.

## Features
- `/azdown [query]` command opens a provider selector.
- Provider tiles for **Modrinth**, **Spigot**, and **Hangar**.
- Async search requests to provider APIs.
- Result GUI showing plugin name, summary, version, downloads, and URL.

## Supported providers
- **Modrinth**: Uses public search API and filters for plugin projects.
- **Hangar**: Uses PaperMC Hangar project search API.
- **Spigot**: Falls back to a web search link because there is no stable public JSON search API.

## Build
```bash
mvn package
```

The plugin jar is generated in `build/libs/`.

## Usage
1. Install the jar into your server `plugins` folder.
2. Start the server.
3. Run:
   ```
   /azdown essentials
   ```
4. Click a provider and browse results.
