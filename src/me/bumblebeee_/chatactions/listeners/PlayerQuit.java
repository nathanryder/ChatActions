package me.bumblebeee_.chatactions.listeners;

import me.bumblebeee_.chatactions.LogManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    LogManager logs = new LogManager();

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        logs.stopLogging(e.getPlayer().getUniqueId());
    }

}
