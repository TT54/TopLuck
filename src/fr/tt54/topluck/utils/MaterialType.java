package fr.tt54.topluck.utils;

import org.bukkit.Material;

public class MaterialType {

    private Material type;
    private int data;
    private String displayName;

    public MaterialType(String materialName, int data) {
        this.type = Material.getMaterial(materialName);
        this.data = data;
        this.displayName = this.type.name();
    }

    public MaterialType(String materialName, int data, String displayName) {
        this.type = Material.getMaterial(materialName);
        this.data = data;
        this.displayName = displayName;
    }

    public int getData() {
        return data;
    }

    public Material getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }
}
