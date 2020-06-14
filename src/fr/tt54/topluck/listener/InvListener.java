package fr.tt54.topluck.listener;

import fr.tt54.topluck.Main;
import fr.tt54.topluck.manager.InvManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class InvListener implements Listener {

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null) {
            if (event.getClickedInventory().getName().contains("§cTopLuck Page §4")) {
                event.setCancelled(true);
                if (event.getCurrentItem().getType() == Material.SKULL_ITEM) {
                    if (Bukkit.getPlayer(event.getCurrentItem().getItemMeta().getDisplayName().substring(2)) != null) {
                        event.getWhoClicked().openInventory(InvManager.getTopLuckPlayerInventory(event.getCurrentItem().getItemMeta().getDisplayName().substring(2)));
                    } else {
                        event.getWhoClicked().openInventory(InvManager.getTopLuckInventory(Integer.parseInt(event.getClickedInventory().getName().split("§4")[1]) - 1));
                        event.getWhoClicked().sendMessage(Main.getMessages().getMessage("notconnected"));
                    }
                } else if (event.getCurrentItem().getType() == Material.PAPER) {
                    if (event.getSlot() == 53) {
                        event.getWhoClicked().openInventory(InvManager.getTopLuckInventory(Integer.parseInt(event.getClickedInventory().getName().split("§4")[1])));
                    } else if (event.getSlot() == 45) {
                        event.getWhoClicked().openInventory(InvManager.getTopLuckInventory(Integer.parseInt(event.getClickedInventory().getName().split("§4")[1]) - 2));
                    }
                }
            } else if (event.getClickedInventory().getName().contains("§cTopLuck ")) {
                event.setCancelled(true);
                if (event.getCurrentItem().getType() == Material.BARRIER && event.getCurrentItem().getItemMeta().getDisplayName().equals(Main.getMessages().getMessage("inventory.quit"))) {
                    event.getWhoClicked().openInventory(InvManager.getTopLuckInventory(0));
                } else if (event.getCurrentItem().getType() == Material.ENDER_PEARL && event.getCurrentItem().getItemMeta().getDisplayName().equals(Main.getMessages().getMessage("inventory.teleport"))) {
                    if (Bukkit.getPlayer(event.getClickedInventory().getName().split(" ")[1].substring(2)) != null) {
                        event.getWhoClicked().closeInventory();
                        event.getWhoClicked().teleport(Bukkit.getPlayer(event.getClickedInventory().getName().split(" ")[1].substring(2)));
                    } else {
                        event.getWhoClicked().openInventory(InvManager.getTopLuckInventory(0));
                        event.getWhoClicked().sendMessage(Main.getMessages().getMessage("notconnected"));
                    }
                } else if (event.getCurrentItem().getType() == Material.CHEST && event.getCurrentItem().getItemMeta().getDisplayName().equals(Main.getMessages().getMessage("inventory.clear"))) {
                    if (Bukkit.getPlayer(event.getClickedInventory().getName().split(" ")[1].substring(2)) != null) {
                        Bukkit.getPlayer(event.getClickedInventory().getName().split(" ")[1].substring(2)).getInventory().setArmorContents(new ItemStack[4]);
                        Bukkit.getPlayer(event.getClickedInventory().getName().split(" ")[1].substring(2)).getInventory().clear();
                        Bukkit.getPlayer(event.getClickedInventory().getName().split(" ")[1].substring(2)).sendMessage(Main.getMessages().getMessage("clear"));
                        event.getWhoClicked().sendMessage(Main.getMessages().getMessage("cleared", "%player%", event.getClickedInventory().getName().split(" ")[1].substring(2)));
                    } else {
                        event.getWhoClicked().openInventory(InvManager.getTopLuckInventory(0));
                        event.getWhoClicked().sendMessage(Main.getMessages().getMessage("notconnected"));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInvDrag(InventoryDragEvent event) {
        if (event.getInventory() != null) {
            if (event.getInventory().getName().equals("§6TopLuck")) {
                for (int i : event.getRawSlots()) {
                    if (i < 9 * 6) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
