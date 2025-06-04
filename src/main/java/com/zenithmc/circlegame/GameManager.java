
package com.zenithmc.circlegame;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public class GameManager {
    
    private final CircleGamePlugin plugin;
    private final Map<String, Game> activeGames;
    private final List<Location> arenaLocations;
    private final LobbyManager lobbyManager;
    
    public GameManager(CircleGamePlugin plugin) {
        this.plugin = plugin;
        this.activeGames = new HashMap<>();
        this.arenaLocations = new ArrayList<>();
        this.lobbyManager = new LobbyManager(plugin);
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
    
    // Lobi sisteminden otomatik oyun başlatma
    public boolean createAndStartGame(List<Player> players) {
        if (players.size() < 6) {
            return false;
        }
        
        Location arenaLocation = getAvailableArena();
        if (arenaLocation == null) {
            return false;
        }
        
        // İlk oyuncuyu creator olarak ayarla
        Player creator = players.get(0);
        Game game = new Game(plugin, creator, arenaLocation);
        activeGames.put(creator.getName(), game);
        
        // Tüm oyuncuları oyuna ekle
        for (Player player : players) {
            game.addPlayer(player);
        }
        
        // Oyunu başlat
        return game.start();
    }
    
    public boolean joinGame(Player player, String gameOwner) {
        Game game = activeGames.get(gameOwner);
        if (game == null) {
            player.sendMessage(ChatColor.RED + "Oyun bulunamadı!");
            return false;
        }
        
        return game.addPlayer(player);
    }
    
    public boolean joinLobby(Player player) {
        return lobbyManager.joinLobby(player);
    }
    
    public boolean leaveLobby(Player player) {
        return lobbyManager.leaveLobby(player);
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
    
    public void handlePlayerQuit(Player player) {
        lobbyManager.handlePlayerQuit(player);
        
        // Aktif oyunlardan da çıkar
        for (Game game : activeGames.values()) {
            // Bu kısım Game sınıfında implement edilmeli
        }
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
            arenaLocations.add(new Location(world, 100, 70, 100)); // Arena lokasyonu
            arenaLocations.add(new Location(world, -100, 70, -100)); // Ek arena
        }
    }
    
    public LobbyManager getLobbyManager() {
        return lobbyManager;
    }
    
    public boolean isPlayerInGame(Player player) {
        for (Game game : activeGames.values()) {
            // Bu kontrol Game sınıfında implement edilmeli
        }
        return lobbyManager.isInLobby(player);
    }
}
