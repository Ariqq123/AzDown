package dev.azdown;

import dev.azdown.command.BrowseCommand;
import dev.azdown.config.PluginSettings;
import dev.azdown.gui.ProviderBrowserGui;
import dev.azdown.service.PluginSearchService;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;

public final class AzDownPlugin extends JavaPlugin {
    private static final String LOGO = """
            \n░█████╗░███████╗██████╗░░█████╗░░██╗░░░░░░░██╗███╗░░██╗
            ██╔══██╗╚════██║██╔══██╗██╔══██╗░██║░░██╗░░██║████╗░██║
            ███████║░░███╔═╝██║░░██║██║░░██║░╚██╗████╗██╔╝██╔██╗██║
            ██╔══██║██╔══╝░░██║░░██║██║░░██║░░████╔═████║░██║╚████║
            ██║░░██║███████╗██████╔╝╚█████╔╝░░╚██╔╝░╚██╔╝░██║░╚███║
            ╚═╝░░╚═╝╚══════╝╚═════╝░░╚════╝░░░░╚═╝░░░╚═╝░░╚═╝░░╚══╝
            """;

    private volatile PluginSettings settings;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        settings = PluginSettings.loadAndRepair(this);

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        PluginSearchService searchService = new PluginSearchService(httpClient);
        ProviderBrowserGui providerBrowserGui = new ProviderBrowserGui(this, searchService, settings);

        BrowseCommand browseCommand = new BrowseCommand(this, providerBrowserGui);
        Objects.requireNonNull(getCommand("azdown"), "azdown command missing in plugin.yml")
                .setExecutor(browseCommand);
        Objects.requireNonNull(getCommand("azdown"), "azdown command missing in plugin.yml")
                .setTabCompleter(browseCommand);

        getServer().getPluginManager().registerEvents(providerBrowserGui, this);
        logPluginState("enabled");
    }

    public PluginSettings currentSettings() {
        return settings;
    }

    public PluginSettings reloadPluginSettings() {
        reloadConfig();
        settings = PluginSettings.loadAndRepair(this);
        return settings;
    }

    @Override
    public void onDisable() {
        logPluginState("disabled");
    }

    private void logPluginState(String state) {
        LOGO.lines().forEach(getLogger()::info);
        getLogger().info("AzDown " + state + ".");
    }
}
