package dev.azdown;

import dev.azdown.command.BrowseCommand;
import dev.azdown.gui.ProviderBrowserGui;
import dev.azdown.service.PluginSearchService;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;

public final class AzDownPlugin extends JavaPlugin {
    private ProviderBrowserGui providerBrowserGui;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        PluginSearchService searchService = new PluginSearchService(httpClient);
        providerBrowserGui = new ProviderBrowserGui(this, searchService);

        String defaultQuery = getConfig().getString("default-query", "essentials");
        BrowseCommand browseCommand = new BrowseCommand(providerBrowserGui, defaultQuery);
        Objects.requireNonNull(getCommand("azdown"), "azdown command missing in plugin.yml")
                .setExecutor(browseCommand);
        Objects.requireNonNull(getCommand("azdown"), "azdown command missing in plugin.yml")
                .setTabCompleter(browseCommand);

        getServer().getPluginManager().registerEvents(providerBrowserGui, this);
        getLogger().info("AzDown enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("AzDown disabled.");
    }
}
