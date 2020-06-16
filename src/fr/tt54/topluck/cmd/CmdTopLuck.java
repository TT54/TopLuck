package fr.tt54.topluck.cmd;

import fr.tt54.topluck.Main;
import fr.tt54.topluck.manager.InvManager;
import fr.tt54.topluck.manager.TopLuckManager;
import fr.tt54.topluck.utils.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
            if (args[0].equalsIgnoreCase("reload") && args.length == 1) {
                Main.getInstance().reload();
                System.out.println(Main.getMessages().getMessage("reload"));
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (Permission.hasPermission(player, "topluck.reload")) {
                        player.sendMessage(Main.getMessages().getMessage("reload"));
                    }
                }
                return true;
            }
            sender.sendMessage(Main.getMessages().getMessage("notplayer"));
            return false;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (Permission.hasPermission(sender, "topluck.reload")) {
                    if (args.length != 1) {
                        sender.sendMessage(Main.getMessages().getBadUsageMessage("/" + label + " reload"));
                        return false;
                    }
                    TopLuckManager.saveTopLuck();
                    Main.getInstance().reload();
                    System.out.println(Main.getMessages().getMessage("reload"));
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (Permission.hasPermission(player, "topluck.reload")) {
                            player.sendMessage(Main.getMessages().getMessage("reload"));
                        }
                    }
                    return true;
                } else {
                    sender.sendMessage(Main.getMessages().getMessage("notpermission"));
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("registerblock")) {
                if (!Permission.hasPermission(sender, "topluck.see")) {
                    sender.sendMessage(Main.getMessages().getMessage("notpermission"));
                    return false;
                }

                String displayItemName = ((Player) sender).getInventory().getItemInMainHand().getType().name();
                if (args.length == 2) {
                    if (Material.getMaterial(args[1].toUpperCase()) != null) {
                        displayItemName = args[1].toUpperCase();
                    } else {
                        sender.sendMessage(Main.getMessages().getMessage("materialnotfound", "%id%", args[1]));
                        return false;
                    }
                }

                if (args.length > 2) {
                    sender.sendMessage(Main.getMessages().getBadUsageMessage("/" + label + " " + args[0] + " [DisplayItemID]"));
                    return false;
                }

                if (((Player) sender).getInventory().getItemInMainHand().getType() == Material.AIR) {
                    sender.sendMessage(Main.getMessages().getMessage("notiteminhand"));
                    return false;
                }

                TopLuckManager.addResource(((Player) sender).getItemInHand(), displayItemName);
                sender.sendMessage(Main.getMessages().getMessage("blockregistered", "%type%", ((Player) sender).getItemInHand().getType().name(), "%data%", "" + ((Player) sender).getItemInHand().getData().getData()));

                return true;
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
                    sender.sendMessage(Main.getMessages().getMessage("notpermission"));
                    return false;
                }
            }
        }

        if (Permission.hasPermission(sender, "topluck.see")) {
            ((Player) sender).openInventory(InvManager.getTopLuckInventory(0));
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
            if (Permission.hasPermission(sender, "topluck.reload") && "reload".startsWith(args[0]))
                msg.add("reload");

            if (Permission.hasPermission(sender, "topluck.registerblock") && "registerblock".startsWith(args[0]))
                msg.add("registerblock");

            if (Permission.hasPermission(sender, "topluck.see"))
                msg.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList()));
        }
        return (msg.isEmpty()) ? Collections.emptyList() : msg;
    }
}
