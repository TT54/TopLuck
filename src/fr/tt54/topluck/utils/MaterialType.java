package fr.tt54.topluck.utils;

import org.bukkit.Material;

public class MaterialType {

    private Material type;
    private int data;
    private int displayId;

    public MaterialType(int id, int data) {
        this.type = Material.getMaterial(id);
        this.data = data;
        this.displayId = id;
    }

    public MaterialType(String materialName, int data) {
        this.type = Material.getMaterial(materialName);
        this.data = data;
        this.displayId = this.type.getId();
    }

    public MaterialType(int id, int data, int displayId) {
        this.type = Material.getMaterial(id);
        this.data = data;
        this.displayId = displayId;
    }

    public MaterialType(String materialName, int data, int displayId) {
        this.type = Material.getMaterial(materialName);
        this.data = data;
        this.displayId = displayId;
    }

    public int getData() {
        return data;
    }

    public Material getType() {
        return type;
    }

    public int getDisplayId() {
        return displayId;
    }
}
