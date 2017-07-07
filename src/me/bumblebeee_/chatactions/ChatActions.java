package me.bumblebeee_.chatactions;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import me.bumblebeee_.chatactions.listeners.InventoryClick;
import me.bumblebeee_.chatactions.listeners.PlayerChat;
import me.bumblebeee_.chatactions.listeners.PlayerQuit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ChatActions extends JavaPlugin {

    public static HashMap<UUID, HashMap<String, Integer>> cooldowns;
    public static HashMap<UUID, HashMap<String, BukkitRunnable>> cdTask;

    private static Plugin instance;
    public static HashMap<UUID, Boolean> spyMode = new HashMap<>();

    Messages msg = new Messages();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        msg.setup();
        registerEvents();

        Bukkit.getServer().getPluginCommand("chatactions").setExecutor(new ReloadCommand());
        Bukkit.getServer().getPluginCommand("salvagiocata").setExecutor(new SaveTheGameCommands());
        Bukkit.getServer().getPluginCommand("stopgiocata").setExecutor(new SaveTheGameCommands());
        Bukkit.getServer().getPluginCommand("vedigiocata").setExecutor(new SaveTheGameCommands());
        cooldowns = new HashMap<>();
        cdTask = new HashMap<>();
    }

    public void registerEvents() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerChat(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryClick(), this);
    }

    public static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }

    public static Plugin getInstance() { return instance; }

}
