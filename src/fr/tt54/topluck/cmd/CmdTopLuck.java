package fr.tt54.topluck.cmd;

import fr.tt54.topluck.Main;
import fr.tt54.topluck.manager.InvManager;
import fr.tt54.topluck.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CmdTopLuck implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.getMessages().getMessage("notplayer"));
            return false;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload") && args.length == 1) {
                if (Permission.hasPermission(sender, "topluck.reload")) {
                    Main.getInstance().reload();
                    System.out.println(Main.getMessages().getMessage("reload"));
                    return true;
                } else {
                    sender.sendMessage(Main.getMessages().getMessage("notpermission"));
                    return false;
                }
            } else {
                if (Permission.hasPermission(sender, "topluck.see")) {
                    if (args.length != 1) {
                        sender.sendMessage(Main.getMessages().getBadUsageMessage("/" + label + " [joueur]"));
                        return false;
                    }

                    if (Bukkit.getPlayer(args[0]) == null) {
                        sender.sendMessage(Main.getMessages().getMessage("notconnected"));
                        return false;
                    }

                    ((Player) sender).openInventory(InvManager.getTopLuckPlayerInventory(args[0]));
                    sender.sendMessage(Main.getMessages().getMessage("playertopluckopened", "%player%", args[0]));
                    return true;
                } else {
                    sender.sendMessage(Main.getMessages().getBadUsageMessage("/" + label));
                    return false;
                }
            }
        }

        if (Permission.hasPermission(sender, "topluck.see")) {
            ((Player) sender).openInventory(InvManager.getTopLuckInventory());
            sender.sendMessage(Main.getMessages().getMessage("topluckopened"));
            return true;
        } else {
            sender.sendMessage(Main.getMessages().getMessage("notpermission"));
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> msg = new ArrayList<>();
        if (args.length == 1) {
            if (Permission.hasPermission(sender, "topluck.reload"))
                msg.add("reload");

            if (Permission.hasPermission(sender, "topluck.see"))
                msg.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.startsWith(args[0])).collect(Collectors.toList()));
        }
        return (msg.isEmpty()) ? Collections.emptyList() : msg;
    }
}
