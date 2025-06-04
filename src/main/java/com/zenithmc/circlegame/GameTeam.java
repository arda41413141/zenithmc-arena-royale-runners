
package com.zenithmc.circlegame;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class GameTeam {
    
    private final String name;
    private final ChatColor color;
    private final List<Player> players;
    private boolean isEliminated;
    
    public GameTeam(String name, ChatColor color) {
        this.name = name;
        this.color = color;
        this.players = new ArrayList<>();
        this.isEliminated = false;
    }
    
    public void addPlayer(Player player) {
        if (players.size() < 2) {
            players.add(player);
        }
    }
    
    public void removePlayer(Player player) {
        players.remove(player);
    }
    
    public String getName() {
        return color + name;
    }
    
    public ChatColor getColor() {
        return color;
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public boolean isEliminated() {
        return isEliminated;
    }
    
    public void setEliminated(boolean eliminated) {
        isEliminated = eliminated;
    }
    
    public boolean isFull() {
        return players.size() >= 2;
    }
}
