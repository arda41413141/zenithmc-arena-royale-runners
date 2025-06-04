
package com.zenithmc.circlegame;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final CircleGamePlugin plugin;
    private final FileConfiguration config;
    
    public ConfigManager(CircleGamePlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        
        setupDefaultConfig();
    }
    
    private void setupDefaultConfig() {
        config.addDefault("game.loot-time", 30);
        config.addDefault("game.min-players", 6);
        config.addDefault("game.max-teams", 9);
        config.addDefault("game.players-per-team", 2);
        
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }
    
    public int getLootTime() {
        return config.getInt("game.loot-time", 30);
    }
    
    public int getMinPlayers() {
        return config.getInt("game.min-players", 6);
    }
}
