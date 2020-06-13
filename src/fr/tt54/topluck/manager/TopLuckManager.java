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
            try {
                int id = Integer.parseInt(matId.split(":")[0]);
                if (Material.getMaterial(id) == null) {
                    System.out.println(Main.getMessages().getMessage("materialnotfound", "%id%", matId));
                } else if (!Material.getMaterial(id).isBlock()) {
                    System.out.println(Main.getMessages().getMessage("notablock", "%id%", matId));
                } else {
                    int meta = 0;
                    try {
                        meta = Integer.parseInt(matId.split(":")[1]);
                    } catch (ArrayIndexOutOfBoundsException ignore) {
                    }
                    MaterialType type = new MaterialType(id, meta);
                    if (!blockCounted.contains(type)) {
                        blockCounted.add(type);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println(Main.getMessages().getMessage("materialnotfound", "%id%", matId));
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
            if (type.getData() == 0) {
                topLuck.set(player.getName() + "." + type.getId() + ":" + type.getData(), player.getStatistic(Statistic.MINE_BLOCK, Material.getMaterial(type.getId())));
            } else {
                topLuck.set(player.getName() + "." + type.getId() + ":" + type.getData(), 0);
            }
        }

        saveTopLuck();
    }

    public static void mine(Player player, int materialId, int data) {
        if (!knowPlayer(player)) {
            resetPlayer(player);
        }
        topLuck.set(player.getName() + "." + materialId + ":" + data, topLuck.getInt(player.getName() + "." + materialId + ":" + data) + 1);
        saveTopLuck();

        Map<String, Integer> map = blockBreakedFromLastConnexion.get(player.getName());
        map.put(materialId + ":" + data, map.getOrDefault(materialId + ":" + data, 0) + 1);
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
            if (materialType.getData() == type.getData() && materialType.getId() == type.getId()) {
                return true;
            }
        }
        return false;
    }

    public static int getResourceMinedFromLastCo(Player player, MaterialType type) {
        if (type.getId() == 1 && type.getData() == 0) {
            return blockBreakedFromLastConnexion.get(player.getName()).getOrDefault("stone", 0);
        }
        return blockBreakedFromLastConnexion.get(player.getName()).getOrDefault(type.getId() + ":" + type.getData(), 0);
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
            total += topLuck.getInt(player.getName() + "." + type.getId() + ":" + type.getData());
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
        return topLuck.getInt(player.getName() + "." + type.getId() + ":" + type.getData());
    }

    public static String getOrePercent(Player player, MaterialType materialType) {
        String str;
        if (Main.getInstance().getConfig().getBoolean("showlastonprofil")) {
            str = getLastOrePercent(player, materialType);
        } else {
            str = getTotalOrePercent(player, materialType);
        }
        return (str != null) ? str.replace(",", ".") : "0";
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
            total += topLuck.getInt(player.getName() + "." + type.getId() + ":" + type.getData());
        }
        return (topLuck.getInt(player.getName() + "." + materialType.getId() + ":" + materialType.getData()) / total * 100 > 0) ? result.format(topLuck.getInt(player.getName() + "." + materialType.getId() + ":" + materialType.getData()) / total * 100).replace(",", ".") : "0.00";
    }
}
