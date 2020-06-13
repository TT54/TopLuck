package fr.tt54.topluck.utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Wool;

import java.util.*;

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
        if (this.item.getType() == Material.SKULL_ITEM) {
            SkullMeta meta = (SkullMeta) this.item.getItemMeta();
            meta.setOwningPlayer(player);
            this.item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder addEnchant(String name, int level) {
        name = name.toUpperCase();
        if (Enchants.enchants.containsKey(name)) {
            this.item.addUnsafeEnchantment(Enchants.enchants.get(name).getEnchantment(), level);
            return this;
        }
        if (Enchantment.getByName(name) != null) {
            this.item.addUnsafeEnchantment(Enchantment.getByName(name), level);
            return this;
        }
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        return this.addEnchant(enchantment.getName(), level);
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

    public ItemBuilder setWoolColor(DyeColor dyeColor) {
        if (this.item.getData() instanceof Wool) {
            Wool wool = (Wool) this.item.getData();
            wool.setColor(dyeColor);
            this.item.setData(wool);
        }
        return this;
    }

    public enum Enchants {
        SHARPNESS(Enchantment.DAMAGE_ALL),
        UNBREAKING(Enchantment.DURABILITY),
        SMITE(Enchantment.DAMAGE_UNDEAD),
        POWER(Enchantment.ARROW_DAMAGE),
        INFINITY(Enchantment.ARROW_INFINITE),
        FLAME(Enchantment.ARROW_FIRE),
        BANE_OF_ARTHROPODS(Enchantment.DAMAGE_ARTHROPODS),
        EFFICIENCY(Enchantment.DIG_SPEED),
        FORTUNE(Enchantment.LOOT_BONUS_BLOCKS),
        LOOTING(Enchantment.LOOT_BONUS_MOBS),
        PROTECTION(Enchantment.PROTECTION_ENVIRONMENTAL),
        PUNCH(Enchantment.ARROW_KNOCKBACK),
        CURSE_OF_VANISHING(Enchantment.VANISHING_CURSE),
        CURSE_OF_BINDING(Enchantment.BINDING_CURSE),
        RESPIRATION(Enchantment.OXYGEN),
        FIRE_PROTECTION(Enchantment.PROTECTION_FIRE),
        EXPLOSION_PROTECTION(Enchantment.PROTECTION_EXPLOSIONS),
        PROJECTILE_PROTECTION(Enchantment.PROTECTION_PROJECTILE),
        FEATHER_FALLING(Enchantment.PROTECTION_FALL);

        private Enchantment enchantment;
        public static Map<String, Enchants> enchants = new HashMap<>();

        Enchants(Enchantment enchantment) {
            this.enchantment = enchantment;
        }

        public Enchantment getEnchantment() {
            return this.enchantment;
        }

        static {
            for (Enchants enchant : Enchants.values()) {
                enchants.put(enchant.name(), enchant);
            }
        }
    }

}
