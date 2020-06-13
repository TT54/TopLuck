package fr.tt54.topluck;

import fr.tt54.topluck.cmd.CmdTopLuck;
import fr.tt54.topluck.listener.InvListener;
import fr.tt54.topluck.listener.PlayerListener;
import fr.tt54.topluck.manager.TopLuckManager;
import fr.tt54.topluck.utils.Messages;
import fr.tt54.topluck.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Messages messages;
    private static Main instance;
    private String configVersion = "1.0.0";
    private String messagesVersion = "1.0.0";

    @Override
    public void onEnable() {
        instance = this;
        messages = new Messages();

        reload();

        this.getCommand("topluck").setExecutor(new CmdTopLuck());
        this.getCommand("topluck").setTabCompleter(new CmdTopLuck());

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new InvListener(), this);

        System.out.println(messages.getMessage("pluginenable"));
    }

    @Override
    public void onDisable() {
        TopLuckManager.disable();
        System.out.println(messages.getMessage("plugindisable"));
    }

    public void reload() {
        this.saveDefaultConfig();
        this.reloadConfig();

        messages.enable(this, this.getConfig().getString("messagenotfound"));
        TopLuckManager.enable();

        if (!this.getConfig().getString("configversion").equalsIgnoreCase(configVersion)) {
            System.out.println(messages.getMessage("badconfigversion", "%configversion%", this.getConfig().getString("configversion"), "%newversion%", configVersion));
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Permission.hasPermission(player, "topluck.reload")) {
                    player.sendMessage("");
                    player.sendMessage(messages.getMessage("badconfigversion", "%configversion%", this.getConfig().getString("configversion"), "%newversion%", configVersion));
                }
            }
        }
        if (!messages.getMessage("version").equalsIgnoreCase(messagesVersion)) {
            System.out.println(messages.getMessage("badmessagesversion", "%messagesversion%", messages.getMessage("version"), "%newversion%", messagesVersion));
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Permission.hasPermission(player, "topluck.reload")) {
                    player.sendMessage("");
                    player.sendMessage(messages.getMessage("badmessagesversion", "%messagesversion%", messages.getMessage("version"), "%newversion%", messagesVersion));
                }
            }
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public static Messages getMessages() {
        return messages;
    }

}
