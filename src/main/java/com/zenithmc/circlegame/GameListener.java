
package com.zenithmc.circlegame;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class GameListener implements Listener {
    
    private final GameManager gameManager;
    
    public GameListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        gameManager.handlePlayerQuit(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        LobbyManager lobby = gameManager.getLobbyManager();
        
        // Parkur pusulası
        if (displayName.equals(ChatColor.GREEN + "Parkura Git")) {
            if (lobby.isInLobby(player)) {
                lobby.getParkour().startParkour(player);
            } else {
                player.sendMessage(ChatColor.RED + "Lobide değilsiniz!");
            }
            event.setCancelled(true);
        }
        
        // Lobiyi terk et
        else if (displayName.equals(ChatColor.RED + "Lobiyi Terk Et")) {
            if (lobby.leaveLobby(player)) {
                player.sendMessage(ChatColor.GREEN + "Lobiden ayrıldınız!");
            }
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        LobbyManager lobby = gameManager.getLobbyManager();
        
        // Parkur kontrolleri
        if (lobby.getParkour().isInParkour(player)) {
            Location loc = player.getLocation();
            
            // Parkur bitiş kontrolü
            Location endLoc = lobby.getParkour().getStartLocation().clone().add(0, 10, 30);
            if (loc.distance(endLoc) < 2) {
                lobby.getParkour().finishParkour(player);
                return;
            }
            
            // Checkpoint kontrolü
            if (loc.getBlock().getType() == Material.GOLD_BLOCK) {
                lobby.getParkour().handleCheckpoint(player, loc);
            }
            
            // Düşme kontrolü (Y < 90)
            if (loc.getY() < 90) {
                lobby.getParkour().handlePlayerFall(player);
            }
        }
    }
}
