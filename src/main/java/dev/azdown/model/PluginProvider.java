package dev.azdown.model;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum PluginProvider {
    MODRINTH("Modrinth", NamedTextColor.GREEN),
    SPIGOT("Spigot", NamedTextColor.GOLD),
    HANGAR("Hangar", NamedTextColor.AQUA);

    private final String displayName;
    private final NamedTextColor color;

    PluginProvider(String displayName, NamedTextColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public String displayName() {
        return displayName;
    }

    public Component displayComponent() {
        return Component.text(displayName, color);
    }
}
