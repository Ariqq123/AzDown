package dev.azdown.service;

import dev.azdown.model.PluginListing;
import dev.azdown.model.PluginProvider;
import dev.azdown.provider.HangarClient;
import dev.azdown.provider.ModrinthClient;
import dev.azdown.provider.PluginProviderClient;
import dev.azdown.provider.SpigotClient;

import java.net.http.HttpClient;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PluginSearchService {
    private final Map<PluginProvider, PluginProviderClient> clients = new EnumMap<>(PluginProvider.class);

    public PluginSearchService(HttpClient httpClient) {
        clients.put(PluginProvider.MODRINTH, new ModrinthClient(httpClient));
        clients.put(PluginProvider.SPIGOT, new SpigotClient());
        clients.put(PluginProvider.HANGAR, new HangarClient(httpClient));
    }

    public CompletableFuture<List<PluginListing>> searchAsync(PluginProvider provider, String query, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return clients.get(provider).search(query, limit);
            } catch (Exception exception) {
                throw new IllegalStateException("Failed to search " + provider.displayName(), exception);
            }
        });
    }
}
