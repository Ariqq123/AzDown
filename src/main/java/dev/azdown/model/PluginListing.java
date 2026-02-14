package dev.azdown.model;

public record PluginListing(
        String title,
        String summary,
        String projectUrl,
        String latestVersion,
        int downloads
) {
}
