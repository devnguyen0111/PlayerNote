package org.devnguyen;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import utils.checkPlugins;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlayerNote extends JavaPlugin {

    private String discordChannelId;
    private String updateCheckUrl;
    private String currentVersion;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        discordChannelId = getConfig().getString("discordChannelId");
        updateCheckUrl = getConfig().getString("updateCheckUrl");
        currentVersion = getDescription().getVersion();

        checkPlugins checkPlugins = new checkPlugins(this);
        if (!checkPlugins.checkPlugin("DiscordSRV")) {
            System.out.println("DiscordSRV is not installed. Stop PlayerNote plugin.");
            return;
        } else {
            System.out.println("DiscordSRV is installed. Continue PlayerNote plugin.");
        }

        // Check for updates
        checkForUpdates();

        // Plugin startup logic
        System.out.println("PlayerNote plugin is enabled.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("PlayerNote plugin is disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("pnote")) {
            if (args.length == 0) {
                sender.sendMessage("Usage: /pnote <message>");
                return true;
            }

            String message = String.join(" ", args);
            sendMessageToDiscord(message);
            sender.sendMessage("Message sent to Discord.");
            return true;
        }
        return false;
    }

    private void sendMessageToDiscord(String message) {
        TextChannel channel = DiscordSRV.getPlugin().getJda().getTextChannelById(discordChannelId);
        if (channel != null) {
            channel.sendMessage(message).queue();
        } else {
            getLogger().severe("Discord channel not found.");
        }
    }

    private void checkForUpdates() {
        try {
            URL url = new URL(updateCheckUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            String latestVersion = parseLatestVersion(content.toString());
            if (latestVersion != null && !currentVersion.equals(latestVersion)) {
                getLogger().info("A new version of PlayerNote is available: " + latestVersion);
            }
        } catch (Exception e) {
            getLogger().severe("Failed to check for updates: " + e.getMessage());
        }
    }

    private String parseLatestVersion(String jsonResponse) {
        Pattern pattern = Pattern.compile("\"tag_name\":\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(jsonResponse);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}