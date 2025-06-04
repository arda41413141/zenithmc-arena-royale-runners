
package com.zenithmc.circlegame;

import org.bukkit.Location;
import org.bukkit.Material;

public class LootItem {
    
    private final Material material;
    private final Location location;
    private final String name;
    private final Rarity rarity;
    
    public enum Rarity {
        COMMON, RARE, EPIC, LEGENDARY
    }
    
    public LootItem(Material material, Location location) {
        this.material = material;
        this.location = location;
        this.name = generateName(material);
        this.rarity = generateRarity(material);
    }
    
    private String generateName(Material material) {
        switch (material) {
            case DIAMOND_SWORD: return "Diamond Kılıç";
            case GOLDEN_APPLE: return "Altın Elma";
            case IRON_CHESTPLATE: return "Demir Zırh";
            case HEALING_POTION: return "İyileştirme İksiri";
            case BOW: return "Büyülü Yay";
            case SHIELD: return "Kalkan";
            default: return material.name();
        }
    }
    
    private Rarity generateRarity(Material material) {
        switch (material) {
            case DIAMOND_SWORD: return Rarity.LEGENDARY;
            case GOLDEN_APPLE: return Rarity.EPIC;
            case IRON_CHESTPLATE: return Rarity.RARE;
            case BOW: return Rarity.EPIC;
            default: return Rarity.COMMON;
        }
    }
    
    public Material getMaterial() { return material; }
    public Location getLocation() { return location; }
    public String getName() { return name; }
    public Rarity getRarity() { return rarity; }
}
