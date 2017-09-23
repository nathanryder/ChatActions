package me.bumblebeee_.chatactions;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LogManager {

    public static List<UUID> logging = new ArrayList<>();

    public void stopLogging(UUID uuid) {
        logging.remove(uuid);
    }

    public void startLogging(UUID uuid) {
        File dir = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data");
        if (!dir.exists())
            dir.mkdir();

        File f = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data" + File.separator + uuid + ".yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().warning("Failed to create log file for " + uuid);
            }
            YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
            c.set("dates." + 1, getTime("dd/MM/yyyy"));
            c.set("selected", 1);
            try {
                c.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
            int next = c.getInt("selected")+1;
            int max = ChatActions.getInstance().getConfig().getInt("amountOfLogs");
            if (next > max) {
                Set<String> logs = c.getConfigurationSection("logs").getKeys(false);
                Map<Integer, List<String>> data = new HashMap<>();
                Map<Integer, String> dates = new HashMap<>();
                for (int i = 1; i <= logs.size(); i++) {
                    data.put(i, c.getStringList("logs." + i));
                    dates.put(i, c.getString("dates." + i));
                    c.set("logs." + i, null);
                }

                int newid = 1;
                for (int id : data.keySet()) {
                    if (id <= 1) {
                        continue;
                    }
                    c.set("dates." + newid, dates.get(id));
                    c.set("logs." + newid, data.get(id));
                    newid++;
                }
            } else {
                if (c.getConfigurationSection("logs") != null) {
                    c.set("dates." + next, getTime("dd/MM/yyyy"));
                    c.set("selected", next);
                }
            }

            try {
                c.save(f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logging.add(uuid);
    }

    public ItemStack getLogItem(Player p, String logID) {
        List<String> logged = getLogs(p.getUniqueId(), logID);
        String firstTime = getFirstTime(p.getUniqueId(), logID);
        if (firstTime == null)
            return null;

        String title = "Giocata: " + getDate(p.getUniqueId(), logID) + " - " + firstTime;
        String pageData = "";

        NBTTagList pages = new NBTTagList();
        String lastTime = getLastTime(p.getUniqueId(), logID);
        String kingdom = getKingdom(p.getUniqueId(), logID);
        String region = getRegion(p.getUniqueId(), logID);
        pages.add(new NBTTagString("{text:\"--- " + kingdom + " ---\n--- " + region + " ---\"}"));

        pageData += "Giocata: " + getTime("dd/MM/yyyy") + "\n" + firstTime + " - " + lastTime + "\n";
        for (String line : logged) {
            line = formatting(line.replace(">>", ":"), p);
            String append = "\n" + ChatColor.RESET + line;

            if (pageData.length() >= 250) {
                pages.add(new NBTTagString("{text:\"" + pageData + "\"}"));
                pageData = append;
            } else if (pageData.length()+line.length() >= 250) {
                pages.add(new NBTTagString("{text:\"" + pageData + "\"}"));
                pageData = append;
            } else {
                pageData += append;
            }
        }
        pages.add(new NBTTagString("{text:\"" + pageData + "\"}"));

        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        net.minecraft.server.v1_12_R1.ItemStack stack = CraftItemStack.asNMSCopy(book);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("title", title);
        tag.setString("author", p.getName());
        tag.set("pages", pages);
        stack.setTag(tag);
        ItemStack is = CraftItemStack.asCraftMirror(stack);

        return is;
    }

    public List<UUID> getLogging() {
        return logging;
    }

    public int getSelected(UUID uuid) {
        File dir = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data");
        if (!dir.exists())
            dir.mkdir();

        File f = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data" + File.separator + uuid + ".yml");
        if (!f.exists())
            return 0;

        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        return c.getInt("selected");
    }

    public String getFirstTime(UUID uuid, String logID) {
        List<String> logs = getLogs(uuid, logID);
        if (logs == null || logs.size() == 0)
            return null;
        String[] data = logs.get(0).split("\\|");
        if (data[0] == null)
            return null;

        return data[0];
    }

    public String getLastTime(UUID uuid, String logID) {
        List<String> logs = getLogs(uuid, logID);
        return logs.get(logs.size()-1).split("\\|")[0];
    }

    public String getDate(UUID uuid, String logID) {
        String path = ChatActions.getInstance().getDataFolder() + File.separator + "data";
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdir();

        File f = new File(path + File.separator + uuid + ".yml");
        if (!f.exists())
            return null;

        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        return c.getString("dates." + logID);
    }

    public void writeToFile(UUID uuid, String message) {
        File dir = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data");
        if (!dir.exists())
            dir.mkdir();

        File f = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data" + File.separator + uuid + ".yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().warning("Failed to create log file for " + uuid);
            }
        }

        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        int selected = getSelected(uuid);
        List<String> currentLogs = getLogs(uuid, String.valueOf(getSelected(uuid)));
        currentLogs.add(getTime("HH:mm:ss") + "|" + message);
        c.set("logs." + selected, currentLogs);
        try {
            c.save(f);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().warning("Failed to save log file for " + uuid);
        }
    }

    public List<String> getLogs(UUID uuid, String logID) {
        File dir = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data");
        if (!dir.exists())
            dir.mkdir();

        File f = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data" + File.separator + uuid + ".yml");
        if (!f.exists())
            return new ArrayList<>();

        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        return c.getStringList("logs." + logID);
    }

    public String formatting(String msg, Player p) {
        if (ChatActions.getInstance().getConfig().getBoolean("displayTime")) {
            msg = msg.replace("|", " ");
        } else {
            msg = msg.split("\\|")[1];
        }

        String[] data = msg.split(":");
        if (!(data.length > 1))
            return msg;

        String done = data[0] + ChatColor.RESET + ":" + ChatColor.ITALIC + ChatColor.stripColor(data[1]);
        if (!ChatActions.getInstance().getConfig().getBoolean("displayname"))
            done = done.replace(p.getDisplayName(), p.getName());

        return done;
    }

    public void setRegion(UUID uuid, String region, String id) {
        File dir = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data");
        if (!dir.exists())
            dir.mkdir();

        File f = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data" + File.separator + uuid + ".yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().warning("Failed to create log file for " + uuid);
            }
        }

        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        c.set("region." + id, region);
        try {
            c.save(f);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().warning("Failed to save log file for " + uuid);
        }
    }

    public void setKingdom(UUID uuid, String kingdom, String id) {
        File dir = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data");
        if (!dir.exists())
            dir.mkdir();

        File f = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data" + File.separator + uuid + ".yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().warning("Failed to create log file for " + uuid);
            }
        }

        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        c.set("kingdom." + id, kingdom);
        try {
            c.save(f);
        } catch (IOException e) {
            Bukkit.getServer().getLogger().warning("Failed to save log file for " + uuid);
        }
    }

    public String getRegion(UUID uuid, String id) {
        File dir = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data");
        if (!dir.exists())
            dir.mkdir();

        File f = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data" + File.separator + uuid + ".yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().warning("Failed to create log file for " + uuid);
            }
        }

        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        return c.getString("region." + id);
    }

    public String getKingdom(UUID uuid, String id) {
        File dir = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data");
        if (!dir.exists())
            dir.mkdir();

        File f = new File(ChatActions.getInstance().getDataFolder() + File.separator + "data" + File.separator + uuid + ".yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                Bukkit.getServer().getLogger().warning("Failed to create log file for " + uuid);
            }
        }

        YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
        return c.getString("kingdom." + id);
    }

    public String getTime(String dateFormat) {
        Date now = new Date();
        //HH:mm:ss dd-MM-yyyy
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(now);
    }
}
