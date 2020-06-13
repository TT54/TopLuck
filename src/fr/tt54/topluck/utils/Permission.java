package fr.tt54.topluck.utils;

import org.bukkit.command.CommandSender;

public class Permission {

    public static boolean hasPermission(CommandSender sender, String permission) {
        if (permission == null)
            return false;
        String parts[] = permission.split("\\.");
        for (int i = 0; i < parts.length - 1; i++) {
            StringBuilder splitPerm = new StringBuilder();
            for (int j = 0; j <= i; j++) {
                splitPerm.append(parts[j]).append(".");
            }
            splitPerm.append("*");
            if (sender.hasPermission(splitPerm.toString()))
                return true;
        }
        if (sender.hasPermission(permission))
            return true;
        return sender.hasPermission("*");
    }
}