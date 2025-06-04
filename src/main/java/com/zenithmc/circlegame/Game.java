
package com.zenithmc.circlegame;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public class Game {
    
    private final CircleGamePlugin plugin;
    private final Player creator;
    private final Location arenaLocation;
    private final List<GameTeam> teams;
    private final List<GameTeam> activeTeams;
    private final Map<Location, LootItem> lootItems;
    
    private GamePhase currentPhase;
    private int currentRound;
    private int timeLeft;
    private BukkitRunnable gameTask;
    
    public enum GamePhase {
        WAITING, LOOTING, FIGHTING, FINAL, ENDED
    }
    
    public Game(CircleGamePlugin plugin, Player creator, Location arenaLocation) {
        this.plugin = plugin;
        this.creator = creator;
        this.arenaLocation = arenaLocation;
        this.teams = new ArrayList<>();
        this.activeTeams = new ArrayList<>();
        this.lootItems = new HashMap<>();
        this.currentPhase = GamePhase.WAITING;
        this.currentRound = 1;
        this.timeLeft = 30;
        
        initializeTeams();
    }
    
    public boolean addPlayer(Player player) {
        if (currentPhase != GamePhase.WAITING) {
            player.sendMessage(ChatColor.RED + "Oyun zaten başladı!");
            return false;
        }
        
        // Boş takım bul ve oyuncuyu ekle
        for (GameTeam team : teams) {
            if (team.getPlayers().size() < 2) {
                team.addPlayer(player);
                player.sendMessage(ChatColor.GREEN + team.getName() + " takımına katıldınız!");
                broadcastToGame(ChatColor.YELLOW + player.getName() + " oyuna katıldı! (" + team.getName() + ")");
                
                // Oyuncuyu arenaya ışınla
                teleportToArena(player);
                
                if (getAllPlayerCount() >= 18) {
                    broadcastToGame(ChatColor.GREEN + "Oyun dolu! Başlatılıyor...");
                    start();
                }
                return true;
            }
        }
        
        player.sendMessage(ChatColor.RED + "Oyun dolu!");
        return false;
    }
    
    public boolean start() {
        if (getAllPlayerCount() < 6) {
            creator.sendMessage(ChatColor.RED + "En az 6 oyuncu gerekli!");
            return false;
        }
        
        currentPhase = GamePhase.LOOTING;
        activeTeams.clear();
        for (GameTeam team : teams) {
            if (!team.getPlayers().isEmpty()) {
                activeTeams.add(team);
            }
        }
        
        broadcastToGame(ChatColor.GOLD + "=== CIRCLE GAME BAŞLADI! ===");
        broadcastToGame(ChatColor.YELLOW + "Round " + currentRound + " - LOOT ZAMANΙ!");
        
        startLootingPhase();
        return true;
    }
    
    private void startLootingPhase() {
        timeLeft = 30;
        generateLoot();
        
        gameTask = new BukkitRunnable() {
            @Override
            public void run() {
                timeLeft--;
                
                if (timeLeft <= 0) {
                    startFightingPhase();
                    cancel();
                }
                
                // Her saniye title göster
                String title = ChatColor.RED + "LOOT ZAMANΙ: " + timeLeft;
                for (GameTeam team : activeTeams) {
                    for (Player player : team.getPlayers()) {
                        player.sendTitle(title, ChatColor.YELLOW + "Ortadaki lootları al!", 0, 20, 0);
                    }
                }
            }
        };
        gameTask.runTaskTimer(plugin, 0L, 20L);
    }
    
    private void startFightingPhase() {
        currentPhase = GamePhase.FIGHTING;
        clearLoot();
        
        broadcastToGame(ChatColor.RED + "SAVAŞ ZAMANΙ!");
        
        // 3 saniye sonra eleme yap
        new BukkitRunnable() {
            @Override
            public void run() {
                eliminateTeams();
                currentRound++;
                
                if (activeTeams.size() <= 2) {
                    startFinalPhase();
                } else {
                    // 2 saniye sonra yeni round başlat
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            currentPhase = GamePhase.LOOTING;
                            startLootingPhase();
                        }
                    }.runTaskLater(plugin, 40L);
                }
            }
        }.runTaskLater(plugin, 60L);
    }
    
    private void startFinalPhase() {
        currentPhase = GamePhase.FINAL;
        broadcastToGame(ChatColor.GOLD + "=== FİNAL SAVAŞI ===");
        
        // 5 saniye sonra kazananı belirle
        new BukkitRunnable() {
            @Override
            public void run() {
                GameTeam winner = activeTeams.get(new Random().nextInt(activeTeams.size()));
                endGame(winner);
            }
        }.runTaskLater(plugin, 100L);
    }
    
    private void endGame(GameTeam winnerTeam) {
        currentPhase = GamePhase.ENDED;
        
        broadcastToGame(ChatColor.GOLD + "=== OYUN BİTTİ ===");
        broadcastToGame(ChatColor.GREEN + "KAZANAN: " + winnerTeam.getName());
        
        // Kazanan oyuncuları göster
        for (Player player : winnerTeam.getPlayers()) {
            broadcastToGame(ChatColor.YELLOW + "🏆 " + player.getName() + " (Champion)");
            
            // Ödül ver
            player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 5));
            player.sendMessage(ChatColor.GREEN + "Kazandığınız için 5 Golden Apple aldınız!");
        }
        
        // 10 saniye sonra oyunu temizle
        new BukkitRunnable() {
            @Override
            public void run() {
                cleanup();
            }
        }.runTaskLater(plugin, 200L);
    }
    
    private void generateLoot() {
        lootItems.clear();
        
        Material[] lootTypes = {
            Material.DIAMOND_SWORD, Material.GOLDEN_APPLE, Material.IRON_CHESTPLATE,
            Material.HEALING_POTION, Material.BOW, Material.SHIELD
        };
        
        int lootCount = 3 + new Random().nextInt(4); // 3-6 loot
        
        for (int i = 0; i < lootCount; i++) {
            double angle = (Math.PI * 2 * i) / lootCount;
            double radius = 10 + new Random().nextDouble() * 15;
            
            Location lootLoc = arenaLocation.clone().add(
                Math.cos(angle) * radius,
                1,
                Math.sin(angle) * radius
            );
            
            Material lootType = lootTypes[new Random().nextInt(lootTypes.length)];
            LootItem loot = new LootItem(lootType, lootLoc);
            lootItems.put(lootLoc, loot);
            
            // Loot'u dünyaya bırak
            lootLoc.getWorld().dropItem(lootLoc, new ItemStack(lootType));
        }
    }
    
    private void clearLoot() {
        for (Location loc : lootItems.keySet()) {
            loc.getWorld().getNearbyEntities(loc, 1, 1, 1).forEach(entity -> {
                if (entity.getType().toString().contains("DROPPED_ITEM")) {
                    entity.remove();
                }
            });
        }
        lootItems.clear();
    }
    
    private void eliminateTeams() {
        int teamsToEliminate = Math.max(1, (int) Math.ceil(activeTeams.size() * 0.3));
        
        for (int i = 0; i < teamsToEliminate && activeTeams.size() > 2; i++) {
            GameTeam eliminated = activeTeams.remove(new Random().nextInt(activeTeams.size()));
            broadcastToGame(ChatColor.RED + eliminated.getName() + " elendi!");
            
            // Elenen oyuncuları spawn'a gönder
            for (Player player : eliminated.getPlayers()) {
                player.teleport(player.getWorld().getSpawnLocation());
                player.sendMessage(ChatColor.RED + "Takımınız elendi!");
            }
        }
    }
    
    private void initializeTeams() {
        ChatColor[] colors = {
            ChatColor.RED, ChatColor.BLUE, ChatColor.GREEN, ChatColor.YELLOW,
            ChatColor.PURPLE, ChatColor.AQUA, ChatColor.ORANGE, ChatColor.PINK, ChatColor.GRAY
        };
        
        for (int i = 0; i < 9; i++) {
            teams.add(new GameTeam("Takım " + (i + 1), colors[i % colors.length]));
        }
    }
    
    private void teleportToArena(Player player) {
        Location spawnLoc = arenaLocation.clone().add(0, 2, 0);
        player.teleport(spawnLoc);
    }
    
    private void broadcastToGame(String message) {
        for (GameTeam team : teams) {
            for (Player player : team.getPlayers()) {
                player.sendMessage(message);
            }
        }
    }
    
    private int getAllPlayerCount() {
        return teams.stream().mapToInt(team -> team.getPlayers().size()).sum();
    }
    
    public void cleanup() {
        if (gameTask != null) {
            gameTask.cancel();
        }
        
        clearLoot();
        
        // Tüm oyuncuları spawn'a gönder
        for (GameTeam team : teams) {
            for (Player player : team.getPlayers()) {
                player.teleport(player.getWorld().getSpawnLocation());
            }
        }
    }
    
    public Location getArenaLocation() {
        return arenaLocation;
    }
}
