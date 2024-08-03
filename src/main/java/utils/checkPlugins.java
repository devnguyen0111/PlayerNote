package utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class checkPlugins {
    private final JavaPlugin plugin;

    public checkPlugins(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean checkPlugin(String pluginName) {
        Plugin targetPlugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (targetPlugin != null && targetPlugin.isEnabled()) {
            return true;
        } else {
            plugin.getLogger().severe("Need " + pluginName + " to run this plugin.");
            Bukkit.getPluginManager().disablePlugin(plugin);
            return false;
        }
    }
}
