package fr.tt54.topluck;

import fr.tt54.topluck.cmd.CmdTopLuck;
import fr.tt54.topluck.listener.InvListener;
import fr.tt54.topluck.listener.PlayerListener;
import fr.tt54.topluck.manager.TopLuckManager;
import fr.tt54.topluck.utils.Messages;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Messages messages;
    private static Main instance;

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
    }

    public static Main getInstance() {
        return instance;
    }

    public static Messages getMessages() {
        return messages;
    }

}
