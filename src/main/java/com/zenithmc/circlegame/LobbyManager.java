
package com.zenithmc.circlegame;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public class LobbyManager {
    
    private final CircleGamePlugin plugin;
    private final Set<Player> lobbyPlayers;
    private final Location lobbySpawn;
    private final LobbyParkour parkour;
    private boolean isLobbyActive;
    private int requiredPlayers;
    private int countdown;
    private BukkitRunnable countdownTask;
    
    public LobbyManager(CircleGamePlugin plugin) {
        this.plugin = plugin;
        this.lobbyPlayers = new HashSet<>();
        this.parkour = new LobbyParkour(plugin);
        this.isLobbyActive = false;
        this.requiredPlayers = 6; // Minimum oyuncu sayısı
        this.countdown = 30;
        
        // Lobi spawn lokasyonunu ayarla
        World world = Bukkit.getWorld("world");
        this.lobbySpawn = new Location(world, 0, 100, 0);
        setupLobbyArea();
    }
    
    public boolean joinLobby(Player player) {
        if (lobbyPlayers.contains(player)) {
            player.sendMessage(ChatColor.RED + "Zaten lobidesiniz!");
            return false;
        }
        
        lobbyPlayers.add(player);
        teleportToLobby(player);
        
        broadcastToLobby(ChatColor.GREEN + player.getName() + " lobiye katıldı! (" + 
                        lobbyPlayers.size() + "/" + requiredPlayers + ")");
        
        // Yeterli oyuncu varsa countdown başlat
        if (lobbyPlayers.size() >= requiredPlayers && !isLobbyActive) {
            startCountdown();
        }
        
        // Parkur bilgisini göster
        player.sendMessage(ChatColor.YELLOW + "Oyun başlayana kadar parkurda pratik yapabilirsiniz!");
        player.sendMessage(ChatColor.GOLD + "Parkur başlangıcı: /cg parkour");
        
        return true;
    }
    
    public boolean leaveLobby(Player player) {
        if (!lobbyPlayers.contains(player)) {
            return false;
        }
        
        lobbyPlayers.remove(player);
        player.teleport(player.getWorld().getSpawnLocation());
        
        broadcastToLobby(ChatColor.YELLOW + player.getName() + " lobiyi terk etti! (" + 
                        lobbyPlayers.size() + "/" + requiredPlayers + ")");
        
        // Yetersiz oyuncu varsa countdown'u durdur
        if (lobbyPlayers.size() < requiredPlayers && isLobbyActive) {
            stopCountdown();
        }
        
        return true;
    }
    
    private void startCountdown() {
        isLobbyActive = true;
        countdown = 30;
        
        broadcastToLobby(ChatColor.GREEN + "Yeterli oyuncu toplandı! Oyun " + countdown + " saniye sonra başlayacak!");
        
        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                countdown--;
                
                if (lobbyPlayers.size() < requiredPlayers) {
                    stopCountdown();
                    return;
                }
                
                if (countdown <= 0) {
                    startGame();
                    cancel();
                } else if (countdown <= 10 || countdown % 10 == 0) {
                    broadcastToLobby(ChatColor.YELLOW + "Oyun " + countdown + " saniye sonra başlayacak!");
                    
                    // Son 5 saniyede title göster
                    if (countdown <= 5) {
                        for (Player player : lobbyPlayers) {
                            player.sendTitle(ChatColor.RED + String.valueOf(countdown), 
                                           ChatColor.YELLOW + "Hazır olun!", 0, 20, 0);
                        }
                    }
                }
            }
        };
        countdownTask.runTaskTimer(plugin, 0L, 20L);
    }
    
    private void stopCountdown() {
        isLobbyActive = false;
        if (countdownTask != null) {
            countdownTask.cancel();
        }
        broadcastToLobby(ChatColor.RED + "Yetersiz oyuncu! Countdown durduruldu.");
    }
    
    private void startGame() {
        if (lobbyPlayers.size() < requiredPlayers) {
            stopCountdown();
            return;
        }
        
        broadcastToLobby(ChatColor.GOLD + "=== OYUN BAŞLIYOR! ===");
        
        // GameManager'a oyuncuları gönder
        List<Player> playerList = new ArrayList<>(lobbyPlayers);
        boolean gameStarted = plugin.getGameManager().createAndStartGame(playerList);
        
        if (gameStarted) {
            // Lobi'yi temizle
            lobbyPlayers.clear();
            isLobbyActive = false;
        } else {
            broadcastToLobby(ChatColor.RED + "Oyun başlatılamadı! Tekrar deneyin.");
            stopCountdown();
        }
    }
    
    private void teleportToLobby(Player player) {
        player.teleport(lobbySpawn);
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.getInventory().clear();
        
        // Lobi için özel itemler ver
        player.getInventory().setItem(0, parkour.getParkourCompass());
        player.getInventory().setItem(8, createLeaveLobbyItem());
    }
    
    private void setupLobbyArea() {
        // Lobi platformunu oluştur
        World world = lobbySpawn.getWorld();
        Location center = lobbySpawn.clone();
        
        // 20x20'lik platform oluştur
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                Location blockLoc = center.clone().add(x, -1, z);
                blockLoc.getBlock().setType(Material.STONE_BRICKS);
                
                // Kenarları duvar yap
                if (Math.abs(x) == 10 || Math.abs(z) == 10) {
                    blockLoc.add(0, 1, 0).getBlock().setType(Material.STONE_BRICK_WALL);
                }
            }
        }
        
        // Merkeze bilgi tabelası
        Location signLoc = center.clone().add(0, 1, 0);
        signLoc.getBlock().setType(Material.OAK_SIGN);
        
        // Parkur alanını oluştur
        parkour.setupParkourArea(center.clone().add(15, 0, 0));
    }
    
    private org.bukkit.inventory.ItemStack createLeaveLobbyItem() {
        org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(Material.RED_BED);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Lobiyi Terk Et");
        item.setItemMeta(meta);
        return item;
    }
    
    private void broadcastToLobby(String message) {
        for (Player player : lobbyPlayers) {
            player.sendMessage(message);
        }
    }
    
    public void handlePlayerQuit(Player player) {
        leaveLobby(player);
    }
    
    public boolean isInLobby(Player player) {
        return lobbyPlayers.contains(player);
    }
    
    public int getPlayerCount() {
        return lobbyPlayers.size();
    }
    
    public int getRequiredPlayers() {
        return requiredPlayers;
    }
    
    public void setRequiredPlayers(int count) {
        this.requiredPlayers = count;
    }
    
    public LobbyParkour getParkour() {
        return parkour;
    }
}
