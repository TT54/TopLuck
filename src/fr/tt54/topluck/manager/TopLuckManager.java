package fr.tt54.topluck.manager;

import fr.tt54.topluck.Main;
import fr.tt54.topluck.utils.FileManager;
import fr.tt54.topluck.utils.MaterialType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TopLuckManager {

    private static FileConfiguration topLuck;
    public static List<MaterialType> blockCounted = new ArrayList<>();
    public static Map<String, Map<String, Integer>> blockBreakedFromLastConnexion = new HashMap<>();

    public static void enable() {
        reloadTopLuck();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!blockBreakedFromLastConnexion.containsKey(player.getName())) {
                blockBreakedFromLastConnexion.put(player.getName(), new HashMap<>());
            }
        }
    }

    public static FileConfiguration getTopLuck() {
        return topLuck;
    }

    public static void reloadTopLuck() {
        topLuck = YamlConfiguration.loadConfiguration(FileManager.getFile("topluck", Main.getInstance()));
        blockCounted = new ArrayList<>();
        for (String matId : Main.getInstance().getConfig().getStringList("materialsverified")) {
            if (Material.getMaterial(matId.split(":")[0].toUpperCase()) != null) {
                String typeName = matId.split(":")[0].toUpperCase();
                int meta = 0;
                try {
                    try {
                        meta = Integer.parseInt(matId.split(":")[1].split(";")[0]);
                    } catch (ArrayIndexOutOfBoundsException ignore) {
                    }
                } catch (NumberFormatException ignore) {
                }
                int displayId = Material.getMaterial(typeName).getId();
                try {
                    try {
                        displayId = Integer.parseInt(matId.split(";")[1]);
                    } catch (ArrayIndexOutOfBoundsException ignore) {
                    }
                } catch (NumberFormatException ignore) {
                }
                System.out.println(displayId);
                MaterialType type = new MaterialType(typeName, meta, displayId);
                if (!containsBlock(type)) {
                    blockCounted.add(type);
                }
            } else {
                try {
                    int id = Integer.parseInt(matId.split(":")[0]);
                    if (Material.getMaterial(id) == null) {
                        System.out.println(Main.getMessages().getMessage("materialnotfound", "%id%", matId));
                    } else {
                        int meta = 0;
                        try {
                            try {
                                meta = Integer.parseInt(matId.split(":")[1].split(";")[0]);
                            } catch (ArrayIndexOutOfBoundsException ignore) {
                            }
                        } catch (NumberFormatException ignore) {
                        }
                        int displayId = id;
                        try {
                            try {
                                displayId = Integer.parseInt(matId.split(";")[1]);
                            } catch (ArrayIndexOutOfBoundsException ignore) {
                            }
                        } catch (NumberFormatException ignore) {
                        }
                        MaterialType type = new MaterialType(id, meta, displayId);
                        if (!containsBlock(type)) {
                            blockCounted.add(type);
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println(Main.getMessages().getMessage("materialnotfound", "%id%", matId));
                }
            }
        }
    }

    public static void saveTopLuck() {
        try {
            topLuck.save(FileManager.getFile("topluck", Main.getInstance()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean knowPlayer(Player player) {
        return topLuck.get(player.getName()) != null;
    }

    public static void resetPlayer(Player player) {
        topLuck.set(player.getName() + ".stonemined", player.getStatistic(Statistic.MINE_BLOCK, Material.STONE));

        for (MaterialType type : blockCounted) {
            topLuck.set(player.getName() + "." + type.getType().name() + ":" + type.getData(), 0);
        }

        saveTopLuck();
    }

    public static void mine(Player player, String materialName, int data) {
        if (!knowPlayer(player)) {
            resetPlayer(player);
        }
        topLuck.set(player.getName() + "." + materialName + ":" + data, topLuck.getInt(player.getName() + "." + materialName + ":" + data) + 1);
        saveTopLuck();

        Map<String, Integer> map = blockBreakedFromLastConnexion.get(player.getName());
        map.put(materialName + ":" + data, map.getOrDefault(materialName + ":" + data, 0) + 1);
    }

    public static void mineStone(Player player) {
        if (!knowPlayer(player)) {
            resetPlayer(player);
        }
        topLuck.set(player.getName() + ".stonemined", topLuck.getInt(player.getName() + ".stonemined") + 1);

        Map<String, Integer> map = blockBreakedFromLastConnexion.get(player.getName());
        map.put("stone", map.getOrDefault("stone", 0) + 1);
    }

    public static boolean containsBlock(MaterialType materialType) {
        for (MaterialType type : blockCounted) {
            if (materialType.getData() == type.getData() && materialType.getType() == type.getType()) {
                return true;
            }
        }
        return false;
    }

    public static int getResourceMinedFromLastCo(Player player, MaterialType type) {
        if (type.getType() == Material.STONE && type.getData() == 0) {
            return blockBreakedFromLastConnexion.get(player.getName()).getOrDefault("stone", 0);
        }
        return blockBreakedFromLastConnexion.get(player.getName()).getOrDefault(type.getType().name() + ":" + type.getData(), 0);
    }

    public static void disable() {
        saveTopLuck();
    }

    public static int getShowedStoneMined(Player player) {
        if (Main.getInstance().getConfig().getBoolean("showlastonprofil")) {
            return getResourceMinedFromLastCo(player, new MaterialType(1, 0));
        }
        return getTotalStoneMined(player);
    }

    public static int getTotalStoneMined(Player player) {
        return topLuck.getInt(player.getName() + ".stonemined");
    }

    public static String getStonePercent(Player player) {
        String str;
        if (Main.getInstance().getConfig().getBoolean("showlastonprofil")) {
            str = getLastStonePercent(player);
        } else {
            str = getTotalStonePercent(player);
        }
        return (str != null) ? str.replace(",", ".") : "0";
    }

    public static String getLastStonePercent(Player player) {
        DecimalFormat result = new DecimalFormat("##.##");
        double total = getResourceMinedFromLastCo(player, new MaterialType(1, 0));
        for (MaterialType type : blockCounted) {
            total += getResourceMinedFromLastCo(player, type);
        }
        return (getResourceMinedFromLastCo(player, new MaterialType(1, 0)) / total * 100 > 0) ? result.format(getResourceMinedFromLastCo(player, new MaterialType(1, 0)) / total * 100) : "0.00";
    }

    public static String getTotalStonePercent(Player player) {
        DecimalFormat result = new DecimalFormat("##.##");
        double total = topLuck.getInt(player.getName() + ".stonemined");
        for (MaterialType type : blockCounted) {
            total += topLuck.getInt(player.getName() + "." + type.getType().name() + ":" + type.getData());
        }
        return (topLuck.getInt(player.getName() + ".stonemined") / total * 100 > 0) ? result.format(topLuck.getInt(player.getName() + ".stonemined") / total * 100) : "0.00";
    }

    public static int getShowedOreMined(Player player, MaterialType type) {
        if (Main.getInstance().getConfig().getBoolean("showlastonprofil")) {
            return getResourceMinedFromLastCo(player, type);
        }
        return getTotalOresMined(player, type);
    }

    public static int getTotalOresMined(Player player, MaterialType type) {
        return topLuck.getInt(player.getName() + "." + type.getType().name() + ":" + type.getData());
    }

    public static String getOrePercent(Player player, MaterialType materialType) {
        if (Main.getInstance().getConfig().getBoolean("showlastonprofil")) {
            return getLastOrePercent(player, materialType);
        } else {
            return getTotalOrePercent(player, materialType);
        }
    }

    public static String getLastOrePercent(Player player, MaterialType materialType) {
        DecimalFormat result = new DecimalFormat("##.##");
        double total = getResourceMinedFromLastCo(player, new MaterialType(1, 0));
        for (MaterialType type : blockCounted) {
            total += getResourceMinedFromLastCo(player, type);
        }
        return (getResourceMinedFromLastCo(player, materialType) / total * 100 > 0) ? result.format(getResourceMinedFromLastCo(player, materialType) / total * 100).replace(",", ".") : "0.00";
    }

    public static String getTotalOrePercent(Player player, MaterialType materialType) {
        DecimalFormat result = new DecimalFormat("##.##");
        double total = topLuck.getInt(player.getName() + ".stonemined");
        for (MaterialType type : blockCounted) {
            total += topLuck.getInt(player.getName() + "." + type.getType().name() + ":" + type.getData());
        }
        return (topLuck.getInt(player.getName() + "." + materialType.getType().name() + ":" + materialType.getData()) / total * 100 > 0) ? result.format(topLuck.getInt(player.getName() + "." + materialType.getType().name() + ":" + materialType.getData()) / total * 100).replace(",", ".") : "0.00";
    }

    public static void addResource(ItemStack item, int itemId) {
        if (!containsBlock(new MaterialType(item.getType().name(), item.getData().getData(), itemId))) {
            blockCounted.add(new MaterialType(item.getType().name(), item.getData().getData(), itemId));
            List<String> blocks = Main.getInstance().getConfig().getStringList("materialsverified");
            blocks.add(item.getType().name() + ":" + item.getData().getData() + ";" + itemId);
            Main.getInstance().getConfig().set("materialsverified", blocks);
            Main.getInstance().saveConfig();
        }
    }
}
