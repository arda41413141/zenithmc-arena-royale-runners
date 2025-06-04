
package com.zenithmc.circlegame;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    
    private final CircleGamePlugin plugin;
    private final FileConfiguration config;
    
    public ConfigManager(CircleGamePlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        
        // Default değerleri ayarla
        setDefaults();
    }
    
    private void setDefaults() {
        config.addDefault("lobby.min-players", 6);
        config.addDefault("lobby.max-players", 18);
        config.addDefault("lobby.countdown-time", 30);
        config.addDefault("lobby.spawn.world", "world");
        config.addDefault("lobby.spawn.x", 0);
        config.addDefault("lobby.spawn.y", 100);
        config.addDefault("lobby.spawn.z", 0);
        
        config.addDefault("parkour.reward-item", "GOLDEN_APPLE");
        config.addDefault("parkour.start-message", "Parkur başladı! İyi şanslar!");
        config.addDefault("parkour.checkpoint-message", "Checkpoint kaydedildi!");
        
        config.addDefault("game.loot-time", 30);
        config.addDefault("game.teams", 9);
        config.addDefault("game.players-per-team", 2);
        
        config.options().copyDefaults(true);
        plugin.saveConfig();
    }
    
    public int getMinPlayers() {
        return config.getInt("lobby.min-players", 6);
    }
    
    public int getMaxPlayers() {
        return config.getInt("lobby.max-players", 18);
    }
    
    public int getCountdownTime() {
        return config.getInt("lobby.countdown-time", 30);
    }
    
    public Location getLobbySpawn() {
        String worldName = config.getString("lobby.spawn.world", "world");
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }
        
        double x = config.getDouble("lobby.spawn.x", 0);
        double y = config.getDouble("lobby.spawn.y", 100);
        double z = config.getDouble("lobby.spawn.z", 0);
        
        return new Location(world, x, y, z);
    }
    
    public int getLootTime() {
        return config.getInt("game.loot-time", 30);
    }
    
    public int getTeamCount() {
        return config.getInt("game.teams", 9);
    }
    
    public int getPlayersPerTeam() {
        return config.getInt("game.players-per-team", 2);
    }
    
    public String getParkourStartMessage() {
        return config.getString("parkour.start-message", "Parkur başladı! İyi şanslar!");
    }
    
    public String getCheckpointMessage() {
        return config.getString("parkour.checkpoint-message", "Checkpoint kaydedildi!");
    }
}
