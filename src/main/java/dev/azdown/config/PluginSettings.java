package dev.azdown.config;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public record PluginSettings(
        String defaultQuery,
        int searchLimit,
        boolean soundsEnabled,
        Sound clickSound,
        Sound successSound,
        Sound errorSound,
        float volume,
        float pitch
) {
    private static final String DEFAULT_QUERY = "essentials";
    private static final int DEFAULT_SEARCH_LIMIT = 9;
    private static final Sound DEFAULT_CLICK_SOUND = Sound.UI_BUTTON_CLICK;
    private static final Sound DEFAULT_SUCCESS_SOUND = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
    private static final Sound DEFAULT_ERROR_SOUND = Sound.ENTITY_VILLAGER_NO;

    public static PluginSettings loadAndRepair(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        boolean changed = false;

        String defaultQuery = config.getString("default-query", DEFAULT_QUERY);
        if (defaultQuery == null || defaultQuery.isBlank()) {
            defaultQuery = DEFAULT_QUERY;
            config.set("default-query", defaultQuery);
            changed = true;
        }

        int searchLimit = config.getInt("search-limit", DEFAULT_SEARCH_LIMIT);
        if (searchLimit < 1 || searchLimit > 45) {
            searchLimit = DEFAULT_SEARCH_LIMIT;
            config.set("search-limit", searchLimit);
            changed = true;
        }

        boolean soundsEnabled = config.getBoolean("sounds.enabled", true);
        if (!config.contains("sounds.enabled")) {
            config.set("sounds.enabled", soundsEnabled);
            changed = true;
        }

        Sound clickSound = parseSound(config, "sounds.click", DEFAULT_CLICK_SOUND, plugin);
        if (clickSound == DEFAULT_CLICK_SOUND && !DEFAULT_CLICK_SOUND.name().equals(config.getString("sounds.click"))) {
            config.set("sounds.click", DEFAULT_CLICK_SOUND.name());
            changed = true;
        }

        Sound successSound = parseSound(config, "sounds.success", DEFAULT_SUCCESS_SOUND, plugin);
        if (successSound == DEFAULT_SUCCESS_SOUND && !DEFAULT_SUCCESS_SOUND.name().equals(config.getString("sounds.success"))) {
            config.set("sounds.success", DEFAULT_SUCCESS_SOUND.name());
            changed = true;
        }

        Sound errorSound = parseSound(config, "sounds.error", DEFAULT_ERROR_SOUND, plugin);
        if (errorSound == DEFAULT_ERROR_SOUND && !DEFAULT_ERROR_SOUND.name().equals(config.getString("sounds.error"))) {
            config.set("sounds.error", DEFAULT_ERROR_SOUND.name());
            changed = true;
        }

        float volume = (float) config.getDouble("sounds.volume", 0.8D);
        if (volume <= 0 || volume > 2.0F) {
            volume = 0.8F;
            config.set("sounds.volume", volume);
            changed = true;
        }

        float pitch = (float) config.getDouble("sounds.pitch", 1.0D);
        if (pitch <= 0 || pitch > 2.0F) {
            pitch = 1.0F;
            config.set("sounds.pitch", pitch);
            changed = true;
        }

        if (changed) {
            plugin.saveConfig();
            plugin.getLogger().warning("Invalid or missing config values were auto-fixed and saved.");
        }

        return new PluginSettings(defaultQuery, searchLimit, soundsEnabled, clickSound, successSound, errorSound, volume, pitch);
    }

    private static Sound parseSound(FileConfiguration config, String path, Sound fallback, JavaPlugin plugin) {
        String raw = config.getString(path, fallback.name());
        try {
            return Sound.valueOf(raw);
        } catch (RuntimeException exception) {
            plugin.getLogger().warning("Invalid sound in config at '" + path + "': " + raw + " (using " + fallback + ")");
            return fallback;
        }
    }
}
