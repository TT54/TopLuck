package fr.tt54.topluck.utils;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemBuilder {

    ItemStack item;

    public ItemBuilder(Material material) {
        item = new ItemStack(material);
    }

    public ItemBuilder(ItemStack item) {
        this.item = item;
    }

    public ItemBuilder(String material) {
        item = new ItemStack(Material.getMaterial(material.toUpperCase()));
    }

    public ItemStack getItem() {
        return item;
    }

    public ItemBuilder setName(String name) {
        ItemMeta meta = this.item.getItemMeta();
        meta.setDisplayName(name);
        this.item.setItemMeta(meta);
        return this;
    }


    public ItemBuilder setSkullOf(OfflinePlayer player) {
        if (this.item.getType() == Material.PLAYER_HEAD) {
            SkullMeta meta = (SkullMeta) this.item.getItemMeta();
            meta.setOwner(player.getName());
            this.item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder addLoreLine(String... line) {
        ItemMeta meta = this.item.getItemMeta();
        List<String> lore = (meta.hasLore()) ? meta.getLore() : new ArrayList<>();
        Collections.addAll(lore, line);
        meta.setLore(lore);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeLoreLine(int line) {
        ItemMeta meta = this.item.getItemMeta();
        if (meta.hasLore() && !meta.getLore().isEmpty()) {
            List<String> lore = meta.getLore();
            lore.remove(line);
            meta.setLore(lore);
            this.item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder removeLastLoreLine() {
        if (this.item.getItemMeta().hasLore() && !this.item.getItemMeta().getLore().isEmpty())
            return this.removeLoreLine(this.item.getItemMeta().getLore().size() - 1);
        return this;
    }

    public ItemStack build() {
        return this.item;
    }

    public ItemBuilder resetLore() {
        ItemMeta meta = this.item.getItemMeta();
        meta.setLore(new ArrayList<>());
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder hideEnchants() {
        ItemMeta meta = this.item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        this.item.setItemMeta(meta);
        return this;
    }
}
