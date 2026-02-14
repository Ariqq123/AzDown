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

public class HangarClient implements PluginProviderClient {
    private static final String HANGAR_API = "https://hangar.papermc.io/api/v1/projects";

    private final HttpClient httpClient;

    public HangarClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public List<PluginListing> search(String query, int limit) throws IOException, InterruptedException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        URI uri = URI.create(HANGAR_API + "?query=" + encodedQuery + "&limit=" + limit);

        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("User-Agent", "AzDown/0.1.0")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Hangar API returned " + response.statusCode());
        }

        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray results = root.getAsJsonArray("result");

        List<PluginListing> listings = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            JsonObject entry = results.get(i).getAsJsonObject();
            JsonObject stats = entry.getAsJsonObject("stats");

            String owner = entry.get("namespace").getAsString();
            String name = entry.get("name").getAsString();

            listings.add(new PluginListing(
                    name,
                    entry.has("description") ? entry.get("description").getAsString() : "No description.",
                    "https://hangar.papermc.io/" + owner + "/" + name,
                    "N/A",
                    stats != null && stats.has("downloads") ? stats.get("downloads").getAsInt() : 0
            ));
        }

        return listings;
    }
}
