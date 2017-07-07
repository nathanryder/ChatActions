package me.bumblebeee_.chatactions.listeners;

import me.bumblebeee_.chatactions.Inventories;
import me.bumblebeee_.chatactions.LogManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class InventoryClick implements Listener {

    Inventories inv = new Inventories();
    LogManager logs = new LogManager();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() == null)
            return;
        if (!e.getCurrentItem().hasItemMeta())
            return;
        if (!e.getCurrentItem().getItemMeta().hasDisplayName()) {
            if (e.getCurrentItem().getType() != Material.WRITTEN_BOOK)
                return;
        }
        if (e.getWhoClicked() == null)
            return;

        Player p = (Player) e.getWhoClicked();
        String dis = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
        if (e.getInventory().getName().equalsIgnoreCase("Salva Giocata")) {
            e.setCancelled(true);
            if (dis.equalsIgnoreCase("Chiudi")) {
                p.closeInventory();
            } else if (dis.equalsIgnoreCase("Inizia a salvare la giocata")) {
                p.performCommand("salvagiocata");
                p.closeInventory();
            } else if (dis.equalsIgnoreCase("Interrompi il salvataggio")) {
                p.performCommand("stopgiocata");
                p.closeInventory();
            } else if (dis.equalsIgnoreCase("Vedi le giocate salvate")) {
                inv.openLogs(p);
            }
        } else if (e.getInventory().getName().equalsIgnoreCase("Giocate Recenti")) {
            e.setCancelled(true);
            if (dis == null) {
                if (e.getCurrentItem().getType() != Material.WRITTEN_BOOK)
                    return;

                ItemStack book = e.getCurrentItem();
                BookMeta bm = (BookMeta) book.getItemMeta();
                List<String> lore = bm.getLore();
                if (!lore.get(0).contains("ID")) {
                    p.sendMessage(ChatColor.RED + "An internal error has occurred!");
                    return;
                }

                String id = ChatColor.stripColor(lore.get(0)).split(" ")[1];
                p.getInventory().addItem(logs.getLogItem(p, id));
                p.closeInventory();
            } else if (dis.equalsIgnoreCase("Chiudi")) {
                p.closeInventory();
            }
        }
    }

}