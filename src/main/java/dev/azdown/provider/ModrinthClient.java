package dev.azdown.provider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.azdown.model.PluginListing;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ModrinthClient implements PluginProviderClient {
    private static final String MODRINTH_API = "https://api.modrinth.com/v2/search";

    private final HttpClient httpClient;

    public ModrinthClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public List<PluginListing> search(String query, int limit) throws IOException, InterruptedException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String facets = URLEncoder.encode("[[\"project_type:plugin\"]]", StandardCharsets.UTF_8);

        URI uri = URI.create(MODRINTH_API + "?query=" + encodedQuery + "&limit=" + limit + "&facets=" + facets);
        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("User-Agent", "AzDown/0.1.0")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Modrinth API returned " + response.statusCode());
        }

        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray hits = root.getAsJsonArray("hits");

        List<PluginListing> results = new ArrayList<>();
        for (int i = 0; i < hits.size(); i++) {
            JsonObject entry = hits.get(i).getAsJsonObject();
            String slug = entry.has("slug") ? entry.get("slug").getAsString() : "unknown";

            results.add(new PluginListing(
                    entry.has("title") ? entry.get("title").getAsString() : slug,
                    entry.has("description") ? entry.get("description").getAsString() : "No description.",
                    "https://modrinth.com/plugin/" + slug,
                    entry.has("latest_version") ? entry.get("latest_version").getAsString() : "N/A",
                    entry.has("downloads") ? entry.get("downloads").getAsInt() : 0
            ));
        }

        return results;
    }
}
