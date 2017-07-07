package me.bumblebeee_.chatactions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventories {

    LogManager logs = new LogManager();
    Messages msg = new Messages();

    public void openMain(Player p) {
        Inventory inv = Bukkit.getServer().createInventory(null, 27, "Salva Giocata");

        ItemStack close = createItem(Material.NETHER_STAR, (short)0, "&cChiudi", null);
        ItemStack filler = createItem(Material.STAINED_GLASS_PANE, (short)11, " ", null);
        ItemStack start = createItem(Material.STAINED_GLASS_PANE, (short)5, "&aInizia a salvare la giocata", null);
        ItemStack stop = createItem(Material.STAINED_GLASS_PANE, (short)14, "&cInterrompi il salvataggio", null);
        ItemStack view = createItem(Material.STAINED_GLASS_PANE, (short)0, "&6Vedi le giocate salvate", null);

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, filler);
        }
        inv.setItem(22, close);
        inv.setItem(11, start);
        inv.setItem(13, stop);
        inv.setItem(15, view);

        p.openInventory(inv);
    }

    public void openLogs(Player p) {
        Inventory inv = Bukkit.getServer().createInventory(null, 27, "Giocate Recenti");

        ItemStack close = createItem(Material.NETHER_STAR, (short)0, "&cChiudi", null);
        ItemStack filler = createItem(Material.STAINED_GLASS_PANE, (short)11, " ", null);

        for (int i = 0; i < 27; i++) {
            if (i >= 0 && i <= 8)
                inv.setItem(i, filler);
            else if (i >= 18 && i <= 26)
                inv.setItem(i, filler);
        }
        inv.setItem(22, close);

        File f = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data" + File.separator + p.getUniqueId() + ".yml");
        if (!f.exists()) {
            p.sendMessage(msg.getMessage("noLogsFound"));
            p.closeInventory();
            return;
        }

        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        ConfigurationSection ids = c.getConfigurationSection("logs");
        if (ids == null) {
            p.sendMessage(msg.getMessage("noLogsFound"));
            p.closeInventory();
            return;
        }

        int slot = 9;
        for (String id : ids.getKeys(false)) {
            if (slot > 18)
                continue;

            List<String> lore = new ArrayList<>(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', "&7ID: " + id)));
            ItemStack i = logs.getLogItem(p, id);
            ItemMeta im = i.getItemMeta();
            im.setLore(lore);
            i.setItemMeta(im);

            inv.setItem(slot, i);
            slot++;
        }

        p.openInventory(inv);
    }

    public ItemStack createItem(Material mat, short metadata, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(mat, 1, metadata);
        List<String> coloredLore = new ArrayList<>();

        ItemMeta im = item.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        if (lore != null) {
            for (String loreItem : coloredLore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', loreItem));
            }
            im.setLore(coloredLore);
        }
        item.setItemMeta(im);
        return item;
    }

}
