package fr.tt54.topluck.utils;

import org.bukkit.Material;

public class MaterialType {

    private final Material type;
    private final String displayName;

    public MaterialType(String materialName) {
        this.type = Material.getMaterial(materialName);
        this.displayName = this.type.name();
    }

    public MaterialType(String materialName, String displayName) {
        this.type = Material.getMaterial(materialName);
        this.displayName = displayName;
    }

    public Material getType() {
        return type;
    }

    public String getDisplayName() {
        return displayName;
    }
}
