package dev.azdown.command;

import dev.azdown.AzDownPlugin;
import dev.azdown.config.PluginSettings;
import dev.azdown.gui.ProviderBrowserGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class BrowseCommand implements CommandExecutor, TabCompleter {
    private static final String RELOAD_PERMISSION = "azdown.admin.reload";

    private final AzDownPlugin plugin;
    private final ProviderBrowserGui providerBrowserGui;

    public BrowseCommand(AzDownPlugin plugin, ProviderBrowserGui providerBrowserGui) {
        this.plugin = plugin;
        this.providerBrowserGui = providerBrowserGui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            return handleReload(sender);
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        String query = args.length == 0
                ? plugin.currentSettings().defaultQuery()
                : String.join(" ", args);
        providerBrowserGui.openProviderMenu(player, query);
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission(RELOAD_PERMISSION)) {
            sender.sendMessage(Component.text("You do not have permission to reload AzDown.", NamedTextColor.RED));
            return true;
        }

        PluginSettings settings = plugin.reloadPluginSettings();
        providerBrowserGui.updateSettings(settings);

        sender.sendMessage(Component.text("AzDown configuration reloaded.", NamedTextColor.GREEN));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "reload".startsWith(args[0].toLowerCase())) {
            return List.of("reload");
        }
        return List.of();
    }
}
