
package com.zenithmc.circlegame;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public class GameManager {
    
    private final CircleGamePlugin plugin;
    private final Map<String, Game> activeGames;
    private final List<Location> arenaLocations;
    
    public GameManager(CircleGamePlugin plugin) {
        this.plugin = plugin;
        this.activeGames = new HashMap<>();
        this.arenaLocations = new ArrayList<>();
        loadArenaLocations();
    }
    
    public boolean createGame(Player creator) {
        if (activeGames.containsKey(creator.getName())) {
            creator.sendMessage(ChatColor.RED + "Zaten aktif bir oyununuz var!");
            return false;
        }
        
        Location arenaLocation = getAvailableArena();
        if (arenaLocation == null) {
            creator.sendMessage(ChatColor.RED + "Uygun arena bulunamadı!");
            return false;
        }
        
        Game game = new Game(plugin, creator, arenaLocation);
        activeGames.put(creator.getName(), game);
        
        creator.sendMessage(ChatColor.GREEN + "Circle Game oluşturuldu! Oyuncuları davet edin: /circlegame join " + creator.getName());
        
        return true;
    }
    
    public boolean joinGame(Player player, String gameOwner) {
        Game game = activeGames.get(gameOwner);
        if (game == null) {
            player.sendMessage(ChatColor.RED + "Oyun bulunamadı!");
            return false;
        }
        
        return game.addPlayer(player);
    }
    
    public boolean startGame(String gameOwner) {
        Game game = activeGames.get(gameOwner);
        if (game == null) return false;
        
        return game.start();
    }
    
    public void removeGame(String gameOwner) {
        Game game = activeGames.remove(gameOwner);
        if (game != null) {
            game.cleanup();
        }
    }
    
    public void stopAllGames() {
        for (Game game : activeGames.values()) {
            game.cleanup();
        }
        activeGames.clear();
    }
    
    private Location getAvailableArena() {
        // Arena lokasyonlarından uygun olanı döndür
        for (Location loc : arenaLocations) {
            if (isArenaAvailable(loc)) {
                return loc;
            }
        }
        return null;
    }
    
    private boolean isArenaAvailable(Location location) {
        // Arena kullanımda mı kontrol et
        for (Game game : activeGames.values()) {
            if (game.getArenaLocation().equals(location)) {
                return false;
            }
        }
        return true;
    }
    
    private void loadArenaLocations() {
        // Config'den arena lokasyonlarını yükle
        World world = Bukkit.getWorld("world"); // Default world
        if (world != null) {
            arenaLocations.add(new Location(world, 0, 70, 0)); // Örnek lokasyon
        }
    }
}
