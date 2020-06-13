package fr.tt54.topluck.utils;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class FileManager {

    private static void createFile(String name, JavaPlugin plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        File file = new File(plugin.getDataFolder(), name + ".yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static File getFile(String name, JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), name + ".yml");

        if (!file.exists()) {
            createFile(name, plugin);
        }

        return file;
    }

}
