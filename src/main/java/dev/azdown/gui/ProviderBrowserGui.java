package dev.azdown.gui;

import dev.azdown.config.PluginSettings;
import dev.azdown.model.PluginListing;
import dev.azdown.model.PluginProvider;
import dev.azdown.service.PluginSearchService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class ProviderBrowserGui implements Listener {
    private final Plugin plugin;
    private final PluginSearchService pluginSearchService;
    private final PluginSettings settings;

    public ProviderBrowserGui(Plugin plugin, PluginSearchService pluginSearchService, PluginSettings settings) {
        this.plugin = plugin;
        this.pluginSearchService = pluginSearchService;
        this.settings = settings;
    }

    public void openProviderMenu(Player player, String query) {
        Inventory inventory = Bukkit.createInventory(new ProviderMenuHolder(query), 27,
                Component.text("AzDown Providers", NamedTextColor.DARK_AQUA));

        inventory.setItem(11, providerItem(PluginProvider.MODRINTH, Material.EMERALD));
        inventory.setItem(13, providerItem(PluginProvider.SPIGOT, Material.BOOK));
        inventory.setItem(15, providerItem(PluginProvider.HANGAR, Material.DIAMOND));

        player.openInventory(inventory);
        playClick(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof ProviderMenuHolder providerMenuHolder)) {
            if (holder instanceof ResultsMenuHolder) {
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) {
                    return;
                }

                ItemMeta meta = event.getCurrentItem().getItemMeta();
                if (meta == null || !meta.hasLore()) {
                    return;
                }

                List<Component> lore = meta.lore();
                if (lore == null || lore.isEmpty()) {
                    return;
                }

                String lastLine = PlainTextComponentSerializer.plainText().serialize(lore.get(lore.size() - 1));
                String url = lastLine.replace("URL: ", "");
                player.sendMessage(Component.text("Open this URL: ", NamedTextColor.YELLOW)
                        .append(Component.text(url, NamedTextColor.AQUA)));
                playSuccess(player);
                player.closeInventory();
            }
            return;
        }

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir() || clicked.getItemMeta() == null) {
            return;
        }

        PluginProvider provider = switch (clicked.getType()) {
            case EMERALD -> PluginProvider.MODRINTH;
            case BOOK -> PluginProvider.SPIGOT;
            case DIAMOND -> PluginProvider.HANGAR;
            default -> null;
        };

        if (provider == null) {
            playError(player);
            return;
        }

        playClick(player);
        player.sendMessage(Component.text("Searching " + provider.displayName() + "...", NamedTextColor.GRAY));
        pluginSearchService.searchAsync(provider, providerMenuHolder.query(), settings.searchLimit())
                .whenComplete((results, throwable) -> Bukkit.getScheduler().runTask(plugin, () -> {
                    if (throwable != null) {
                        player.sendMessage(Component.text("Search failed: " + throwable.getMessage(), NamedTextColor.RED));
                        playError(player);
                        return;
                    }
                    openResultsMenu(player, provider, providerMenuHolder.query(), results);
                }));
    }

    private void openResultsMenu(Player player, PluginProvider provider, String query, List<PluginListing> results) {
        Inventory inventory = Bukkit.createInventory(new ResultsMenuHolder(), 54,
                Component.text(provider.displayName() + " Results", NamedTextColor.DARK_GREEN));

        int slot = 10;
        for (PluginListing listing : results) {
            if (slot >= 44) {
                break;
            }
            inventory.setItem(slot, resultItem(listing));
            slot++;
            if (slot % 9 == 8) {
                slot += 2;
            }
        }

        if (results.isEmpty()) {
            ItemStack noResults = new ItemStack(Material.BARRIER);
            ItemMeta meta = noResults.getItemMeta();
            meta.displayName(Component.text("No results found", NamedTextColor.RED));
            meta.lore(List.of(Component.text("Try another keyword, e.g. 'economy'", NamedTextColor.GRAY)));
            noResults.setItemMeta(meta);
            inventory.setItem(22, noResults);
        }

        player.openInventory(inventory);
        player.sendMessage(Component.text("Showing results for: " + query, NamedTextColor.GRAY));
        if (results.isEmpty()) {
            playError(player);
        } else {
            playSuccess(player);
        }
    }

    private ItemStack providerItem(PluginProvider provider, Material material) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(provider.displayComponent());
        meta.lore(List.of(
                Component.text("Click to browse plugins", NamedTextColor.GRAY),
                Component.text("Provider: " + provider.displayName(), NamedTextColor.DARK_GRAY)
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack resultItem(PluginListing listing) {
        ItemStack stack = new ItemStack(Material.PAPER);
        ItemMeta meta = stack.getItemMeta();

        String summary = listing.summary();
        if (summary.length() > 80) {
            summary = summary.substring(0, 77) + "...";
        }

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(summary, NamedTextColor.GRAY));
        lore.add(Component.text("Version: " + listing.latestVersion(), NamedTextColor.DARK_GRAY));
        lore.add(Component.text("Downloads: " + listing.downloads(), NamedTextColor.DARK_GRAY));
        lore.add(Component.text("URL: " + listing.projectUrl(), NamedTextColor.AQUA));

        meta.displayName(Component.text(listing.title(), NamedTextColor.WHITE));
        meta.lore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    private void playClick(Player player) {
        playSound(player, settings.clickSound());
    }

    private void playSuccess(Player player) {
        playSound(player, settings.successSound());
    }

    private void playError(Player player) {
        playSound(player, settings.errorSound());
    }

    private void playSound(Player player, org.bukkit.Sound sound) {
        if (!settings.soundsEnabled()) {
            return;
        }
        player.playSound(player.getLocation(), sound, settings.volume(), settings.pitch());
    }

    private record ProviderMenuHolder(String query) implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    private static class ResultsMenuHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
