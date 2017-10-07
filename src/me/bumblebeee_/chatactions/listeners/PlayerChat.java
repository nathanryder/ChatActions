package me.bumblebeee_.chatactions.listeners;

import me.bumblebeee_.chatactions.ChatActions;
import me.bumblebeee_.chatactions.LogManager;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerChat implements Listener {

    LogManager logs = new LogManager();

    @EventHandler (ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        ConfigurationSection cs = ChatActions.getInstance().getConfig().getConfigurationSection("Channels");
        if (cs == null)
            return;

        e.setCancelled(true);
        int cooldown = 0;
        final Player p = e.getPlayer();
        String ch = "NORMAL";
        String message = e.getMessage();
        String consoleMsg = ChatColor.stripColor(p.getDisplayName()) + ": " + ChatColor.stripColor(e.getMessage());
        String send = ChatColor.translateAlternateColorCodes('&', ChatActions.getInstance().getConfig().getString("Channels.Normal.format").replace("%player%", p.getDisplayName()).replace("%msg%", e.getMessage()));
        int radius = ChatActions.getInstance().getConfig().getInt("Channels.Normal.range");

        for (String channel : cs.getKeys(false)) {
            if (channel.equalsIgnoreCase("Normal"))
                continue;
            
            if (!p.hasPermission(ChatActions.getInstance().getConfig().getString("Channels." + channel + ".permission")))
                continue;

            String suffix = ChatActions.getInstance().getConfig().getString("Channels." + channel + ".suffix");
            String prefix = ChatActions.getInstance().getConfig().getString("Channels." + channel + ".prefix");

            if (suffix.length() > 0 && message.endsWith(suffix)) {
                ch = channel;
                send = ChatColor.translateAlternateColorCodes('&', ChatActions.getInstance().getConfig().getString("Channels." + channel + ".format").replace("%player%", p.getDisplayName()).replace("%msg%", e.getMessage())).replace(suffix, "");
                consoleMsg = consoleMsg.replace(suffix, "");
                radius = ChatActions.getInstance().getConfig().getInt("Channels." + channel + ".range");
                cooldown = ChatActions.getInstance().getConfig().getInt("Channels." + channel + ".cooldown");
            } else if (prefix.length() > 0 && message.startsWith(prefix)) {
                ch = channel;
                send = ChatColor.translateAlternateColorCodes('&', ChatActions.getInstance().getConfig().getString("Channels." + channel + ".format").replace("%player%", p.getDisplayName()).replace("%msg%", e.getMessage())).replace(prefix, "");
                consoleMsg = consoleMsg.replace(prefix, "");
                radius = ChatActions.getInstance().getConfig().getInt("Channels." + channel + ".range");
                cooldown = ChatActions.getInstance().getConfig().getInt("Channels." + channel + ".cooldown");
            }
        }

        if (ChatActions.cooldowns.containsKey(p.getUniqueId())) {
            HashMap<String, Integer> d = ChatActions.cooldowns.get(p.getUniqueId());
            if (d.containsKey(ch.toLowerCase())) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatActions.getInstance().getConfig().getString("Messages.Cooldown").replace("%time%", String.valueOf(d.get(ch.toLowerCase()))).replace("%channel%", ch.toLowerCase())));
                return;
            }
        }

        //Actual message sending
        List<UUID> recieved = new ArrayList<>();
        if (radius == 0) {
            for (Player t : Bukkit.getServer().getOnlinePlayers()) {
                if (p.hasPermission("chatactions.arcane")) {
                    if (t.hasPermission("chatactions.arcane") || t.hasPermission("chatactions.arcane.bypass")) {
                        t.sendMessage(send);
                        recieved.add(t.getUniqueId());
                    } else {
                        t.sendMessage(send.replace(" ", " " + ChatColor.MAGIC));
                    }
                } else {
                    t.sendMessage(send);
                    recieved.add(t.getUniqueId());
                }
            }
        } else {
            for (Entity en : p.getNearbyEntities(radius, radius, radius)) {
                if (!(en instanceof Player))
                    continue;
                en.sendMessage(send);
                recieved.add(((Player) en).getUniqueId());
            }
            p.sendMessage(send);
            recieved.add(p.getUniqueId());
        }
        Bukkit.getServer().getLogger().info(StringUtils.prependIfMissing(consoleMsg, "[" + ch + "] "));

        String spyMsg = ChatColor.translateAlternateColorCodes('&', ChatActions.getInstance().getConfig().getString("Messages.SpyFormat")).replace("%action%", ch.toUpperCase()).replace("%player%", p.getDisplayName()).replace("%msg%", e.getMessage());
        for (UUID uuid : ChatActions.spyMode.keySet()) {
            if (!ChatActions.spyMode.get(uuid))
                continue;
            Player t = Bukkit.getServer().getPlayer(uuid);
            if (t == null)
                continue;

            t.sendMessage(spyMsg);
            recieved.add(t.getUniqueId());
        }

        //Logging
        for (UUID uuid : logs.getLogging()) {
            if (recieved.contains(uuid)) {
                logs.writeToFile(uuid, send);
            }
        }

        //Cooldowns
        if (!p.hasPermission("chatactions.admin")) {
            if (cooldown != 0) {
                final String cch = ch;
                HashMap<String, Integer> d;
                HashMap<String, BukkitRunnable> rd;
                if (ChatActions.cooldowns.containsKey(p.getUniqueId())) {
                    d = ChatActions.cooldowns.get(p.getUniqueId());
                    if (d.containsKey(ch.toLowerCase())) {
                        d.remove(ch.toLowerCase());
                        d.put(ch.toLowerCase(), cooldown);
                    } else {
                        d.put(ch.toLowerCase(), cooldown);
                    }
                } else {
                    d = new HashMap<>();
                    d.put(ch.toLowerCase(), cooldown);
                }

                if (ChatActions.cdTask.containsKey(p.getUniqueId())) {
                    rd = ChatActions.cdTask.get(p.getUniqueId());
                    if (rd.containsKey(ch.toLowerCase())) {
                        rd.remove(ch.toLowerCase());
                        rd.put(ch.toLowerCase(), new BukkitRunnable() {
                            public void run() {
                                if (!ChatActions.cooldowns.containsKey(p.getUniqueId()))
                                    cancel();

                                HashMap<String, Integer> d = ChatActions.cooldowns.get(p.getUniqueId());
                                if (!d.containsKey(cch.toLowerCase()))
                                    cancel();

                                d.put(cch.toLowerCase(), d.get(cch.toLowerCase()) - 1);
                                if (d.get(cch.toLowerCase()) == 1) {
                                    d.remove(cch.toLowerCase());
                                    ChatActions.cooldowns.put(p.getUniqueId(), d);
                                    ChatActions.cdTask.get(p.getUniqueId()).remove(cch.toLowerCase());
                                    cancel();
                                }
                            }
                        });
                    } else {
                        rd.put(ch.toLowerCase(), new BukkitRunnable() {
                            public void run() {
                                if (!ChatActions.cooldowns.containsKey(p.getUniqueId()))
                                    cancel();

                                HashMap<String, Integer> d = ChatActions.cooldowns.get(p.getUniqueId());
                                if (!d.containsKey(cch.toLowerCase()))
                                    cancel();

                                d.put(cch.toLowerCase(), d.get(cch.toLowerCase()) - 1);
                                if (d.get(cch.toLowerCase()) <= 1) {
                                    d.remove(cch.toLowerCase());
                                    ChatActions.cooldowns.put(p.getUniqueId(), d);
                                    ChatActions.cdTask.get(p.getUniqueId()).remove(cch.toLowerCase());
                                    cancel();
                                }
                            }
                        });
                    }
                } else {
                    rd = new HashMap<>();
                    rd.put(ch.toLowerCase(), new BukkitRunnable() {
                        public void run() {
                            if (!ChatActions.cooldowns.containsKey(p.getUniqueId()))
                                cancel();

                            HashMap<String, Integer> d = ChatActions.cooldowns.get(p.getUniqueId());
                            if (!d.containsKey(cch.toLowerCase()))
                                cancel();

                            d.put(cch.toLowerCase(), d.get(cch.toLowerCase()) - 1);
                            if (d.get(cch.toLowerCase()) <= 1) {
                                d.remove(cch.toLowerCase());
                                ChatActions.cooldowns.put(p.getUniqueId(), d);
                                ChatActions.cdTask.get(p.getUniqueId()).remove(cch.toLowerCase());
                                cancel();
                            }
                        }
                    });
                }

                ChatActions.cooldowns.put(p.getUniqueId(), d);
                ChatActions.cdTask.put(p.getUniqueId(), rd);
                ChatActions.cdTask.get(p.getUniqueId()).get(ch.toLowerCase()).runTaskTimer(ChatActions.getInstance(), 20, 20);
            }
        }
    }
}