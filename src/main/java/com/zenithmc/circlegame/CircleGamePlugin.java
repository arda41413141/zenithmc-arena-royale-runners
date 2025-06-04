
package com.zenithmc.circlegame;

import org.bukkit.plugin.java.JavaPlugin;

public class CircleGamePlugin extends JavaPlugin {
    
    private GameManager gameManager;
    private ConfigManager configManager;
    
    @Override
    public void onEnable() {
        getLogger().info("ZenithMC Circle Game Plugin (Lobi Sistemi ile) yüklendi!");
        
        // Config dosyasını oluştur
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        
        // Game Manager'ı başlat (lobi sistemi dahil)
        gameManager = new GameManager(this);
        
        // Komutları kaydet
        getCommand("circlegame").setExecutor(new CircleGameCommand(gameManager));
        
        // Event listener'ları kaydet
        getServer().getPluginManager().registerEvents(new GameListener(gameManager), this);
        
        getLogger().info("Circle Game sistemi aktif! Oyuncular /cg lobby ile katılabilir!");
        getLogger().info("Minimum oyuncu sayısı: " + configManager.getMinPlayers());
    }
    
    @Override
    public void onDisable() {
        if (gameManager != null) {
            gameManager.stopAllGames();
        }
        getLogger().info("Circle Game Plugin kapatıldı!");
    }
    
    public GameManager getGameManager() {
        return gameManager;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
}
