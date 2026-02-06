package me.bartus47.multik;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TabListManager {

    private final Multik plugin;
    private final GuildManager guildManager;
    private final PlayerManager playerManager;

    public TabListManager(Multik plugin, GuildManager guildManager, PlayerManager playerManager) {
        this.plugin = plugin;
        this.guildManager = guildManager;
        this.playerManager = playerManager;
    }

    public void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    updateTab(player);
                }
            }
        }.runTaskTimer(plugin, 20L, 40L);
    }

    private void updateTab(Player player) {
        String header = "\n" + ChatColor.AQUA + "" + ChatColor.BOLD + "KozMc" + "\n"
                + ChatColor.GRAY + player.getName() + " | " + ChatColor.RED + "Kills: " + playerManager.getKills(player.getUniqueId())
                + " | " + ChatColor.GREEN + "Ping: " + player.getPing() + "ms\n";

        Gildie guild = guildManager.getGuildByPlayer(player.getUniqueId());
        if (guild != null) {
            header += ChatColor.YELLOW + "Guild: " + ChatColor.GOLD + guild.getName() + " [" + guild.getTag().toUpperCase() + "] "
                    + ChatColor.WHITE + "(" + guild.getPoints() + " pkt)\n";
        } else {
            header += ChatColor.RED + "No Guild\n";
        }

        // Reverted: Footer columns now have all three titles
        String footer = "\n" + ChatColor.DARK_GRAY + "----------------------------------------------------------\n"
                + ChatColor.AQUA + "Online Players      " + ChatColor.GOLD + "Top Teams           " + ChatColor.RED + "Top Kills\n";

        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        List<Gildie> topGuilds = new ArrayList<>(guildManager.getGuilds());
        topGuilds.sort((g1, g2) -> Integer.compare(g2.getPoints(), g1.getPoints()));

        LinkedHashMap<String, Integer> topKillers = playerManager.getTopKillers(10);
        List<Map.Entry<String, Integer>> killerEntries = new ArrayList<>(topKillers.entrySet());

        for (int i = 0; i < 10; i++) {
            String col1 = (i < onlinePlayers.size()) ? ChatColor.WHITE + onlinePlayers.get(i).getName() : "";

            String col2 = "";
            if (i < topGuilds.size()) {
                Gildie g = topGuilds.get(i);
                col2 = ChatColor.GRAY + String.valueOf(i + 1) + ". " + ChatColor.YELLOW + g.getTag().toUpperCase() + " " + ChatColor.WHITE + g.getPoints();
            }

            String col3 = "";
            if (i < killerEntries.size()) {
                Map.Entry<String, Integer> entry = killerEntries.get(i);
                col3 = ChatColor.GRAY + String.valueOf(i + 1) + ". " + ChatColor.YELLOW + entry.getKey() +
                        ChatColor.WHITE + " [" + ChatColor.RED + entry.getValue() + ChatColor.WHITE + "]";
            }

            if (col1.isEmpty() && col2.isEmpty() && col3.isEmpty()) break;

            footer += String.format("%-20s %-25s %-20s\n", col1, col2, col3);
        }

        footer += ChatColor.DARK_GRAY + "----------------------------------------------------------\n";
        player.setPlayerListHeaderFooter(header, footer);
    }
}