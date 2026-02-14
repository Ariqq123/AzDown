package dev.azdown.command;

import dev.azdown.gui.ProviderBrowserGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public class BrowseCommand implements CommandExecutor, TabCompleter {
    private final ProviderBrowserGui providerBrowserGui;
    private final String defaultQuery;

    public BrowseCommand(ProviderBrowserGui providerBrowserGui, String defaultQuery) {
        this.providerBrowserGui = providerBrowserGui;
        this.defaultQuery = defaultQuery;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        String query = args.length == 0 ? defaultQuery : String.join(" ", args);
        providerBrowserGui.openProviderMenu(player, query);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return List.of();
    }
}
