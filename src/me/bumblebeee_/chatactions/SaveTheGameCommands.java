package me.bumblebeee_.chatactions;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.land.SimpleChunkLocation;
import org.kingdoms.constants.player.KingdomPlayer;
import org.kingdoms.main.Kingdoms;
import org.kingdoms.manager.game.LandManager;

public class SaveTheGameCommands implements CommandExecutor {

    Messages msgs = new Messages();
    LogManager logs = new LogManager();
    Inventories inv = new Inventories();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (cmd.getName().equalsIgnoreCase("vedigiocata")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command");
                return false;
            }
            Player p = (Player) sender;

            if (!sender.hasPermission("stg.vedigiocata")) {
                sender.sendMessage(msgs.getMessage("noPermissions"));
                return false;
            }

            inv.openMain(p);
            return true;
        } else if (cmd.getName().equalsIgnoreCase("salvagiocata")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command");
                return false;
            }

            if (!sender.hasPermission("stg.salvagiocata")) {
                sender.sendMessage(msgs.getMessage("noPermissions"));
                return false;
            }
            Player p = (Player) sender;

            if (logs.getLogging().contains(p.getUniqueId())) {
                p.sendMessage(msgs.getMessage("alreadyLogging"));
                return false;
            }

            logs.startLogging(p.getUniqueId());
            p.sendMessage(msgs.getMessage("startedLogging"));
            return true;
        } else if (cmd.getName().equalsIgnoreCase("stopgiocata")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command");
                return false;
            }

            if (!sender.hasPermission("stg.stopgiocata")) {
                sender.sendMessage(msgs.getMessage("noPermissions"));
                return false;
            }
            Player p = (Player) sender;

            if (!logs.getLogging().contains(p.getUniqueId())) {
                p.sendMessage(msgs.getMessage("notLogging"));
                return false;
            }

            int priority = -10;
            String region = "None";
            RegionManager regionManager = ChatActions.getWorldGuard().getRegionManager(p.getWorld());
            for (ProtectedRegion r : regionManager.getApplicableRegions(p.getLocation())) {
                if (r.getPriority() >= priority) {
                    region = r.getId();
                    priority = r.getPriority();
                }
            }

            String kingdom = "None";
            LandManager lm = Kingdoms.getInstance().getManagers().getLandManager();
            Land land = lm.getOrLoadLand(new SimpleChunkLocation(p.getLocation().getChunk()));
            if (land.getOwner() != null) {
                Player t = Bukkit.getServer().getPlayer(land.getOwner());
                if (t != null) {
                    KingdomPlayer kp = Kingdoms.getInstance().getManagers().getPlayerManager().getSession(p);
                    kingdom = kp.getKingdomName();
                } else {
                    OfflinePlayer ot = Bukkit.getServer().getOfflinePlayer(land.getOwner());
                    if (ot.hasPlayedBefore()) {
                        KingdomPlayer kp = Kingdoms.getInstance().getManagers().getPlayerManager().getSession(p);
                        kingdom = kp.getKingdomName();
                    }
                }
            }

            logs.setRegion(p.getUniqueId(), region, String.valueOf(logs.getSelected(p.getUniqueId())));
            logs.setKingdom(p.getUniqueId(), kingdom, String.valueOf(logs.getSelected(p.getUniqueId())));

            logs.stopLogging(p.getUniqueId());
            p.sendMessage(msgs.getMessage("stoppedLogging"));
            return true;
        }
        return false;
    }
}
