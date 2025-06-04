
package com.zenithmc.circlegame;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CircleGameCommand implements CommandExecutor {
    
    private final GameManager gameManager;
    
    public CircleGameCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Bu komut sadece oyuncular tarafından kullanılabilir!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "create":
                gameManager.createGame(player);
                break;
                
            case "join":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Kullanım: /circlegame join <oyuncu>");
                    return true;
                }
                gameManager.joinGame(player, args[1]);
                break;
                
            case "start":
                if (gameManager.startGame(player.getName())) {
                    player.sendMessage(ChatColor.GREEN + "Oyun başlatıldı!");
                } else {
                    player.sendMessage(ChatColor.RED + "Oyun başlatılamadı!");
                }
                break;
                
            case "help":
                showHelp(player);
                break;
                
            default:
                showHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Circle Game Komutları ===");
        player.sendMessage(ChatColor.YELLOW + "/circlegame create - Yeni oyun oluştur");
        player.sendMessage(ChatColor.YELLOW + "/circlegame join <oyuncu> - Oyuna katıl");
        player.sendMessage(ChatColor.YELLOW + "/circlegame start - Oyunu başlat");
        player.sendMessage(ChatColor.YELLOW + "/circlegame help - Bu yardım menüsü");
    }
}
