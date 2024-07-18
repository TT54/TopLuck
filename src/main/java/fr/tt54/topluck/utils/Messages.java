package fr.tt54.topluck.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Messages {

    private FileConfiguration messages;
    private File messagesFile;
    private String messageNotFound = "messagenotfound";

    public void enable(JavaPlugin javaPlugin, String messageNotFound1) {
        messagesFile = new File(javaPlugin.getDataFolder(), "message.yml");
        saveDefaultLangFile(javaPlugin);
        reloadMessages();
        messageNotFound = (messageNotFound1 == null) ? "messagenotfound" : messageNotFound1;
    }

    public void reloadMessages() {
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getMessage(String name, String... replace) {
        String msg = messages.getString(name);
        if (msg == null) {
            msg = messageNotFound;
            if (msg == null)
                msg = "&cMessage introuvable";
        }
        msg = msg.replace("&", "§");
        for (int i = 0; i < replace.length; i += 2) {
            if (i + 1 <= replace.length) {
                msg = msg.replace(replace[i], replace[i + 1]);
            }
        }
        return msg;
    }

    public String getBadUsageMessage(String usage) {
        return getMessage("badusage", "%usage%", usage);
    }

    public void setMessageNotFound(String message) {
        messageNotFound = message;
    }

    private void saveDefaultLangFile(JavaPlugin javaPlugin) {
        if (!messagesFile.exists()) {
            javaPlugin.saveResource("message.yml", false);
        }
    }
}
