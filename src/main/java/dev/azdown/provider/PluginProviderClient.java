package dev.azdown.provider;

import dev.azdown.model.PluginListing;

import java.io.IOException;
import java.util.List;

public interface PluginProviderClient {
    List<PluginListing> search(String query, int limit) throws IOException, InterruptedException;
}
