
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
            case "lobby":
            case "lobi":
                gameManager.joinLobby(player);
                break;
                
            case "leave":
            case "ayril":
                if (gameManager.leaveLobby(player)) {
                    player.sendMessage(ChatColor.GREEN + "Lobiden ayrıldınız!");
                } else {
                    player.sendMessage(ChatColor.RED + "Zaten lobide değilsiniz!");
                }
                break;
                
            case "parkour":
            case "parkur":
                if (gameManager.getLobbyManager().isInLobby(player)) {
                    gameManager.getLobbyManager().getParkour().startParkour(player);
                } else {
                    player.sendMessage(ChatColor.RED + "Parkura gitmek için önce lobiye katılmalısınız!");
                }
                break;
                
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
                
            case "status":
            case "durum":
                showStatus(player);
                break;
                
            case "help":
            case "yardim":
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
        player.sendMessage(ChatColor.YELLOW + "/cg lobby - Ana lobiye katıl");
        player.sendMessage(ChatColor.YELLOW + "/cg leave - Lobiden ayrıl");
        player.sendMessage(ChatColor.YELLOW + "/cg parkour - Parkura git (lobideyken)");
        player.sendMessage(ChatColor.YELLOW + "/cg status - Lobi durumunu göster");
        player.sendMessage(ChatColor.GRAY + "--- Manuel Oyun Komutları ---");
        player.sendMessage(ChatColor.YELLOW + "/cg create - Yeni oyun oluştur");
        player.sendMessage(ChatColor.YELLOW + "/cg join <oyuncu> - Oyuna katıl");
        player.sendMessage(ChatColor.YELLOW + "/cg start - Oyunu başlat");
        player.sendMessage(ChatColor.YELLOW + "/cg help - Bu yardım menüsü");
    }
    
    private void showStatus(Player player) {
        LobbyManager lobby = gameManager.getLobbyManager();
        
        player.sendMessage(ChatColor.GOLD + "=== Lobi Durumu ===");
        player.sendMessage(ChatColor.YELLOW + "Lobideki Oyuncular: " + 
                          lobby.getPlayerCount() + "/" + lobby.getRequiredPlayers());
        
        if (lobby.isInLobby(player)) {
            player.sendMessage(ChatColor.GREEN + "✓ Lobidesiniz");
            if (lobby.getParkour().isInParkour(player)) {
                player.sendMessage(ChatColor.AQUA + "➤ Şu anda parkurdasınız");
            }
        } else {
            player.sendMessage(ChatColor.RED + "✗ Lobide değilsiniz");
        }
    }
}
