package me.bartus47.multik;

import org.bukkit.plugin.java.JavaPlugin;

public final class Multik extends JavaPlugin {

    private GuildManager guildManager;
    private MarketManager marketManager;
    private NameTagManager nameTagManager;
    private PlayerManager playerManager;
    private TabListManager tabListManager;
    private GuildInventoryManager guildInventoryManager;
    private ChestConfigManager chestConfigManager;
    private LootManager lootManager;

    @Override
    public void onEnable() {
        // --- 1. CORE DATA & PLAYER STATS ---
        this.guildManager = new GuildManager(this);
        this.playerManager = new PlayerManager(this);
        this.guildInventoryManager = new GuildInventoryManager(this);

        // --- 2. GILDIE ---
        TeamCommand teamCmd = new TeamCommand(this, guildManager);
        getCommand("team").setExecutor(teamCmd);
        getCommand("team").setTabCompleter(teamCmd);

        getServer().getPluginManager().registerEvents(new GuildListener(guildManager, this), this);
        getServer().getPluginManager().registerEvents(new PointsListener(guildManager), this);
        getServer().getPluginManager().registerEvents(new GuildInventoryListener(), this);

        // --- 3. RYNEK ---
        this.marketManager = new MarketManager(this);
        MarketCommand marketCmd = new MarketCommand(marketManager, guildManager);
        getCommand("rynek").setExecutor(marketCmd);
        getCommand("rynek").setTabCompleter(marketCmd);

        getServer().getPluginManager().registerEvents(new MarketListener(marketManager, guildManager), this);

        // --- 4. LOOT CHESTS ---
        this.chestConfigManager = new ChestConfigManager(this);
        this.lootManager = new LootManager(this, chestConfigManager);

        // Register the updated command executor with Tab Completion
        ChestCommand chestCmd = new ChestCommand(this, chestConfigManager);
        getCommand("chests").setExecutor(chestCmd);
        getCommand("chests").setTabCompleter(chestCmd);

        // Register the listener that handles chest claims and hologram removal
        getServer().getPluginManager().registerEvents(new LootInteractionListener(this), this);

        // --- 5. VISUALS (NAMETAGS & TAB) ---
        this.nameTagManager = new NameTagManager(this, guildManager);
        nameTagManager.startTask();
        getServer().getPluginManager().registerEvents(nameTagManager, this);

        this.tabListManager = new TabListManager(this, guildManager, playerManager);
        tabListManager.startTask();

        getLogger().info("Multik (Gildie + Rynek + Tab + Item + Chests) wlaczony!");
    }

    @Override
    public void onDisable() {
        if (guildManager != null) guildManager.saveGuilds();
        if (marketManager != null) marketManager.saveMarketData();
        if (guildInventoryManager != null) guildInventoryManager.saveInventories();
    }

    // Getters for other classes to access managers
    public GuildManager getGuildManager() { return guildManager; }
    public PlayerManager getPlayerManager() { return playerManager; }
    public GuildInventoryManager getGuildInventoryManager() { return guildInventoryManager; }
    public ChestConfigManager getChestConfigManager() { return chestConfigManager; }
    public LootManager getLootManager() { return lootManager; }
}