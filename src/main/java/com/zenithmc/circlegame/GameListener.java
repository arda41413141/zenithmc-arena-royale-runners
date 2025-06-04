
package com.zenithmc.circlegame;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameListener implements Listener {
    
    private final GameManager gameManager;
    
    public GameListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Oyuncu çıktığında oyundan çıkar
        // Bu kısım daha detaylı implemente edilebilir
    }
}
