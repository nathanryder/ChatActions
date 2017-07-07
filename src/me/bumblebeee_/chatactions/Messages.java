package me.bumblebeee_.chatactions;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Messages {

    public static File f;
    public static YamlConfiguration c;

    public void setup() {
        f = new File(ChatActions.getInstance().getDataFolder() + File.separator + "messages.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            c = YamlConfiguration.loadConfiguration(f);

            createMessage("noPermissions", "&cYou do not have the required permissions.");
            createMessage("noLogsFound", "&cYou do not have any logs saved");
            createMessage("alreadyLogging", "&cYou are already logging");
            createMessage("startedLogging", "&aYou have started logging");
            createMessage("notLogging", "&cYou are not logging");
            createMessage("stoppedLogging", "&aYou have stopped logging");

            try {
                c.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            c = YamlConfiguration.loadConfiguration(f);
        }
    }

    public void createMessage(String key, String value) {
        c.set(key, value);
    }

    public String getMessage(String key) {
        String msg = c.getString(key);
        if (msg == null) {
            ChatActions.getInstance().getLogger().warning(ChatColor.RED + "Failed to find message with key " + key);
            ChatActions.getInstance().getLogger().warning(ChatColor.RED + "Deleting the messages.yml file or adding the key will fix this");
            return ChatColor.translateAlternateColorCodes('&', "&cFailed to find message! Please report this to a server admin.");
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
