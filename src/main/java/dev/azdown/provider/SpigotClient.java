package dev.azdown.provider;

import dev.azdown.model.PluginListing;

import java.util.List;

public class SpigotClient implements PluginProviderClient {
    @Override
    public List<PluginListing> search(String query, int limit) {
        // Spigot does not provide a public unauthenticated search API suitable for direct integration.
        // This placeholder keeps the UX consistent and points users to the web page.
        return List.of(
                new PluginListing(
                        "Open Spigot search in browser",
                        "Spigot search is not publicly exposed via a stable JSON API.",
                        "https://www.spigotmc.org/search/18124983/?q=" + query.replace(" ", "+") + "&o=date",
                        "N/A",
                        0
                )
        );
    }
}
