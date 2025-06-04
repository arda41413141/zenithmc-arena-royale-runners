
package com.zenithmc.circlegame;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.*;

public class LobbyParkour {
    
    private final CircleGamePlugin plugin;
    private final Map<Player, Long> playerTimes;
    private final Map<Player, Location> playerCheckpoints;
    private final List<Location> checkpoints;
    private Location startLocation;
    private Location endLocation;
    
    public LobbyParkour(CircleGamePlugin plugin) {
        this.plugin = plugin;
        this.playerTimes = new HashMap<>();
        this.playerCheckpoints = new HashMap<>();
        this.checkpoints = new ArrayList<>();
    }
    
    public void setupParkourArea(Location startLoc) {
        this.startLocation = startLoc.clone();
        World world = startLoc.getWorld();
        
        // Parkur rotasını oluştur
        createParkourCourse(world, startLoc);
        
        // Bitiş noktası
        this.endLocation = startLoc.clone().add(0, 10, 30);
        endLocation.getBlock().setType(Material.EMERALD_BLOCK);
        
        // Başlangıç işareti
        Location signLoc = startLoc.clone().add(0, 1, -1);
        signLoc.getBlock().setType(Material.OAK_SIGN);
    }
    
    private void createParkourCourse(World world, Location start) {
        // Basit parkur rotası oluştur
        Location current = start.clone();
        
        // 1. Bölüm: Zıplama platformları
        for (int i = 0; i < 10; i++) {
            current.add(0, 0, 3);
            if (i % 2 == 0) current.add(0, 1, 0);
            else current.add(0, -1, 0);
            
            current.getBlock().setType(Material.STONE);
            
            if (i == 4) {
                checkpoints.add(current.clone());
                current.clone().add(0, 1, 0).getBlock().setType(Material.GOLD_BLOCK);
            }
        }
        
        // 2. Bölüm: Merdiven bölümü
        for (int i = 0; i < 5; i++) {
            current.add(1, 1, 1);
            current.getBlock().setType(Material.COBBLESTONE);
        }
        
        // Checkpoint 2
        checkpoints.add(current.clone());
        current.clone().add(0, 1, 0).getBlock().setType(Material.GOLD_BLOCK);
        
        // 3. Bölüm: Su üzerinde zıplama
        for (int i = 0; i < 8; i++) {
            current.add(-1, -2, 2);
            current.getBlock().setType(Material.LILY_PAD);
            current.clone().add(0, -1, 0).getBlock().setType(Material.WATER);
        }
        
        // Final checkpoint
        checkpoints.add(current.clone());
        current.clone().add(0, 1, 0).getBlock().setType(Material.GOLD_BLOCK);
    }
    
    public void startParkour(Player player) {
        player.teleport(startLocation);
        playerTimes.put(player, System.currentTimeMillis());
        playerCheckpoints.put(player, startLocation.clone());
        
        player.sendMessage(ChatColor.GREEN + "Parkur başladı! İyi şanslar!");
        player.sendMessage(ChatColor.YELLOW + "Altın bloklar checkpoint'ler. Düşersen oraya geri döneceksin.");
        
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(false);
    }
    
    public void handleCheckpoint(Player player, Location checkpoint) {
        if (!playerTimes.containsKey(player)) return;
        
        playerCheckpoints.put(player, checkpoint);
        player.sendMessage(ChatColor.GOLD + "Checkpoint kaydedildi!");
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }
    
    public void handlePlayerFall(Player player) {
        if (!playerCheckpoints.containsKey(player)) return;
        
        Location checkpoint = playerCheckpoints.get(player);
        player.teleport(checkpoint);
        player.sendMessage(ChatColor.RED + "Düştün! Son checkpoint'e geri döndün.");
    }
    
    public void finishParkour(Player player) {
        if (!playerTimes.containsKey(player)) return;
        
        long startTime = playerTimes.remove(player);
        long completionTime = System.currentTimeMillis() - startTime;
        double seconds = completionTime / 1000.0;
        
        playerCheckpoints.remove(player);
        
        player.sendMessage(ChatColor.GOLD + "Tebrikler! Parkuru tamamladın!");
        player.sendMessage(ChatColor.YELLOW + "Süren: " + String.format("%.2f", seconds) + " saniye");
        
        // Ödül ver
        ItemStack reward = new ItemStack(Material.GOLDEN_APPLE);
        player.getInventory().addItem(reward);
        
        // Başarı efekti
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 20);
        
        // 3 saniye sonra lobi'ye geri gönder
        new BukkitRunnable() {
            @Override
            public void run() {
                Location lobbySpawn = new Location(player.getWorld(), 0, 100, 0);
                player.teleport(lobbySpawn);
                player.sendMessage(ChatColor.GREEN + "Lobi'ye geri döndün!");
            }
        }.runTaskLater(plugin, 60L);
    }
    
    public ItemStack getParkourCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Parkura Git");
        List<String> lore = Arrays.asList(
            ChatColor.GRAY + "Sağ tık yaparak parkura git!",
            ChatColor.YELLOW + "Oyun başlayana kadar eğlen!"
        );
        meta.setLore(lore);
        compass.setItemMeta(meta);
        return compass;
    }
    
    public boolean isInParkour(Player player) {
        return playerTimes.containsKey(player);
    }
    
    public void resetPlayer(Player player) {
        playerTimes.remove(player);
        playerCheckpoints.remove(player);
    }
    
    public Location getStartLocation() {
        return startLocation;
    }
}
